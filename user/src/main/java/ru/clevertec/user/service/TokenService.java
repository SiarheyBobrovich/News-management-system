package ru.clevertec.user.service;

import ru.clevertec.user.entity.User;

public interface TokenService {

    /**
     * Метод для генерирации токена авторизации на основе пользователя
     *
     * @param user объект {@link User}
     * @return сгенерированный токен
     */
    String generateToken(User user);

    /**
     * Метод для получения имени пользователя на основе токена авторизации.
     *
     * @param token токен авторизации
     * @return имя пользователя
     */
    String getUserName(String token);
}
