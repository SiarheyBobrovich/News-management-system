package ru.clevertec.user.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.exception.handling.exception.InvalidTokenException;
import ru.clevertec.user.entity.User;
import ru.clevertec.user.service.TokenService;
import ru.clevertec.user.utils.TestDataUtil;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JwtService.class)
@TestPropertySource(properties = {
        "spring.security.secret=244226452948404D6351655468576D5A7134743777217A25432A462D4A614E645267556A586E3272357538782F413F4428472B4B6250655368566D5970337336d",
        "spring.security.issuer=test"
})
class JwtServiceTest {

    @Value("${spring.security.secret}")
    private String secret;

    @Autowired
    private TokenService tokenService;

    @Test
    void generateToken() {
        User user = TestDataUtil.getUser();
        String token = tokenService.generateToken(user);
        byte[] decode = Decoders.BASE64.decode(secret);

        Claims test = Jwts.parserBuilder()
                .requireIssuer("test")
                .setSigningKey(Keys.hmacShaKeyFor(decode))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertThat(test.getExpiration()).isAfter(new Date());
        assertThat(test.getSubject()).isEqualTo(user.getLogin());
    }

    @Test
    void getUserName() {
        User user = TestDataUtil.getUser();
        String token = tokenService.generateToken(user);
        String userName = tokenService.getUserName(token);

        assertThat(userName).isEqualTo(user.getLogin());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 12, 21, 25, 30, 35})
    void getUserNameNegate(Integer index) {
        User user = TestDataUtil.getUser();
        String token = tokenService.generateToken(user);
        char character = token.charAt(index);
        String replacement = String.valueOf(character);
        boolean isLower = Character.isLowerCase(character);
        String modifiedToken = token.replace(replacement, isLower ? replacement.toUpperCase() : replacement.toLowerCase());

        assertThatThrownBy(() -> tokenService.getUserName(modifiedToken))
                .isExactlyInstanceOf(InvalidTokenException.class);
    }
}
