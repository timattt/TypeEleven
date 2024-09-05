package io.mipt.typeeleven.dao;

import io.mipt.typeeleven.core.TypeElevenCoreConfig;
import io.mipt.typeeleven.core.domain.model.Chat;
import io.mipt.typeeleven.core.service.api.TypeElevenMessengerService;
import io.mipt.typeeleven.core.service.impl.TypeElevenMessengerServiceImpl;
import io.mipt.typeeleven.dao.service.impl.ChatDao;
import io.mipt.typeeleven.dao.service.impl.MessageDao;
import io.mipt.typeeleven.dao.service.impl.UserDao;
import io.mipt.typeeleven.dao.service.impl.repository.ChatRepository;
import io.mipt.typeeleven.dao.service.impl.repository.MessageRepository;
import io.mipt.typeeleven.dao.service.impl.repository.UserRepository;
import io.mipt.typesix.businesslogic.domain.model.User;
import lombok.extern.java.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log
@SpringBootTest(classes = {
        TypeElevenMessengerServiceImpl.class,
        UserRepository.class,
        MessageRepository.class,
        ChatRepository.class,
        ChatDao.class,
        UserDao.class,
        MessageDao.class,
        TypeElevenDaoConfig.class,
        TypeElevenCoreConfig.class,
        R2dbcAutoConfiguration.class,
        R2dbcRepositoriesAutoConfiguration.class,
        R2dbcDataAutoConfiguration.class,
        R2dbcTransactionManagerAutoConfiguration.class,
        LiquibaseAutoConfiguration.class
})
@EnableR2dbcRepositories
@TestPropertySource(properties = {
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost",
        "spring.main.allow-bean-definition-overriding=true",
        "spring.profiles.active=test",
        // H2 DATA BASE
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.r2dbc.url=r2dbc:h2:mem:///~/db/postgres;MODE=PostgreSQL",
        "spring.r2dbc.username=tmp_user",
        "spring.r2dbc.password=tmp_password",
        "logging.level.org.springframework.data.r2dbc=DEBUG",
        "spring.r2dbc.properties.ssl=false",
        // LIQUIBASE
        "spring.liquibase.url=jdbc:h2:mem:~/db/postgres;DB_CLOSE_DELAY=-1",
        "spring.liquibase.user=tmp_user",
        "spring.liquibase.password=tmp_password",
})
public class TypeElevenMessengerServiceTests {
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
        Assertions.assertTrue(message1.getTime() <= System.currentTimeMillis());
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
