# Spring MVC Non-Boot Banking Cards API

Java backend-приложение для управления банковскими картами, реализованное на Spring MVC без использования Spring Boot.

Проект включает JWT-аутентификацию, ролевой доступ, работу с пользователями и банковскими картами, переводы между картами, фильтрацию, пагинацию, миграции базы данных через Liquibase и деплой в servlet container Apache Tomcat.

## О проекте

Приложение моделирует backend-сервис для управления банковскими картами.

В системе есть два типа пользователей:

- `USER` — обычный пользователь;
- `ADMIN` — администратор.

Обычный пользователь может просматривать свои карты, видеть маскированные или полные номера своих карт и выполнять переводы между собственными картами.

Администратор может управлять пользователями и картами: просматривать список пользователей, создавать карты, блокировать, активировать и удалять карты.

Главная особенность проекта — реализация backend-приложения на Spring Framework без Spring Boot и автоконфигурации. Конфигурация Spring MVC, Spring Security, JPA/Hibernate, Liquibase, DispatcherServlet и деплой в Tomcat выполняются вручную через Java Config и Maven WAR-сборку.

## Функциональность

### Аутентификация и безопасность

- Регистрация пользователей.
- Аутентификация через JWT.
- Поддержка access token и refresh token.
- Ролевая модель доступа `USER` / `ADMIN`.
- Защита endpoint’ов через Spring Security.
- Stateless security-конфигурация.
- Хеширование паролей через BCrypt.
- Передача JWT через заголовок `Authorization: Bearer your-access-token`.

### Работа с пользователями

- Регистрация нового пользователя.
- Получение списка всех пользователей.
- Удаление пользователя.
- Разделение доступа между обычным пользователем и администратором.

### Работа с банковскими картами

- Создание банковской карты администратором.
- Просмотр карт пользователя.
- Просмотр карт с маскированными номерами.
- Просмотр карт с полными номерами.
- Блокировка карты.
- Активация карты.
- Удаление карты.
- Фильтрация и пагинация списка карт.
- Перевод средств между картами.

### Работа с базой данных

- Использование MySQL.
- Работа с сущностями через Hibernate / Spring Data JPA.
- Миграции схемы базы данных через Liquibase.
- Валидация схемы через Hibernate.
- Использование DTO для разделения внутренней модели приложения и данных API.

## Tech Stack

**Language:** Java 17

**Backend:** Spring MVC 6.2.10, Spring Web, Spring Security 6.5.2

**Persistence:** Spring Data JPA, JPA, Hibernate 7.1.0

**Database:** MySQL 8.0.43, Liquibase

**Security:** JWT, Spring Security, BCrypt

**Validation:** Jakarta Validation, Hibernate Validator

**Build:** Maven

**Deployment:** WAR, Apache Tomcat 10.1.43

**Testing:** JUnit 5, Mockito, Spring Test

**Logging:** SLF4J, Logback

## Особенность проекта

Проект реализован без Spring Boot.

Это значит, что конфигурация приложения задаётся вручную:

- `Application` наследуется от `AbstractAnnotationConfigDispatcherServletInitializer` и настраивает запуск Spring MVC-приложения в servlet container;
- `AppConfig` объединяет основные конфигурационные классы;
- `WebConfig` отвечает за web-слой и настройку Spring MVC;
- `DBConfig` настраивает DataSource, Liquibase, EntityManagerFactory и TransactionManager;
- `SecurityConfig` настраивает Spring Security, JWT-фильтр, роли и правила доступа;
- приложение собирается в WAR-файл и запускается через Apache Tomcat.

Такой подход позволяет лучше понять, какие части Spring Boot обычно настраивает автоматически: DispatcherServlet, ApplicationContext, SecurityFilterChain, JPA/Hibernate, транзакции, Liquibase и деплой приложения.

## Структура проекта

```text
src/main/java/com/app
├── annotation          # Кастомные аннотации
├── aspect              # AOP-логика
├── config              # Конфигурация Spring MVC, Security, JPA, Liquibase
├── controller          # REST-контроллеры
├── dto                 # DTO-классы для запросов и ответов API
├── exception           # Кастомные исключения
├── exceptionHandler    # Обработка исключений
├── model               # JPA-сущности
├── repository          # Spring Data JPA repositories
├── security            # JWT-фильтр, UserDetailsService, security-модели
├── service             # Бизнес-логика приложения
└── validator           # Валидация данных
```

```text
src/main/resources
├── db/changelog        # Liquibase changelog-файлы
└── logback.xml         # Конфигурация логирования
```

