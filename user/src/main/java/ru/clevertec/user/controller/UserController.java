package ru.clevertec.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.user.controller.open_api.UserOpenApi;
import ru.clevertec.user.data.CreateUserDto;
import ru.clevertec.user.data.ResponseUserDto;
import ru.clevertec.user.data.UserDto;
import ru.clevertec.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserOpenApi {

    private final UserService userService;

    /**
     * Возвращает токен аутентификации для пользователя с заданными логином и паролем.
     *
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @return объект {@link ResponseEntity}, содержащий токен в виде строки и статус ответа
     */
    @Override
    @PostMapping("/login")
    public ResponseEntity<String> getToken(String login,
                                           String password) {
        String token = userService.getAuthorizationToken(login, password);
        return ResponseEntity.ok().body(token);
    }

    /**
     * Возвращает информацию о пользователе в виде объекта {@link UserDto}.
     *
     * @param userDetails объект {@link UserDetails}, содержащий информацию о текущем аутентифицированном пользователе
     * @return объект {@link ResponseEntity}, содержащий объект {@link UserDto} и статус ответа
     */
    @Override
    @PostMapping("/info")
    public ResponseEntity<UserDto> getUserInfo(@AuthenticationPrincipal UserDetails userDetails) {
        UserDto userDto = userService.getUserInfo(userDetails);

        return ResponseEntity.ok(userDto);
    }

    /**
     * Создаёт пользьвателя на сонове информации полученной из {@link CreateUserDto}
     *
     * @param userDto объект {@link CreateUserDto} созержащий информацию о пользователе
     * @return объект {@link ResponseEntity} содержащий объект {@link ResponseUserDto} с информацией о пользователе
     */
    @Override
    @PostMapping
    public ResponseEntity<ResponseUserDto> createUser(@Valid @RequestBody CreateUserDto userDto) {
        ResponseUserDto responseUserDto = userService.create(userDto);

        return ResponseEntity.status(201).body(responseUserDto);
    }
}
