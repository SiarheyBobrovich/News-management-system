package ru.clevertec.news.mapper;

import org.mapstruct.Mapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import ru.clevertec.news.data.AuthorityDto;
import ru.clevertec.news.data.UserDto;

import java.util.stream.Collectors;

@Mapper
public interface UserMapper {

    /**
     * Конвертирует {@link UserDto} в {@link UserDetails}
     *
     * @param userDto объект {@link UserDto} с информацией о пользователе
     * @return {@link UserDetails} c информацией о пользователе
     */
    default UserDetails toUserDetails(UserDto userDto) {
        return User.builder()
                .username(userDto.username())
                .password("")
                .authorities(
                        userDto.authorities().stream()
                                .map(AuthorityDto::authority)
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()))
                .build();
    }
}
