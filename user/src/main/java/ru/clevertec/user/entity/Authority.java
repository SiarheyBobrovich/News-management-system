package ru.clevertec.user.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public enum Authority {

    NEWS_WRITE(new SimpleGrantedAuthority("news:write")),
    NEWS_DELETE(new SimpleGrantedAuthority("news:delete")),
    COMMENTS_WRITE(new SimpleGrantedAuthority("comments:write")),
    COMMENTS_DELETE(new SimpleGrantedAuthority("comments:delete")),
    ADMIN(new SimpleGrantedAuthority("admin"));

    private final GrantedAuthority grantedAuthority;

    Authority(GrantedAuthority grantedAuthority) {
        this.grantedAuthority = grantedAuthority;
    }
}
