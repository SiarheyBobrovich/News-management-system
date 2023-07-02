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

| method | url                 | params                                                                        | description             | request body                                                                          | response body                                                                               |
|--------|---------------------|-------------------------------------------------------------------------------|-------------------------|---------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------|
| GET    | /api/v1/news/{{ID}} | <pre>id - long</pre>                                                          | Get a News by ID        | -                                                                                     | [view_news_01.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fview_news_01.json)               |
| GET    | /api/v1/news        | <pre>page - int<br/>size - int<br/>sort - string(title, text, id, time)</pre> | Get News page           | -                                                                                     | [page_resp_news_view.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fpage_resp_news_view.json) |
| GET    | /api/v1/news/filter | <pre>page - int<br/>size - int<br/>part - part of text/title</pre>            | Get News page by filter | -                                                                                     | [page_resp_news_view.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fpage_resp_news_view.json) |
| POST   | /api/v1/news        |                                                                               | Create new News         | [post_dto_news_01.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fpost_dto_news_01.json) | [view_news_01.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fview_news_01.json)               |
| PUT    | /api/v1/news/{{ID}} | <pre>id - long</pre>                                                          | Update existing News    | [put_dto_news_01.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fput_dto_news_01.json)   | [view_news_01.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fview_news_01.json)               |
| DELETE | /api/v1/news/{{ID}} | <pre>id - long</pre>                                                          | Delete News by ID       | -                                                                                     | -                                                                                           |

### Comments:

- port: 8080

| method | url                          | params                                                                 | description                  | request body                                                                                    | response body                                                                                     |
|--------|------------------------------|------------------------------------------------------------------------|------------------------------|-------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| GET    | /api/v1/comments/{{ID}}      | <pre>id - long</pre>                                                   | Get a Comment by ID          | -                                                                                               | [resp_comment_news_01.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fresp_comment_news_01.json)     |
| GET    | /api/v1/comments             | <pre>page - int<br/>size - int<br/>sort - string(text, id, time)</pre> | Get Comments page            | -                                                                                               | [page_resp_comment_news.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fpage_resp_comment_news.json) |
| GET    | /api/v1/comments/news/{{ID}} | <pre>page - int<br/>size - int<br/>sort - string(text, id, time)</pre> | Get Comments page by news ID | -                                                                                               | [page_resp_comment.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fpage_resp_comment.json)           |
| GET    | /api/v1/comments/filter      | <pre>page - int<br/>size - int<br/>part - part of text</pre>           | Get Comments page by filter  | -                                                                                               | [page_resp_comment_news.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fpage_resp_comment_news.json) |
| POST   | /api/v1/comments             |                                                                        | Create a new Comment         | [create_comment_dto_01.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fcreate_comment_dto_01.json) | [resp_comment_news_01.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fresp_comment_news_01.json)     |
| PUT    | /api/v1/comments/{{ID}}      | <pre>id - long</pre>                                                   | Update an existing Comment   | [update_comment_dto.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fupdate_comment_dto.json)       | [resp_comment_news_01.json](news%2Fsrc%2Ftest%2Fresources%2Fdata%2Fresp_comment_news_01.json)     |
| DELETE | /api/v1/comments/{{ID}}      | <pre>id - long</pre>                                                   | Delete a Comment by ID       | -                                                                                               | -                                                                                                 |

### USERS:

- port: 8081

| method | url                 | description                       | request params / body                                                                     | response body                                                                                 |
|--------|---------------------|-----------------------------------|-------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------|
| POST   | /api/v1/users/login | Log in with username and password | <pre>login - string<br/>password - string</pre>                                           | JWT token                                                                                     |
| POST   | /api/v1/users/info  | Get user information              | <pre>header: authorization</pre>                                                          | [response_user_dto_01.json](user%2Fsrc%2Ftest%2Fresources%2Fdata%2Fresponse_user_dto_01.json) |
| POST   | /api/v1/users/      | Register new user                 | [create_user_dto_01.json](user%2Fsrc%2Ftest%2Fresources%2Fdata%2Fcreate_user_dto_01.json) | [response_user_dto_01.json](user%2Fsrc%2Ftest%2Fresources%2Fdata%2Fresponse_user_dto_01.json) |
