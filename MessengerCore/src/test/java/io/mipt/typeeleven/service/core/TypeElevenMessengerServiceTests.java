package io.mipt.typeeleven.service.core;

import io.mipt.typeeleven.BaseTest;
import io.mipt.typeeleven.domain.model.Chat;
import io.mipt.typeeleven.service.impl.dao.repository.ChatRepository;
import io.mipt.typeeleven.service.impl.dao.repository.MessageRepository;
import io.mipt.typeeleven.service.impl.dao.repository.UserRepository;
import io.mipt.typesix.businesslogic.domain.model.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@TestPropertySource(properties = {
        "grpc.port=9091"
})
public class TypeElevenMessengerServiceTests extends BaseTest {
    private static final String USER1_EMAIL = "a@gmail.com";
    private static final String USER1_FIRST_NAME = "a";
    private static final String USER1_LAST_NAME = "aa";

    private static final String USER2_EMAIL = "b@gmail.com";
    private static final String USER2_FIRST_NAME = "b";
    private static final String USER2_LAST_NAME = "bb";

    private static final String USER3_EMAIL = "c@gmail.com";
    private static final String USER3_FIRST_NAME = "c";
    private static final String USER3_LAST_NAME = "cc";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private TypeElevenMessengerService typeElevenMessengerService;

    private User user1;
    private User user2;
    private User user3;

    @Container
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.url", postgres::getJdbcUrl);
        registry.add("spring.liquibase.user", postgres::getUsername);
        registry.add("spring.liquibase.password", postgres::getPassword);

        registry.add("spring.r2dbc.url", () -> postgres.getJdbcUrl().replace("jdbc", "r2dbc"));
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

    @BeforeEach
    @Transactional
    public void prepareTest() {
        userRepository.insertUser(USER1_EMAIL, USER1_FIRST_NAME, USER1_LAST_NAME).block();
        userRepository.insertUser(USER2_EMAIL, USER2_FIRST_NAME, USER2_LAST_NAME).block();
        userRepository.insertUser(USER3_EMAIL, USER3_FIRST_NAME, USER3_LAST_NAME).block();

        user1 = userRepository.selectByEmail(USER1_EMAIL).block();
        user2 = userRepository.selectByEmail(USER2_EMAIL).block();
        user3 = userRepository.selectByEmail(USER3_EMAIL).block();
    }

    @AfterEach
    @Transactional
    public void cleanupTest() {
        userRepository.deleteAll().block();
        messageRepository.deleteAll().block();
        chatRepository.deleteAll().block();
        chatRepository.deleteAllFromActiveUsers().block();
    }

    @Test
    public void listAvailableUsersTest() {
        var users = typeElevenMessengerService.listAvailableUsers().collectList().block();

        Assertions.assertEquals(users, List.of(user1, user2, user3));
    }

    @Test
    public void listChatsTest() {
        Chat chat = typeElevenMessengerService.createChat(List.of(user1.getId(), user2.getId())).block();

        Assertions.assertNotNull(chat);
        Assertions.assertNotNull(chat.getActiveUsers());
        Assertions.assertFalse(chat.getActiveUsers().isEmpty());

        var chats = typeElevenMessengerService.listChats(user1.getId()).collectList().block();

        Assertions.assertEquals(List.of(chat), chats);

        chats = typeElevenMessengerService.listChats(user3.getId()).collectList().block();

        Assertions.assertEquals(chats, List.of());
    }

    @Test
    public void newMessageTest() {
        Chat chat = typeElevenMessengerService.createChat(List.of(user1.getId(), user2.getId())).block();

        Assertions.assertNotNull(chat);

        var message1 = typeElevenMessengerService.newMessage(user1.getId(), chat.getId(), "biba").block();
        var message2 = typeElevenMessengerService.newMessage(user1.getId(), chat.getId(), "biba").block();

        Assertions.assertNotNull(message1);
        Assertions.assertNotNull(message2);
    }

    @Test
    public void listMessagesTest() throws InterruptedException {
        Chat chat = typeElevenMessengerService.createChat(List.of(user1.getId(), user2.getId())).block();

        Assertions.assertNotNull(chat);

        final int total = 10;

        for (int i = 0; i < total; i++) {
            typeElevenMessengerService.newMessage(user1.getId(), chat.getId(), "biba " + i).block();
            Thread.sleep(30);
        }

        final int chunk = 2;

        var received = typeElevenMessengerService.listMessages(chat.getId(), System.currentTimeMillis(), chunk).collectList().block();

        Assertions.assertNotNull(received);
        Assertions.assertEquals(received.size(), chunk);

        for (int i = 0; i < chunk; i++) {
            Assertions.assertEquals(received.get(i).getContent(), "biba " + (total - i - 1));
        }
    }

    @Test
    public void createChatTest() {
        var users = List.of(user1.getId(), user2.getId(), user3.getId());
        Chat chat = typeElevenMessengerService.createChat(users).block();

        Assertions.assertNotNull(chat);
        Assertions.assertEquals(chat.getActiveUsers(), users);
    }

    @Test
    public void listActiveUsersForMessageChatTest() {
        var users = List.of(user1.getId(), user2.getId(), user3.getId());
        Chat chat = typeElevenMessengerService.createChat(users).block();

        Assertions.assertNotNull(chat);

        var message = typeElevenMessengerService.newMessage(user1.getId(), chat.getId(), "biba").block();

        Assertions.assertNotNull(message);

        var actives = typeElevenMessengerService.listActiveUsersForMessageChat(message.getId()).collectList().block();

        Assertions.assertEquals(users, actives);
    }
}
