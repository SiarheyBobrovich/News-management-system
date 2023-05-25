package ru.clevertec.user.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.clevertec.user.data.CreateUserDto;
import ru.clevertec.user.data.ResponseUserDto;
import ru.clevertec.user.data.UserDto;

public interface UserService extends UserDetailsService {

    /**
     * Метод для получения токена авторизации на основе логина и пароля пользователя.
     *
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @return токен авторизации
     */
    String getAuthorizationToken(String login,
                                 String password);

    /**
     * Метод для получения {@link UserDto} объекта пользователя на основе объекта {@link UserDetails}.
     *
     * @param userDetails объект {@link UserDetails}
     * @return {@link UserDto} объект пользователя
     */
    UserDto getUserInfo(UserDetails userDetails);

    /**
     * Метод для создания {@link ru.clevertec.user.entity.User} объекта на основе объекта {@link CreateUserDto}.
     *
     * @param dto объект {@link CreateUserDto}
     * @return объект пользователя {@link ResponseUserDto}
     */
    ResponseUserDto create(CreateUserDto dto);
}
