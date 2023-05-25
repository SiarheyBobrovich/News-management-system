package ru.clevertec.user.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Role {

    /**
     * Администратор может производить CRUD-операции со всеми сущностями
     */
    ADMIN(Arrays.stream(Authority.values())
            .map(Authority::getGrantedAuthority)
            .toList()),

    /**
     * Журналист может добавлять и изменять/удалять только свои новости
     */
    JOURNALIST(List.of(
            Authority.NEWS_WRITE.getGrantedAuthority(),
            Authority.NEWS_DELETE.getGrantedAuthority()
    )),

    /**
     * Подписчик может добавлять и изменять/удалять только свои комментарии
     */
    SUBSCRIBER(List.of(
            Authority.COMMENTS_WRITE.getGrantedAuthority(),
            Authority.COMMENTS_DELETE.getGrantedAuthority()
    ));

    private final List<GrantedAuthority> authorities;

    Role(List<GrantedAuthority> grantedAuthorities) {
        this.authorities = grantedAuthorities;
    }
}
