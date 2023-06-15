package ru.clevertec.news.controller.open_api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import ru.clevertec.exception.handling.exception.ExceptionMessage;
import ru.clevertec.news.data.CreateCommentDto;
import ru.clevertec.news.data.Filter;
import ru.clevertec.news.data.ResponseComment;
import ru.clevertec.news.data.ResponseCommentNews;
import ru.clevertec.news.data.UpdateCommentDto;

@Tag(name = "Comment", description = "API for working with comments")
public interface CommentOpenApi {

    @Operation(
            method = "GET",
            tags = "Comment",
            description = "Get a comment by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseCommentNews.class),
                                    examples = @ExampleObject("""
                                            {
                                               "id": 1,
                                               "time": "2023-05-01T01:01:01.1",
                                               "text": "Что-то не сходится, аппарат в последний раз отправил показания, когда по данным телеметрии до поверхности оставалось несколько метров.",
                                               "username": "user1",
                                               "newsId": 1
                                             }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionMessage.class),
                                    examples = @ExampleObject("""
                                            {
                                              "status": 404,
                                              "message": "Comment 1 not found."
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<ResponseCommentNews> getById(@Parameter(example = "1") Long id);

    @Operation(
            method = "GET",
            tags = "Comment",
            description = "Get page of comments",
            parameters = {
                    @Parameter(name = "page", description = "Comments page", example = "0"),
                    @Parameter(name = "size", description = "Page size", example = "2"),
                    @Parameter(name = "sort", description = "Sorting by field", example = "id")
            },
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageResponseCommentsNews.class),
                            examples = @ExampleObject("""
                                    {
                                        "content": [
                                            {
                                                "id": 1,
                                                "time": "2023-05-01T01:01:01.1",
                                                "text": "Что-то не сходится, аппарат в последний раз отправил показания, когда по данным телеметрии до поверхности оставалось несколько метров.",
                                                "username": "user1",
                                                "newsId": 1
                                            },
                                            {
                                                "id": 2,
                                                "time": "2023-05-01T01:02:01.1",
                                                "text": "Всё сходится. Компьютер решил что сканер высоты глючит и садился по другим данным/датчикам.",
                                                "username": "user2",
                                                "newsId": 1
                                            }
                                        ],
                                        "pageable": {
                                            "sort": [
                                                {
                                                    "direction": "ASC",
                                                    "property": "id",
                                                    "ignoreCase": false,
                                                    "nullHandling": "NATIVE",
                                                    "ascending": true,
                                                    "descending": false
                                                }
                                            ],
                                            "offset": 0,
                                            "pageNumber": 0,
                                            "pageSize": 2,
                                            "paged": true,
                                            "unpaged": false
                                        },
                                        "last": false,
                                        "totalPages": 50,
                                        "totalElements": 100,
                                        "size": 2,
                                        "number": 0,
                                        "sort": [
                                            {
                                                "direction": "ASC",
                                                "property": "id",
                                                "ignoreCase": false,
                                                "nullHandling": "NATIVE",
                                                "ascending": true,
                                                "descending": false
                                            }
                                        ],
                                        "first": true,
                                        "numberOfElements": 2,
                                        "empty": false
                                    }
                                    """)
                    )
            )
    )
    ResponseEntity<Page<ResponseCommentNews>> getAll(@Parameter(hidden = true) Pageable pageable);

