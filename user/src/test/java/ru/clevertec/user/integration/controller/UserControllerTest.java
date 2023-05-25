package ru.clevertec.user.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
                        content().json(mapper.writeValueAsString(userDto))
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
                            .content(mapper.writeValueAsString(testData)))
                    .andExpectAll(
                            status().isCreated(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            content().string(mapper.writeValueAsString(expectedUserDto))
                    );
        }

        @SneakyThrows
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "abcd",  //4 chars
                "1asdfghj", //must start with char
                "asdfgh/12a", //contains illegal char
                "РусскийТекст",
                "asdfghjklhzxcvbnmasdfghjklasdfghjklasdfga" //41 chars
        })
        void createLoginNegate(String login) {
            CreateUserDto testData = getTestData(CreateUserDto.class, CREATE_USER_DTO_01);
            CreateUserDto userDto = new CreateUserDto(login, testData.password(), testData.email(), testData.firstName(), testData.lastName());

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(userDto)))
                    .andExpect(status().isBadRequest());
        }

        @SneakyThrows
        @ParameterizedTest
        @NullAndEmptySource
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
            CreateUserDto testData = getTestData(CreateUserDto.class, CREATE_USER_DTO_01);
            CreateUserDto userDto = new CreateUserDto(testData.login(), password, testData.email(), testData.firstName(), testData.lastName());

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(userDto)))
                    .andExpect(status().isBadRequest());
        }

        @SneakyThrows
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "asdfghj.dad",
                "@ddada.dad",
                "dawd@.dad",
                "dawd@dada.",
        })
        void createEmailEmpty(String email) {
            CreateUserDto testData = getTestData(CreateUserDto.class, CREATE_USER_DTO_01);
            CreateUserDto userDto = new CreateUserDto(testData.login(), testData.password(), email, testData.firstName(), testData.lastName());

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(userDto)))
                    .andExpect(status().isBadRequest());
        }

        @SneakyThrows
        @ParameterizedTest
        @NullAndEmptySource
        void createFirstNameEmpty(String firstName) {
            CreateUserDto testData = getTestData(CreateUserDto.class, CREATE_USER_DTO_01);
            CreateUserDto userDto = new CreateUserDto(testData.login(), testData.password(), testData.email(), firstName, testData.lastName());

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(userDto)))
                    .andExpect(status().isBadRequest());
        }

        @SneakyThrows
        @ParameterizedTest
        @NullAndEmptySource
        void createLastNameEmpty(String lastName) {
            CreateUserDto testData = getTestData(CreateUserDto.class, CREATE_USER_DTO_01);
            CreateUserDto userDto = new CreateUserDto(testData.login(), testData.password(), testData.email(), testData.firstName(), lastName);

            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(userDto)))
                    .andExpect(status().isBadRequest());
        }
    }
}