```text
docs
└── openapi.yaml        # OpenAPI-спецификация
```

## API Endpoints

### Authentication

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/auth/sing-in` | Вход в систему | Public |
| `POST` | `/auth/refresh` | Обновление access token | Public |

### Registration

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/register` | Регистрация нового пользователя | Public |

### Cards — User

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/card/show` | Получить свои карты с маскированными номерами | USER |
| `POST` | `/card/show-full-number` | Получить свои карты с полными номерами | USER |
| `POST` | `/card/transfer` | Перевод между картами | USER |

### Cards — Admin

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/card/all` | Получить все карты с фильтрацией и пагинацией | ADMIN |
| `POST` | `/card/add` | Создать карту | ADMIN |
| `POST` | `/card/block` | Заблокировать карту | ADMIN |
| `POST` | `/card/activate` | Активировать карту | ADMIN |
| `POST` | `/card/delete` | Удалить карту | ADMIN |

### Users — Admin

| Method | Endpoint | Description | Access |
|---|---|---|---|
| `GET` | `/user/all` | Получить список всех пользователей | ADMIN |
| `POST` | `/user/delete` | Удалить пользователя | ADMIN |

## Настройка базы данных

Для запуска проекта необходимо создать базу данных MySQL и указать параметры подключения в файле:

```text
src/main/resources/application.properties
```

Пример конфигурации:

```properties
database.driver=com.mysql.cj.jdbc.Driver
database.url=jdbc:mysql://localhost:3306/your_database_name
database.username=your_username
database.password=your_password
```

Liquibase применит миграции базы данных из директории:

```text
src/main/resources/db/changelog
```

## Сборка и запуск через Tomcat

### Требования

Для запуска проекта нужны:

- Java 17;
- Maven;
- MySQL;
- Apache Tomcat 10.1.43.

### Сборка WAR-файла

Собрать проект можно командой:

```bash
mvn clean package
```

После сборки Maven создаст WAR-файл в директории:

```text
target/
```

### Деплой в Tomcat

Скопируйте собранный WAR-файл в директорию `webapps` вашего Tomcat.

Пример:

```bash
cp target/spring-project.war /path/to/tomcat/webapps/
```

После этого запустите Tomcat:

```bash
/path/to/tomcat/bin/startup.sh
```

На Windows:

```bat
C:\path\to\tomcat\bin\startup.bat
```

После запуска приложение будет доступно по адресу:

```text
http://localhost:8080/spring-project/
```

## Примеры запросов

### Авторизация

```bash
curl -X POST "http://localhost:8080/spring-project/auth/sing-in" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password"
  }'
```

Пример ответа:

```json
{
  "token": "access-token-example",
  "refreshToken": "refresh-token-example"
}
```

### Обновление access token

```bash
curl -X POST "http://localhost:8080/spring-project/auth/refresh" \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "your-refresh-token"
  }'
```

### Регистрация пользователя

```bash
curl -X POST "http://localhost:8080/spring-project/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "new_user",
    "password": "password"
  }'
```

### Просмотр своих карт

```bash
curl -X POST "http://localhost:8080/spring-project/card/show" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-access-token" \
  -d '{
    "page": 0,
    "size": 10,
    "sortBy": "id",
    "directionSort": "ASC"
  }'
```

### Перевод между картами

```bash
curl -X POST "http://localhost:8080/spring-project/card/transfer" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-access-token" \
  -d '{
    "firstCardNumber": "1111222233334444",
    "secondCardNumber": "5555666677778888",
    "amountTransferBetweenCards": 100
  }'
```

### Получение всех карт администратором

```bash
curl -X GET "http://localhost:8080/spring-project/card/all" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer admin-access-token" \
  -d '{
    "page": 0,
    "size": 10,
    "sortBy": "id",
    "directionSort": "ASC"
  }'
```

### Создание карты администратором

```bash
curl -X POST "http://localhost:8080/spring-project/card/add" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer admin-access-token" \
  -d '{
    "cardNumber": "1111222233334444",
    "ownerUsername": "user",
    "validityPeriod": "2028-12-31",
    "balance": 1000
  }'
```

## OpenAPI

OpenAPI-спецификация находится в директории:

```text
docs/openapi.yaml
```

Её можно открыть в Swagger Editor или другом инструменте для просмотра OpenAPI-документации.

## Тестирование

Для запуска тестов:

```bash
mvn test
```

В проекте используются:

- JUnit 5;
- Mockito;
- Spring Test.

Тесты находятся в директории:

```text
src/test/java/com/app
```