    @Operation(
            method = "GET",
            tags = "Comment",
            description = "Find comments page by filter",
            parameters = {
                    @Parameter(name = "page", description = "Comments page", example = "0"),
                    @Parameter(name = "size", description = "Page size", example = "20"),
                    @Parameter(name = "sort", description = "Sorting by field", example = "id"),
                    @Parameter(name = "part", description = "Part of text", example = "для")
            },
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageResponseCommentsNews.class),
                            examples = @ExampleObject("""
                                    {
                                        "content": [
                                            {
                                                "id": 46,
                                                "time": "2023-05-05T05:10:05.5",
                                                "text": "Taibbi, тот самый который для Маска еще недавно выкладывал \\"twitter files\\", уже почему-то не верит, что Маск следует этой объявленной позиции насчет цензуры, и в результате с Твиттера ушел вообще.",
                                                "username": "user6",
                                                "newsId": 5
                                            },
                                            {
                                                "id": 90,
                                                "time": "2023-05-09T09:18:09.9",
                                                "text": "Кажется что потенциал для модернизации минимален",
                                                "username": "user10",
                                                "newsId": 9
                                            }
                                        ],
                                        "pageable": {
                                            "sort": [
                                                {
                                                    "direction": "ASC",
                                                    "property": "id",
                                                    "ignoreCase": false,
                                                    "nullHandling": "NATIVE",
                                                    "ascending": true,
                                                    "descending": false
                                                }
                                            ],
                                            "offset": 0,
                                            "pageNumber": 0,
                                            "pageSize": 20,
                                            "paged": true,
                                            "unpaged": false
                                        },
                                        "last": true,
                                        "totalPages": 1,
                                        "totalElements": 2,
                                        "size": 20,
                                        "number": 0,
                                        "sort": [
                                            {
                                                "direction": "ASC",
                                                "property": "id",
                                                "ignoreCase": false,
                                                "nullHandling": "NATIVE",
                                                "ascending": true,
                                                "descending": false
                                            }
                                        ],
                                        "first": true,
                                        "numberOfElements": 2,
                                        "empty": false
                                    }
                                    """)
                    )
            )
    )
    ResponseEntity<Page<ResponseCommentNews>> getAllByFilter(@Parameter(hidden = true) Filter filter,
                                                             @Parameter(hidden = true) Pageable pageable);

    @Operation(
            method = "GET",
            tags = "Comment",
            description = "Find comments page by news id",
            parameters = {
                    @Parameter(name = "page", description = "Comments page", example = "0"),
                    @Parameter(name = "size", description = "Page size", example = "2"),
                    @Parameter(name = "sort", description = "Sorting by field", example = "id,desc")
            },
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageResponseComments.class),
                            examples = @ExampleObject("""
                                    {
                                        "content": [
                                            {
                                                "id": 101,
                                                "time": "2023-06-10T01:58:43.684166",
                                                "text": "First comment",
                                                "username": "user1"
                                            },
                                            {
                                                "id": 10,
                                                "time": "2023-05-01T01:10:01.1",
                                                "text": "Некоторые ещё утверждают, что способны водить автомобиль, хотя компьютеры, способные делать это, начали появляться только в самое последнее время.",
                                                "username": "user10"
                                            }
                                        ],
                                        "pageable": {
                                            "sort": [
                                                {
                                                    "direction": "DESC",
                                                    "property": "id",
                                                    "ignoreCase": false,
                                                    "nullHandling": "NATIVE",
                                                    "ascending": false,
                                                    "descending": true
                                                }
                                            ],
                                            "offset": 0,
                                            "pageNumber": 0,
                                            "pageSize": 2,
                                            "paged": true,
                                            "unpaged": false
                                        },
                                        "last": false,
                                        "totalPages": 6,
                                        "totalElements": 11,
                                        "size": 2,
                                        "number": 0,
                                        "sort": [
                                            {
                                                "direction": "DESC",
                                                "property": "id",
                                                "ignoreCase": false,
                                                "nullHandling": "NATIVE",
                                                "ascending": false,
                                                "descending": true
                                            }
                                        ],
                                        "first": true,
                                        "numberOfElements": 2,
                                        "empty": false
                                    }
                                    """)
                    )
            )
    )
    ResponseEntity<Page<ResponseComment>> getPageNewsComments(@Parameter(example = "1") Long newsId,
                                                              @Parameter(hidden = true) Pageable pageable);

    @Operation(
            method = "POST",
            tags = "Comment",
            security = @SecurityRequirement(name = "Bearer Authentication", scopes = "comments:write"),
            description = "Create a new comment",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateCommentDto.class),
                            examples = @ExampleObject("""
                                    {
                                      "text": "First comment",
                                      "newsId": "1"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseCommentNews.class),
                                    examples = @ExampleObject("""
                                            {
                                                 "id": 101,
                                                 "time": "2023-06-10T01:58:43.684165735",
                                                 "text": "First comment",
                                                 "username": "user1",
                                                 "newsId": 1
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionMessage.class),
                                    examples = @ExampleObject("""
                                            {
                                                 "status": 400,
                                                 "message": "username = must not be blank; newsId = must not be null"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionMessage.class),
                                    examples = @ExampleObject("""
                                            {
                                                "status":401,
                                                "message":"Full authentication is required to access this resource"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionMessage.class),
                                    examples = @ExampleObject("""
                                            {
                                                "status": 403,
                                                "message": "Forbidden"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<ResponseCommentNews> postComment(CreateCommentDto dto);

    @Operation(
            method = "PUT",
            tags = "Comment",
            security = @SecurityRequirement(name = "Bearer Authentication", scopes = "comments:write"),
            description = "Update existed comment",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateCommentDto.class),
                            examples = @ExampleObject("""
                                    {
                                        "text": "Updated text"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseCommentNews.class),
                                    examples = @ExampleObject("""
                                            {
                                                 "id": 1,
                                                 "time": "2023-05-01T01:01:01.1",
                                                 "text": "Updated text",
                                                 "username": "user1",
                                                 "newsId": 1
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionMessage.class),
                                    examples = @ExampleObject("""
                                            {
                                                 "status": 400,
                                                 "message": "text = must not be blank"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionMessage.class),
                                    examples = @ExampleObject("""
                                            {
                                                "status":401,
                                                "message":"Full authentication is required to access this resource"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionMessage.class),
                                    examples = @ExampleObject("""
                                            {
                                                "status": 403,
                                                "message": "Forbidden"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionMessage.class),
                                    examples = @ExampleObject("""
                                            {
                                                "status": 404,
                                                "message": "Comment 1 not found."
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<ResponseCommentNews> putComment(@Parameter(example = "1") Long id,
                                                   UpdateCommentDto dto);

    @Operation(
            method = "DELETE",
            tags = "Comment",
            security = @SecurityRequirement(name = "Bearer Authentication", scopes = "comments:delete"),
            description = "Delete a comment by id",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(
                            responseCode = "401",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionMessage.class),
                                    examples = @ExampleObject("""
                                            {
                                                "status":401,
                                                "message":"Full authentication is required to access this resource"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionMessage.class),
                                    examples = @ExampleObject("""
                                            {
                                                "status": 403,
                                                "message": "Forbidden"
                                            }
                                            """)
                            )
                    ),
            }
    )
    ResponseEntity<Void> delete(@Parameter(example = "10") Long id);
}
