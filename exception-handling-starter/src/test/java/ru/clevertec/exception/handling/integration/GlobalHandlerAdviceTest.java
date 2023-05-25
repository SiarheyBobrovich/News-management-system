package ru.clevertec.exception.handling.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.exception.handling.config.HandlerConfig;
import ru.clevertec.exception.handling.exception.ExceptionMessage;
import ru.clevertec.exception.handling.util.ControllerFake;
import ru.clevertec.exception.handling.util.DtoFake;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {HandlerConfig.class, ControllerFake.class})
@TestPropertySource(properties = "spring.exception.handling.enabled=true")
class GlobalHandlerAdviceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private final static String URL = "/fake";

    @Test
    @SneakyThrows
    void handleNewsNotFoundException() {
        ExceptionMessage exceptionMessage = new ExceptionMessage(404, "News 1 not found.");

        mockMvc.perform(get(URL + "/news/{id}", 1L))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mapper.writeValueAsString(exceptionMessage))
                );
    }

    @Test
    @SneakyThrows
    void handleCommentNotFoundException() {
        ExceptionMessage exceptionMessage = new ExceptionMessage(404, "Comment 1 not found.");

        mockMvc.perform(get(URL + "/comments/{id}", 1L))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mapper.writeValueAsString(exceptionMessage))
                );
    }

    @Test
    @SneakyThrows
    void handleMethodArgumentNotValidException() {
        DtoFake dtoFake = new DtoFake(-1);

        mockMvc.perform(post(URL + "/valid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dtoFake)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("{\"status\":400,\"message\":\"ids = must be greater than 0\"}")
                );
    }
}
