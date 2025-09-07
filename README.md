# Spring MVC Non-Boot Application

Проект представляет собой веб-приложение на Spring MVC без использования Spring Boot, с интеграцией JPA, Hibernate и Spring Security с JWT аутентификацией.

## Технологический стек

- **Java 17**
- **Spring MVC 6.2.10**
- **Spring Security 6.5.2**
- **JPA/Hibernate 7.1.0**
- **JWT аутентификация**
- **MySQL Database 8.0.43**
- **Liquibase** (миграции базы данных)
- **Apache Tomcat 10.1.43** (контейнер сервлетов)
- **Maven 3.8.7** (сборка проекта)

## Функциональность

### Аутентификация и авторизация
- JWT аутентификация с access/refresh токенами
- Ролевая модель (USER, ADMIN)
- Защищенные эндпоинты с проверкой прав доступа

### Управление пользователями
- Регистрация новых пользователей
- Просмотр списка пользователей
- Удаление пользователей (только для ADMIN)

### Управление банковскими картами
- Создание новых карт (ADMIN)
- Просмотр карт (с маскировкой номеров для пользователей)
- Блокировка/активация карт (ADMIN)
- Переводы между картами
- Пагинация и фильтрация списка карт

## Настройка базы данных

Создайте базу данных MySQL и обновите параметры подключения в src/main/resources/application.properties:

```properties
database.driver=com.mysql.cj.jdbc.Driver
database.url=jdbc:mysql://localhost:3306/your_database_name
database.username=your_username
database.password=your_password
```

## Сборка проекта

При помощи команды `mvn package` происходит сборка проекта в war файл.

Далее этот war файл необходимо переместить в папку webapps контейнера сервлетов Tomcat и запустить его.

Приложение будет находиться по адресу:
```
http://localhost:8080/spring-project/
```