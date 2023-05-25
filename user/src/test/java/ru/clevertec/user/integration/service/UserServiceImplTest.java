package ru.clevertec.user.integration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.clevertec.exception.handling.exception.InvalidLoginException;
import ru.clevertec.user.data.CreateUserDto;
import ru.clevertec.user.data.ResponseUserDto;
import ru.clevertec.user.service.UserService;
import ru.clevertec.user.utils.IntegrationTest;
import ru.clevertec.user.utils.PostgresqlTestContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.clevertec.user.utils.TestDataUtil.*;

@IntegrationTest
@ActiveProfiles("test")
@Sql("classpath:sql/Integration.sql")
public class UserServiceImplTest extends PostgresqlTestContainer {

    @Autowired
    private UserService service;

    @Test
    void getAuthorizationToken() {
        CreateUserDto user = getTestData(CreateUserDto.class, CREATE_USER_DTO_01);

        String token = service.getAuthorizationToken(user.login(), user.password());

        assertThat(token).isNotNull().isNotBlank();
    }

    @ParameterizedTest
    @CsvSource({"not existed,password", "login,not existed password"})
    void getAuthorizationTokenThrows(String login, String password) {
        assertThatThrownBy(() -> service.getAuthorizationToken(login, password))
                .isExactlyInstanceOf(InvalidLoginException.class);
    }

    @Test
    void createUser() {
        CreateUserDto createUserDto = getTestData(CreateUserDto.class, CREATE_USER_DTO_02);
        ResponseUserDto expected = getTestData(ResponseUserDto.class, RESPONSE_USER_DTO_02);

        ResponseUserDto actual = service.create(createUserDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createUserExist() {
        CreateUserDto createUserDto = getTestData(CreateUserDto.class, CREATE_USER_DTO_01);

        assertThatThrownBy(() -> service.create(createUserDto))
                .isExactlyInstanceOf(InvalidLoginException.class);
    }
}
