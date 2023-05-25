package ru.clevertec.news.controller.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.clevertec.exception.handling.exception.AbstractExceptionMessageException;
import ru.clevertec.exception.handling.exception.ExceptionMessage;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper mapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (AbstractExceptionMessageException e) {
            sendException(response, e.getExceptionMessage());
        }
    }

    public void sendException(HttpServletResponse response, AuthenticationException e) {
        sendException(response, new ExceptionMessage(HttpStatus.FORBIDDEN.value(), e.getMessage()));
    }

    @SneakyThrows
    private void sendException(HttpServletResponse response, ExceptionMessage exceptionMessage) {
        response.setStatus(exceptionMessage.status());
        response.setContentType("application/json");
        response.getWriter().write(mapper.writeValueAsString(exceptionMessage));
    }
}
