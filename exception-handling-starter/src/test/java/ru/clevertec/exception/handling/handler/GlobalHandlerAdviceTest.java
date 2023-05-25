package ru.clevertec.exception.handling.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.clevertec.exception.handling.exception.CommentNotFoundException;
import ru.clevertec.exception.handling.exception.ExceptionMessage;
import ru.clevertec.exception.handling.exception.NewsNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalHandlerAdviceTest {

    private final GlobalHandlerAdvice globalHandlerAdvice = new GlobalHandlerAdvice();

    @Test
    void handleNewsNotFoundException() {
        NewsNotFoundException newsNotFoundException = new NewsNotFoundException(1L);
        ResponseEntity<ExceptionMessage> handle =
                globalHandlerAdvice.handle(newsNotFoundException);

        assertThat(handle)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND)
                .extracting("body")
                .hasFieldOrPropertyWithValue("status", 404)
                .hasFieldOrPropertyWithValue("message", "News 1 not found.");
    }

    @Test
    void handleCommentNotFoundException() {
        CommentNotFoundException commentNotFoundException = new CommentNotFoundException(1L);

        ResponseEntity<ExceptionMessage> handle = globalHandlerAdvice.handle(commentNotFoundException);

        assertThat(handle)
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.NOT_FOUND)
                .extracting("body")
                .hasFieldOrPropertyWithValue("status", 404)
                .hasFieldOrPropertyWithValue("message", "Comment 1 not found.");
    }
}
