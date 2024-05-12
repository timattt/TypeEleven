## Как дебажить и запускать локально

* Устанавливаем [Docker](https://www.docker.com/products/docker-desktop/)
* Устанавливаем [Idea](https://www.jetbrains.com/ru-ru/idea/)
* Устанавливаем [JDK Temurin](https://adoptium.net/temurin/releases/)
* В терминале переходим в папку ```scripts/debug``` и выполняем команду ```docker compose up```
* После этого в докере соберется проект и запустится вместе с БД и envoy