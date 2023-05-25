# NEWS MANAGEMENT SYSTEM

## Build
1) logging-starter:build
2) exception-handling-starter:build
3) config:build
4) news:build
5) user:build

## RUN

- docker compose up

## Swagger:

- News: http://localhost:8080/swagger-ui/index.html#/
- Users: http://localhost:8081/swagger-ui/index.html#/

## Database:

- News: localhost:5432/clevertec
- Users: localhost:5433/clevertec

## Endpoints:

### NEWS:

- port: 8080

| method | url                 | params                                                                        | description             |
|--------|---------------------|-------------------------------------------------------------------------------|-------------------------|
| GET    | /api/v1/news/{{ID}} | <pre>id - long</pre>                                                          | Get a News by ID        |
| GET    | /api/v1/news        | <pre>page - int<br/>size - int<br/>sort - string(title, text, id, time)</pre> | Get News page           |
| GET    | /api/v1/news/filter | <pre>page - int<br/>size - int<br/>part - part of text/title</pre>            | Get News page by filter |
| POST   | /api/v1/news        |                                                                               | Create new News         |
| PUT    | /api/v1/news/{{ID}} | <pre>id - long</pre>                                                          | Update existing News    |
| DELETE | /api/v1/news/{{ID}} | <pre>id - long</pre>                                                          | Delete News by ID       |

### Comments:

- port: 8080

| method | url                          | params                                                                 | description                  |
|--------|------------------------------|------------------------------------------------------------------------|------------------------------|
| GET    | /api/v1/comments/{{ID}}      | <pre>id - long</pre>                                                   | Get a Comment by ID          |
| GET    | /api/v1/comments             | <pre>page - int<br/>size - int<br/>sort - string(text, id, time)</pre> | Get Comments page            |
| GET    | /api/v1/comments/news/{{ID}} | <pre>page - int<br/>size - int<br/>sort - string(text, id, time)</pre> | Get Comments page by news ID |
| GET    | /api/v1/comments/filter      | <pre>page - int<br/>size - int<br/>part - part of text</pre>           | Get Comments page by filter  |
| POST   | /api/v1/comments             |                                                                        | Create a new Comment         |
| PUT    | /api/v1/comments/{{ID}}      | <pre>id - long</pre>                                                   | Update an existing Comment   |
| DELETE | /api/v1/comments/{{ID}}      | <pre>id - long</pre>                                                   | Delete a Comment by ID       |

### USERS:

- port: 8081

| method | url                 | description                       |
|--------|---------------------|-----------------------------------|
| POST   | /api/v1/users/login | Log in with username and password |
| POST   | /api/v1/users/info  | Get user information              |
| POST   | /api/v1/users/      | Register new user                 |
