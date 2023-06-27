package ru.clevertec.user.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.util.JsonFormat;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.exception.handling.exception.ExceptionMessage;
import ru.clevertec.exception.handling.exception.InvalidTokenException;
import ru.clevertec.user.data.CreateUserDto;
import ru.clevertec.user.data.ResponseUserDto;
import ru.clevertec.user.data.UserDto;
import ru.clevertec.user.entity.User;
import ru.clevertec.user.service.TokenService;
import ru.clevertec.user.service.UserService;
import ru.clevertec.user.utils.IntegrationTest;
import ru.clevertec.user.utils.PostgresqlTestContainer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.clevertec.user.utils.TestDataUtil.*;

@IntegrationTest
@AutoConfigureMockMvc
class UserControllerTest extends PostgresqlTestContainer {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private ObjectMapper mapper;

    private static final String URL = "/api/v1/users";
    private static final String URL_LOGIN = URL + "/login";
    private static final String URL_INFO = URL + "/info";

    @Test
    @SneakyThrows
    void getTokenAnonymous() {
        User user = getUser();
        String login = user.getLogin();
        String password = user.getPassword();

        doReturn("token")
                .when(userService).getAuthorizationToken(login, password);

        mockMvc.perform(
                        post(URL_LOGIN)
                                .param("login", login)
                                .param("password", password))
                .andExpectAll(
                        status().isOk(),
                        content().string("token")
                );
    }

    @Test
    @SneakyThrows
    @WithMockUser
    void getTokenForbidden() {
        mockMvc.perform(
                        post(URL_LOGIN)
                                .param("login", "login")
                                .param("password", "password"))
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    void getUserInfo() {
        User user = getUser();
        UserDto userDto = userDto();
        String login = user.getLogin();

        doReturn(login)
                .when(tokenService).getUserName("token");
        doReturn(user)
                .when(userService).loadUserByUsername(login);
        doReturn(userDto)
                .when(userService).getUserInfo(any());

        mockMvc.perform(
                        post(URL_INFO)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(JsonFormat.printer().print(userDto))
                );
    }

    @Test
    @SneakyThrows
    void getUserNotValid() {
        InvalidTokenException tokenException = new InvalidTokenException("token");
        ExceptionMessage exceptionMessage = tokenException.getExceptionMessage();

        doThrow(tokenException)
                .when(tokenService).getUserName("token");

        mockMvc.perform(
                        post(URL_INFO)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().string(mapper.writeValueAsString(exceptionMessage))
                );
    }

    @Test
    @SneakyThrows
    void getUserAnonymousForbidden() {
        ExceptionMessage exceptionMessage =
                new ExceptionMessage(403, "Full authentication is required to access this resource");
        mockMvc.perform(
                        post(URL_INFO))
                .andExpectAll(
                        status().isForbidden(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().string(mapper.writeValueAsString(exceptionMessage))
                );
    }

    @Nested
    class CreateUser {

        @Test
        @SneakyThrows
        void create() {
            CreateUserDto testData = getTestData(CreateUserDto.class, CREATE_USER_DTO_01);
            ResponseUserDto expectedUserDto = getTestData(ResponseUserDto.class, RESPONSE_USER_DTO_01);

            doReturn(expectedUserDto)
                    .when(userService).create(testData);

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonFormat.printer().print(testData)))
                    .andExpectAll(
                            status().isCreated(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            content().string(JsonFormat.printer().print(expectedUserDto))
                    );
        }

        @EmptySource
        @SneakyThrows
        @ParameterizedTest
        @ValueSource(strings = {
                "abcd",  //4 chars
                "1asdfghj", //must start with char
                "asdfgh/12a", //contains illegal char
                "РусскийТекст",
                "asdfghjklhzxcvbnmasdfghjklasdfghjklasdfga" //41 chars
        })
        void createLoginNegate(String login) {
            CreateUserDto userDto = getTestData(CreateUserDto.class, CREATE_USER_DTO_01)
                    .toBuilder()
                    .setLogin(login)
                    .build();

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonFormat.printer().print(userDto)))
                    .andExpect(status().isBadRequest());
        }

        @EmptySource
        @SneakyThrows
        @ParameterizedTest
        @ValueSource(strings = {
                "abcdas4",  //7 chars
                "aasdfghja", //must contains number
                "123456789", //must contains char
                "asdfgh/12a", //contains illegal char
                "РусскийТекст123",
                //201 chars
                "asdfghjklhzxcvbnmasdfghjklasdfghjklas123aasdfghjklhzxcvbnmasdfghjklasdfghjklasdfgaasdfghjklhzxcvbnmasdfghjklasdfghjklasdfgaasdfghjklhzxcvbnmasdfghjklasdfghjklasdfgaasdfghjklhzxcvbnmasdfghjklaghjklhzxcv"
        })
        void createPasswordNegate(String password) {
            CreateUserDto userDto = getTestData(CreateUserDto.class, CREATE_USER_DTO_01)
                    .toBuilder()
                    .setPassword(password)
                    .build();

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonFormat.printer().print(userDto)))
                    .andExpect(status().isBadRequest());
        }

        @EmptySource
        @SneakyThrows
        @ParameterizedTest
        @ValueSource(strings = {
                "asdfghj.dad",
                "@ddada.dad",
                "dawd@.dad",
                "dawd@dada.",
        })
        void createEmailEmpty(String email) {
            CreateUserDto userDto = getTestData(CreateUserDto.class, CREATE_USER_DTO_01)
                    .toBuilder()
                    .setEmail(email)
                    .build();

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonFormat.printer().print(userDto)))
                    .andExpect(status().isBadRequest());
        }

        @EmptySource
        @SneakyThrows
        @ParameterizedTest
        void createFirstNameEmpty(String firstName) {
            CreateUserDto userDto = getTestData(CreateUserDto.class, CREATE_USER_DTO_01)
                    .toBuilder()
                    .setFirstName(firstName)
                    .build();

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonFormat.printer().print(userDto)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @SneakyThrows
        void createLastNameEmpty() {
            CreateUserDto userDto = getTestData(CreateUserDto.class, CREATE_USER_DTO_01)
                    .toBuilder()
                    .setLastName("")
                    .build();

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(JsonFormat.printer().print(userDto)))
                    .andExpect(status().isBadRequest());
        }
    }
}
