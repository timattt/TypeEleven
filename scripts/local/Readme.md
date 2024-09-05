## Запуск инфраструктуры

### Локальный тест

```bash
docker compose --profile local -p type-11-infrastructure up --build
```

### Тест в докере

```bash
docker compose --profile dockerized -p type-11-dockerized up --build
```