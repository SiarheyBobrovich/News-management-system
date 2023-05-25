package ru.clevertec.exception.handling.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.clevertec.exception.handling.exception.AbstractExceptionMessageException;
import ru.clevertec.exception.handling.exception.ExceptionMessage;
import ru.clevertec.logging.annotation.Logging;

import java.util.Optional;

@Logging
@RestControllerAdvice
public class GlobalHandlerAdvice {

    @ExceptionHandler
    public ResponseEntity<ExceptionMessage> handle(AbstractExceptionMessageException e) {
        return Optional.of(e)
                .map(AbstractExceptionMessageException::getExceptionMessage)
                .map(exceptionMessage ->
                        ResponseEntity
                                .status(e.getStatusCode())
                                .body(exceptionMessage))
                .orElseThrow();
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionMessage> handleThrowable(MethodArgumentNotValidException e) {
        String message = e.getFieldErrors().stream()
                .map(error -> StringUtils.joinWith(" = ", error.getField(), error.getDefaultMessage()))
                .reduce((s1, s2) -> StringUtils.joinWith("; ", s1, s2))
                .orElse("Bad Request");
        ExceptionMessage responseError = new ExceptionMessage(400, message);

        return ResponseEntity.badRequest().body(responseError);
    }
}
