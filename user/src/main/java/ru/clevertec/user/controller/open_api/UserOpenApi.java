package ru.clevertec.user.controller.open_api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import ru.clevertec.exception.handling.exception.ExceptionMessage;
import ru.clevertec.user.data.CreateUserDto;
import ru.clevertec.user.data.ResponseUserDto;
import ru.clevertec.user.data.UserDto;
import ru.clevertec.user.validation.ValidProto;

@Tag(name = "User")
public interface UserOpenApi {

    @Operation(
            method = "POST",
            tags = "User",
            description = "Get JWT token user by login and password",
            parameters = {
                    @Parameter(name = "login", example = "subscriber", required = true),
                    @Parameter(name = "password", example = "subscriber", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    examples = @ExampleObject("""
                                            eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.VFb0qJ1LRg_4ujbZoRMXnVkUgiuKq5KxWqNdbKq_G9Vvz-S1zZa9LPxtHWKa64zDl2ofkT8F6jBt_K4riU-fPg
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
                                                "message": "Не верный логин или пороль"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Only anonymous users must logIn",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExceptionMessage.class),
                                    examples = @ExampleObject("""
                                            {
                                                "status": 403,
                                                "message": "Full authentication is required to access this resource"
                                            }
                                            """)
                            )
                    ),
            }
    )
    ResponseEntity<String> getToken(@Parameter(hidden = true) String login,
                                    @Parameter(hidden = true) String password);

    @Operation(
            method = "POST",
            tags = "User",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            description = "Get information about user by JWT token",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    examples = @ExampleObject("""
                                            {
                                                "username": "subscriber",
                                                "authorities": [
                                                    {
                                                        "authority": "comments:write"
                                                    },
                                                    {
                                                        "authority": "comments:delete"
                                                    }
                                                ]
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
                                                "message": "Full authentication is required to access this resource"
                                            }
                                            """)
                            )
                    ),
            }
    )
    ResponseEntity<UserDto> getUserInfo(@Parameter(hidden = true) UserDetails userDetails);

    @Operation(
            method = "POST",
            tags = "User",
            description = "Create new user subscriber",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            content = @Content(
                                    examples = @ExampleObject("""
                                            {
                                                "id": 1,
                                                "login": "Siarhey1987",
                                                "email": "siarhey@siarhey.by",
                                                "firstName":"Siarhey",
                                                "lastName": "Babrovich"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            content = @Content(
                                    examples = @ExampleObject("""
                                            {
                                                "status": 400,
                                                "message": "Не верный логин"
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<ResponseUserDto> createUser(@Parameter(hidden = true) @ValidProto CreateUserDto userDto);
}
