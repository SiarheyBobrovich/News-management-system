package ru.clevertec.user.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;
import ru.clevertec.exception.handling.exception.InvalidTokenException;
import ru.clevertec.user.entity.User;
import ru.clevertec.user.service.TokenService;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService implements TokenService {

    /**
     * Секретный ключ токена авторизации
     */
    @Value("${spring.security.secret}")
    private String secret;

    /**
     * Владелец токена авторизации
     */
    @Value("${spring.security.issuer}")
    private String issuer;

    /**
     * Метод для генерирации JWT токена авторизации на основе пользователя
     *
     * @param user пользователь
     * @return сгенерированный токен
     */
    @Override
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7))) // 1 week
                .signWith(getSignInKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Метод для получения имени пользователя на основе токена авторизации.
     *
     * @param token JWT токен авторизации
     * @return имя пользователя
     */
    @Override
    public String getUserName(String token) {
        checkToken(token);
        return getClaims(token).getSubject();
    }

    /**
     * Метод проверки полдинности токена и срока дествия
     *
     * @param token Jwt токен авторизации
     */
    private void checkToken(String token) {
        boolean isValid;
        try {
            isValid = !getClaims(token).getExpiration().before(new Date());
        } catch (JwtException | JsonParseException exception) {
            isValid = false;
        }
        if (!isValid) {
            throw new InvalidTokenException(token);
        }
    }

    /**
     * Получает утверждения, связанные с данным JWT токеном.
     *
     * @param token Токен, для которого нужно получить утверждения.
     * @return Объект {@link Claims}, представляющий утверждения, связанные с токеном.
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Возвращает ключ для подписи токена аутентификации.
     *
     * @return {@link Key} для подписи токена аутентификации.
     */
    private Key getSignInKey() {
        byte[] decode = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(decode);
    }
}
