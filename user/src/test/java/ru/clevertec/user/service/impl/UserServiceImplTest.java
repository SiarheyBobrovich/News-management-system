package ru.clevertec.user.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.clevertec.exception.handling.exception.InvalidLoginException;
import ru.clevertec.exception.handling.exception.UserNotFoundException;
import ru.clevertec.user.data.CreateUserDto;
import ru.clevertec.user.data.ResponseUserDto;
import ru.clevertec.user.data.UserDto;
import ru.clevertec.user.entity.User;
import ru.clevertec.user.mapper.UserMapper;
import ru.clevertec.user.repository.UserRepository;
import ru.clevertec.user.service.TokenService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static ru.clevertec.user.utils.TestDataUtil.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper mapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void getToken() {
        String login = "login";
        String password = "password";
        String token = "";
        User user = getUser();

        doReturn(Optional.of(user))
                .when(userRepository).findByLogin(login);
        doReturn(true)
                .when(passwordEncoder).matches(password, user.getPassword());
        doReturn(token)
                .when(tokenService).generateToken(user);

        String actualToken = userService.getAuthorizationToken(login, password);

        assertThat(actualToken).isEqualTo("");
    }

    @Test
    void getTokenThrows() {
        String login = "login";
        String password = "password";

        doReturn(Optional.empty())
                .when(userRepository).findByLogin(login);

        assertThatThrownBy(() -> userService.getAuthorizationToken(login, password))
                .isExactlyInstanceOf(InvalidLoginException.class);
    }

    @Test
    void loadUserByUsername() {
        String login = "login";
        User user = getUser();

        doReturn(Optional.of(user))
                .when(userRepository).findByLogin(login);

        UserDetails userDetails = userService.loadUserByUsername(login);

        assertThat(userDetails)
                .hasFieldOrPropertyWithValue("id", user.getId())
                .hasFieldOrPropertyWithValue("login", user.getUsername())
                .hasFieldOrPropertyWithValue("password", user.getPassword())
                .hasFieldOrPropertyWithValue("firstName", user.getFirstName())
                .hasFieldOrPropertyWithValue("lastName", user.getLastName())
                .hasFieldOrPropertyWithValue("email", user.getEmail())
                .hasFieldOrPropertyWithValue("role", user.getRole());
    }

    @Test
    void loadUserByUsernameThrows() {
        String login = "login";

        doReturn(Optional.empty())
                .when(userRepository).findByLogin(login);

        assertThatThrownBy(() -> userService.loadUserByUsername(login))
                .isExactlyInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getUserDto() {
        User user = getUser();
        UserDto userDto = userDto();

        doReturn(userDto)
                .when(mapper).toUserDto((UserDetails) user);

        UserDto actualDto = userService.getUserInfo(user);

        assertThat(actualDto).isEqualTo(userDto);
    }

    @Test
    void create() {
        CreateUserDto createUserDto = getTestData(CreateUserDto.class, CREATE_USER_DTO_01);
        User user = getTestData(User.class, USER_01_BEFORE_SAVE);
        ResponseUserDto expectedDto = getTestData(ResponseUserDto.class, RESPONSE_USER_DTO_01);
        User saved = getUser();

        doReturn(user)
                .when(mapper).toUser(createUserDto);
        doReturn(saved.getPassword())
                .when(passwordEncoder).encode(user.getPassword());
        doReturn(saved)
                .when(userRepository).save(user);
        doReturn(expectedDto)
                .when(mapper).toUserDto(saved);

        ResponseUserDto actualDto = userService.create(createUserDto);

        assertThat(actualDto).isEqualTo(expectedDto);
    }

    @Test
    void createThrows() {
        CreateUserDto createUserDto = getTestData(CreateUserDto.class, CREATE_USER_DTO_01);
        User user = getTestData(User.class, USER_01_BEFORE_SAVE);
        User saved = getUser();

        doReturn(user)
                .when(mapper).toUser(createUserDto);
        doReturn(saved.getPassword())
                .when(passwordEncoder).encode(user.getPassword());
        doThrow(DataIntegrityViolationException.class)
                .when(userRepository).save(user);

        assertThatThrownBy(() -> userService.create(createUserDto))
                .isExactlyInstanceOf(InvalidLoginException.class);
    }
}