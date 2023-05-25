package ru.clevertec.user.data;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public record UserDto(String username,
                      List<GrantedAuthority> authorities) {
}
