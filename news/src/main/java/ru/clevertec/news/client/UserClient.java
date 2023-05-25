package ru.clevertec.news.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.clevertec.news.data.UserDto;

@FeignClient(name = "users", url = "${client.users.url}")
public interface UserClient {

    /**
     * Возвращает объект {@link UserDto}, содержащий информацию о пользователе по заданному токену аутентификации.
     *
     * @param token строка, содержащая токен аутентификации в заголовке запроса
     * @return объект {@link UserDto}, содержащий данные пользователя
     */
    @PostMapping
    UserDto getUserDetails(@RequestHeader("Authorization") String token);
}
