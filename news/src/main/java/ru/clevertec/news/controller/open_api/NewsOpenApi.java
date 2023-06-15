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
import ru.clevertec.news.data.CreateNewsDto;
import ru.clevertec.news.data.Filter;
import ru.clevertec.news.data.ResponseNewsView;

@Tag(name = "News", description = "API for working with news")
public interface NewsOpenApi {

    @Operation(
            method = "GET",
            tags = "News",
            description = "Get news by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject("""
                                            {
                                                "id": 1,
                                                "time": "2023-05-01T01:01:01",
                                                "title": "Компания ispace объяснила причину крушения модуля Hakuto-R",
                                                "text": " Эксперты компании пришли к выводу, что причиной крушения стала ошибка бортового компьютера, который неправильно определил высоту аппарата из-за сложного рельефа.",
                                                "author": "journalist"
                                            }
                                            """),
                                    schema = @Schema(implementation = ResponseNewsView.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject("""
                                            {
                                                "status": 404,
                                                "message": "News 1 not found."
                                            }
                                            """),
                                    schema = @Schema(implementation = ExceptionMessage.class))
                    )
            }
    )
    ResponseEntity<ResponseNewsView> getNewsById(@Parameter(example = "1") Long id);


    @Operation(
            method = "GET",
            tags = "News",
            description = "Get all news",
            parameters = {
                    @Parameter(name = "page", description = "News page", example = "0"),
                    @Parameter(name = "size", description = "Page size", example = "2"),
                    @Parameter(name = "sort", description = "Sorting by field", example = "title,desc")
            },
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageResponseNewsView.class),
                            examples = @ExampleObject("""
                                    {
                                         "content": [
                                             {
                                                 "id": 1,
                                                 "time": "2023-05-01T01:01:01",
                                                 "title": "Компания ispace объяснила причину крушения модуля Hakuto-R",
                                                 "text": " Эксперты компании пришли к выводу, что причиной крушения стала ошибка бортового компьютера, который неправильно определил высоту аппарата из-за сложного рельефа.",
                                                 "author": "journalist"
                                             },
                                             {
                                                 "id": 2,
                                                 "time": "2023-05-02T02:02:02",
                                                 "title": "Gigabyte представила внешнюю видеокарту Aorus GeForce RTX 4090 Gaming Box в большом корпусе с мощным БП",
                                                 "text": "Устройство представляет из себя пятикилограммовый корпус размером 302х172х189 мм (ГхШхВ) с системой жидкостного охлаждения и встроенным БП на 850 Вт, имеющим сертификацию 80 Plus Gold.",
                                                 "author": "journalist"
                                             }
                                         ],
                                         "pageable": {
                                             "sort": [],
                                             "offset": 0,
                                             "pageNumber": 0,
                                             "pageSize": 2,
                                             "paged": true,
                                             "unpaged": false
                                         },
                                         "last": false,
                                         "totalPages": 11,
                                         "totalElements": 21,
                                         "size": 2,
                                         "number": 0,
                                         "sort": [],
                                         "first": true,
                                         "numberOfElements": 2,
                                         "empty": false
                                    }
                                    """)
                    )
            )
    )
    ResponseEntity<Page<ResponseNewsView>> getAllNews(@Parameter(hidden = true) Pageable pageable);

    @Operation(
            method = "GET",
            tags = "News",
            description = "Find news page by filter",
            parameters = {
                    @Parameter(name = "page", description = "News page", example = "0"),
                    @Parameter(name = "size", description = "Page size", example = "20"),
                    @Parameter(name = "sort", description = "Sorting by field", example = "title,desc"),
                    @Parameter(name = "part", description = "Part of title or text", example = "мод")
            },
            responses = @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PageResponseNewsView.class),
                            examples = @ExampleObject("""
                                    {
                                       "content": [
                                         {
                                           "id": 3,
                                           "time": "2023-05-03T03:03:03",
                                           "title": "Суд утвердил мировое соглашение по коллективному иску к Apple из-за проблем с клавиатурой-«бабочкой»",
                                           "text": "Суд в США утвердил мировое соглашение по коллективному иску к Apple из-за проблем с клавиатурой типа «бабочка» в MacBook моделей 2015-2019 годов. Об этом сообщает Reuters.",
                                           "author": "journalist"
                                         },
                                         {
                                           "id": 1,
                                           "time": "2023-05-01T01:01:01",
                                           "title": "Компания ispace объяснила причину крушения модуля Hakuto-R",
                                           "text": " Эксперты компании пришли к выводу, что причиной крушения стала ошибка бортового компьютера, который неправильно определил высоту аппарата из-за сложного рельефа.",
                                           "author": "journalist"
                                         }
                                       ],
                                       "pageable": {
                                         "sort": [
                                           {
                                             "direction": "DESC",
                                             "property": "title",
                                             "ignoreCase": false,
                                             "nullHandling": "NATIVE",
                                             "ascending": false,
                                             "descending": true
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
                                           "direction": "DESC",
                                           "property": "title",
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
    ResponseEntity<Page<ResponseNewsView>> getAllNewsByFilter(@Parameter(hidden = true) Filter filter,
                                                              @Parameter(hidden = true) Pageable pageable);

    @Operation(
            method = "POST",
            tags = "News",
            security = @SecurityRequirement(name = "Bearer Authentication", scopes = "news:write"),
            description = "Create new news",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateNewsDto.class),
                            examples = @ExampleObject("""
                                    {
                                      "title": "First news",
                                      "text": "First text"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ResponseNewsView.class),
                                    examples = @ExampleObject("""
                                            {
                                                 "id": 21,
                                                 "time": "2023-06-16T00:25:18.391254984",
                                                 "title": "First news",
                                                 "text": "First text",
                                                 "author": "journalist"
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
                                                 "message": "title = must match \\"[\\\\w\\\\sа-яА-ЯёЁ,.\\\\-()!?:<>*+/]{5,255}\\"; text = must match \\"[\\\\w\\\\sа-яА-ЯёЁ,.\\\\-()!?:<>*+/]{10,}\\""
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
    ResponseEntity<ResponseNewsView> postNews(CreateNewsDto newsDto);

    @Operation(
            method = "PUT",
            tags = "News",
            security = @SecurityRequirement(name = "Bearer Authentication", scopes = "news:write"),
            description = "Update existed news",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateNewsDto.class),
                            examples = @ExampleObject("""
                                    {
                                        "title": "Updated news",
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
                                    schema = @Schema(implementation = ResponseNewsView.class),
                                    examples = @ExampleObject("""
                                            {
                                                "id": 21,
                                                "time": "2023-06-16T00:25:18.391255",
                                                "title": "Updated news",
                                                "text": "Updated text",
                                                "author": "journalist"
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
                                                 "message": "title = must match \\"[\\\\w\\\\sа-яА-ЯёЁ,.\\\\-()!?:<>*+/]{5,255}\\"; text = must match \\"[\\\\w\\\\sа-яА-ЯёЁ,.\\\\-()!?:<>*+/]{10,}\\""
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
                                                "message": "News 1 not found."
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<ResponseNewsView> putNews(@Parameter(example = "1") Long id,
                                             CreateNewsDto newsDto);

    @Operation(
            method = "DELETE",
            tags = "News",
            security = @SecurityRequirement(name = "Bearer Authentication", scopes = "news:delete"),
            description = "Delete news by id",
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
    ResponseEntity<Void> deleteNews(@Parameter(example = "1") Long id);
}
