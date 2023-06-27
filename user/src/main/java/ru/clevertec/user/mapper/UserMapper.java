package ru.clevertec.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.clevertec.user.data.Authority;
import ru.clevertec.user.data.CreateUserDto;
import ru.clevertec.user.data.ResponseUserDto;
import ru.clevertec.user.data.UserDto;
import ru.clevertec.user.entity.Role;
import ru.clevertec.user.entity.User;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = Role.class)
public interface UserMapper {

    /**
     * Конвертирует объект {@link UserDetails} в объект {@link UserDto}.
     *
     * @param userDetails объект {@link UserDetails}, содержащий информацию о пользователе
     * @return объект {@link UserDto}, содержащий данные пользователя
     */
    UserDto toUserDto(UserDetails userDetails);

    @ObjectFactory
    default UserDto.Builder getBuilder(UserDetails userDetails) {
        return UserDto.newBuilder()
                .addAllAuthorities(
                        userDetails.getAuthorities().stream()
                                .map(this::mapAuthorities)
                                .toList());
    }

    @Mapping(target = "authority", source = "authority")
    Authority mapAuthorities(GrantedAuthority authority);

    /**
     * Конвертирует объект {@link CreateUserDto} в объект {@link User}.
     *
     * @param userDto объект {@link CreateUserDto}, содержащий информацию о пользователе
     * @return объект {@link User} без id и пороля
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", expression = "java(Role.SUBSCRIBER)")
    User toUser(CreateUserDto userDto);

    /**
     * Конвертирует объект {@link User} в объект {@link ResponseUserDto}.
     *
     * @param user объект {@link User}, текущай пользователь
     * @return объект {@link ResponseUserDto}, содержащий данные пользователя без пороля
     */
    ResponseUserDto toUserDto(User user);
}
