FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY . /app/.
RUN mvn -f /app/pom.xml clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/TypeElevenStarter/target/*.jar /app/*.jar

RUN mkdir -p /root/.postgresql
RUN wget "https://storage.yandexcloud.net/cloud-certs/CA.pem" --output-document /root/.postgresql/root.crt
RUN chmod 777 /root/.postgresql/root.crt

ENTRYPOINT java -jar /app/*.jar
