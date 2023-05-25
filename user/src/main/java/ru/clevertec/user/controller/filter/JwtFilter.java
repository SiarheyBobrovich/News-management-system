package ru.clevertec.user.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.clevertec.user.service.TokenService;
import ru.clevertec.user.service.UserService;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserService userService;

    /**
     * Проверяет наличие и валидность токена JWT в заголовке Authorization запроса.
     * Усли токена нет отдат запрос дальше, иначе авторизирует пользователя
     *
     * @param req         запрос пользователя
     * @param resp        Ответ пользователю
     * @param filterChain объект {@link FilterChain}
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req,
                                    @NonNull HttpServletResponse resp,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(header) || !header.startsWith("Bearer ")) {
            filterChain.doFilter(req, resp);
            return;
        }

        String token = header.substring(7);
        String userName = tokenService.getUserName(token);
        UserDetails userDetails = userService.loadUserByUsername(userName);

        UsernamePasswordAuthenticationToken authenticated =
                UsernamePasswordAuthenticationToken
                        .authenticated(
                                userDetails, null, userDetails.getAuthorities());
        authenticated.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(req));

        SecurityContextHolder.getContext().setAuthentication(authenticated);

        filterChain.doFilter(req, resp);
    }
}
