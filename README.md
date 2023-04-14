# Account Service

<!-- TOC -->
* [Account Service](#account-service)
  * [Description](#description)
    * [Payments](#payments)
    * [Security](#security)
    * [SSl](#ssl)
  * [Endpoints](#endpoints)
    * [Role model](#role-model)
  * [Build and run](#build-and-run)
<!-- TOC -->

## Description

Account Service is a REST API service which helps you to manage your employee payments. With this service, your
employees
can register and see their payment by specified period, or all at once.

### Payments

And you, as an employer and administrator, can grant an Accountant role to your employee, who would manage payments,
with that role user can:

- add payment records to the system
- edit payments if there are mistakes in them

### Security

This project utilizes Spring Security features for managing users' permissions.
By that, users can't reach api methods that they shouldn't be able to reach. For example, a user can only see his
payments, but not the payments of other users, only a user with an Administrator role can lock and unlock other users,
and so on. And, of course, all users' passwords hashed before saving in the database.

Another security feature that Account Service has logging various security events, such as user register, user login,
adding payments, and so on. It saves the events to the database.
Also, you can grant an Auditor role for user — it brings him an opportunity to check all security events that happen in
Account Service system.

Account Service also has a logging-failure system that can detect brute force attack on a user account and automatically
block that account for further inspection and unlocking by administrator.

### SSl

For further security enhancement, this project uses HTTPS protocol by default.
You should only add your ssl certificate,
and encryption will apply to all requests to service and responses from service.

---

This project is written in Java 17. It uses [Spring Boot](https://spring.io/projects/spring-boot#overview) framework,
[Spring Security](https://docs.spring.io/spring-security/reference/index.html) framework,
and [H2](https://www.h2database.com/html/main.html) as a database.
It also has [OpenAPI 3.0](https://swagger.io/specification/v3/) documentation.

I used [Postman](https://www.postman.com/) for testing web pages and REST API endpoints.

This project is a part
of [JetBrains Spring Security for Java Backend Developers course](https://hyperskill.org/tracks/38?category=2).

## Endpoints

This project has OpenAPI 3.0 documentation in .yaml and .html format which includes information about all api
endpoints —
you can check it [here](https://app.swaggerhub.com/apis-docs/EXNESKORO/account-service_api/1.0.0) or by going
to server page by "**/api/doc**" path.

### Role model

In this table, you can see which role have access to which path.

|                           | Anonymous | User | Accountant | Administrator | Auditor |
|---------------------------|-----------|------|------------|---------------|---------|
| GET api/doc               | +         | +    | +          | +             | +       |
| POST api/auth/signup      | +         | +    | +          | +             | +       |
| POST api/auth/changepass  |           | +    | +          | +             | -       |
| GET api/empl/payment      | -         | +    | +          | -             | -       |
| POST api/acct/payments    | -         | -    | +          | -             | -       |
| PUT api/acct/payments     | -         | -    | +          | -             | -       |
| GET api/admin/user        | -         | -    | -          | +             | -       |
| DELETE api/admin/user     | -         | -    | -          | +             | -       |
| PUT api/admin/user/role   | -         | -    | -          | +             | -       |
| PUT api/admin/user/access | -         | -    | -          | +             | -       |
| GET api/security/events   | -         | -    | -          | -             | +       |

## Build and run

- Clone repo and go to project directory

```shell
git clone https://github.com/ex-neskoro/AccountService.git 
```

- Add your ssl certificate **by name service.p12** to the path AccountService/src/main/resources/ssl/service.p12

- You can now start the app by command

```shell
./gradlew bootRun
```

- Or you can create .jar first

```shell
./gradlew bootJar
java -jar AccountService/build/libs/AccountService-1.0.jar
```
