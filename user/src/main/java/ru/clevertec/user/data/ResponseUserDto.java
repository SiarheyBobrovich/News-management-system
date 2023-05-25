package ru.clevertec.user.data;

public record ResponseUserDto(Long id,
                              String login,
                              String email,
                              String firstName,
                              String lastName
) {
}
