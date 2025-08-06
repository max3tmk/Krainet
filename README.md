# Krainet Project

Два микросервиса: `auth-service` и `notification-service`.

---

## Требования

- Docker и Docker Compose
- Java 17+
- Maven

---

## Запуск

1. Клонировать репозиторий:

    git clone <repo-url>

    cd krainet

2. Собрать и запустить контейнеры с помощью Docker Compose:

    docker compose up --build

3. Сервисы будут доступны:

   · Auth-service: http://localhost:8080

   · Notification-service: http://localhost:8090

## Описание сервисов
### Auth-service

· REST API для регистрации, авторизации, CRUD пользователей

· Авторизация JWT, роли USER и ADMIN

· При создании/обновлении/удалении пользователя с ролью USER отправляется уведомление всем пользователям с ролью ADMIN через notification-service

### Notification-service

· Принимает события от auth-service

· Отправляет email уведомления ADMIN пользователям

· Сохраняет логи отправленных сообщений в БД

## Конфигурация

· JWT секрет и параметры в application.yml

· URL notification-service в application.yml auth-service

· БД Postgres с миграциями Flyway в папке resources/db/migration

## Тестовые данные
В БД по умолчанию созданы:

· ADMIN пользователи:

 `admin1` / `adminpass1`

 `admin2` / `adminpass2`

 `admin3` / `adminpass3`

 `admin4` / `adminpass4`

· USER пользователи:

 `user1` / `password1`

 `user2` / `password2`

 `user3` / `password3`

 `user4` / `password4`

## Использование
· Регистрация нового пользователя: POST /register (роль USER)

· Админ может просматривать, обновлять и удалять пользователей через эндпоинты /users/**

· Пользователь может просматривать и изменять только свои данные

## Postman коллекция

Для удобного тестирования всех доступных API-эндпоинтов проекта доступна готовая Postman коллекция:
[Скачать коллекцию (Krainet.postman_collection.json)](./Krainet.postman_collection.json)

Коллекция включает следующие группы запросов:

· 🔐 Аутентификация: регистрация, логин

· 👤 Пользователи: просмотр, обновление, удаление, получение информации о себе

· 📧 Уведомления: отправка email-сообщений (при регистрации, изменении и удалении)

⚠️ Перед использованием установите переменные окружения:

· base_url — например, http://localhost:8080

· jwt_token — будет автоматически сохранён после авторизации и использован в приватных запросах

## Логирование
· Вся активность логируется с уровня INFO и выше

· Ошибки логируются с подробным сообщением

## Разработка
· Maven для сборки

· Java 17

· Spring Boot 3.x

· Spring Security, JWT, Hibernate, Flyway

· Docker и docker-compose для локального запуска
