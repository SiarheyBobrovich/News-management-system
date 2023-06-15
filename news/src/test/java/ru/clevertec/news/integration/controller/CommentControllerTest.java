package ru.clevertec.news.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.news.data.*;
import ru.clevertec.news.entity.News;
import ru.clevertec.news.service.CommentService;
import ru.clevertec.news.util.IntegrationTest;
import ru.clevertec.news.util.PostgresqlTestContainer;
import ru.clevertec.news.util.aggregator.CreateCommentDtoAggregator;
import ru.clevertec.news.util.aggregator.UpdateCommentDtoAggregator;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.clevertec.news.util.TestDataUtil.*;

@IntegrationTest
@AutoConfigureMockMvc
class CommentControllerTest extends PostgresqlTestContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CommentService service;

    private static final String URL = "/api/v1/comments";

    @Test
    @SneakyThrows
    void checkFindById() {
        ResponseCommentNews expected = getTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_01_JSON);
        Long id = expected.id();

        doReturn(expected)
                .when(service).getById(id);

        mockMvc.perform(get(URL + "/{id}", id))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mapper.writeValueAsString(expected))
                );
    }

    @Test
    @SneakyThrows
    void checkFindAll() {
        Pageable pageable = Pageable.ofSize(20);
        List<ResponseCommentNews> expected =
                getListTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_01_JSON, RESP_COMMENT_NEWS_02_JSON);
        PageImpl<ResponseCommentNews> expectedPage = new PageImpl<>(expected, pageable, 2);

        doReturn(expectedPage)
                .when(service).getAll(pageable);

        mockMvc.perform(get(URL))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mapper.writeValueAsString(expectedPage))
                );
    }

    @Test
    @SneakyThrows
    void checkFindAllPageSize_1() {
        Pageable pageable = Pageable.ofSize(1);
        List<ResponseCommentNews> expected =
                getListTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_01_JSON);
        PageImpl<ResponseCommentNews> expectedPage = new PageImpl<>(expected, pageable, 2);

        doReturn(expectedPage)
                .when(service).getAll(pageable);

        mockMvc.perform(get(URL)
                        .param("size", "1"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mapper.writeValueAsString(expectedPage))
                );
    }

    @Test
    @SneakyThrows
    void checkGetAllByFilter() {
        Pageable pageable = Pageable.ofSize(1);
        Filter filter = getTestData(Filter.class, FILTER_01_JSON);
        List<ResponseCommentNews> expected =
                getListTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_01_JSON);
        PageImpl<ResponseCommentNews> expectedPage = new PageImpl<>(expected, pageable, 1);

        doReturn(expectedPage)
                .when(service).getAllByFilter(filter, pageable);

        mockMvc.perform(get(URL + "/filter")
                        .param("part", filter.part())
                        .param("size", "1"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mapper.writeValueAsString(expectedPage))
                );
    }

    @Test
    @SneakyThrows
    void getPageNewsComments() {
        Pageable pageable = Pageable.ofSize(20);
        News news = getTestData(News.class, NEWS_01_JSON);
        Long newsId = news.getId();

        List<ResponseComment> expected =
                getListTestData(ResponseComment.class, RESP_COMMENT_01_JSON, RESP_COMMENT_02_JSON);
        PageImpl<ResponseComment> expectedPage = new PageImpl<>(expected, pageable, 2);

        doReturn(expectedPage)
                .when(service).getAllByNewsId(newsId, pageable);

        mockMvc.perform(get(URL + "/news/{newsId}", newsId))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mapper.writeValueAsString(expectedPage))
                );
    }

    @Nested
    class Validation {

        @SneakyThrows
        @ParameterizedTest
        @CsvFileSource(resources = "/data/csv/negate_create_comment_dto.csv", delimiter = ';')
        void checkPostCommentNegate(@AggregateWith(CreateCommentDtoAggregator.class) CreateCommentDto commentDto) {
            mockMvc.perform(post(URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(commentDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("status").value(400));
        }

        @SneakyThrows
        @ParameterizedTest
        @CsvFileSource(resources = "/data/csv/negate_update_comment_dto.csv", lineSeparator = ";")
        void checkPutCommentNegate(@AggregateWith(UpdateCommentDtoAggregator.class) UpdateCommentDto updateCommentDto) {
            mockMvc.perform(put(URL + "/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(updateCommentDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("status").value(400));
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("getEmptyString")
        void checkPutCommentNegateEmpty(@AggregateWith(UpdateCommentDtoAggregator.class) UpdateCommentDto updateCommentDto) {
            mockMvc.perform(put(URL + "/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(updateCommentDto)))
                    .andExpect(status().isBadRequest());
        }

        @SneakyThrows
        @ParameterizedTest
        @ValueSource(strings = {""})
        void checkGetAllNewsByFilterNegate(String part) {
            mockMvc.perform(get(URL + "/filter")
                            .param("part", part))
                    .andExpect(status().isBadRequest());
        }

        private static Stream<String> getEmptyString() {
            return Stream.of(
                    "", "\t", null
            );
        }
    }

    @Nested
    @WireMockTest(httpPort = 8081)
    class WithUserToken {

        @SneakyThrows
        @ParameterizedTest
        @ValueSource(strings = {ADMIN_TOKEN, SUBSCRIBER_TOKEN})
        void checkPutComment(String token) {
            ResponseCommentNews currentComment = getTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_01_JSON);
            UpdateCommentDto updateCommentDto = getTestData(UpdateCommentDto.class, UPDATE_COMMENT_DTO_JSON);
            ResponseCommentNews expected = getTestData(ResponseCommentNews.class, UPDATED_RESP_COMMENT_NEWS_01_JSON);
            Long id = expected.id();

            doReturn(currentComment)
                    .when(service).getById(id);
            doReturn(expected)
                    .when(service).update(id, updateCommentDto);

            mockMvc.perform(put(URL + "/{id}", id)
                            .header(HttpHeaders.AUTHORIZATION, token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(updateCommentDto)))
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            content().json(mapper.writeValueAsString(expected))
                    );
        }

        @SneakyThrows
        @ParameterizedTest
        @ValueSource(strings = {ADMIN_TOKEN, SUBSCRIBER_TOKEN})
        void checkPostComment(String token) {
            ResponseCommentNews expected = getTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_01_JSON);
            CreateCommentDto commentDto = getTestData(CreateCommentDto.class, CREATE_COMMENT_DTO_01_JSON);

            doReturn(expected)
                    .when(service).create(commentDto);

            mockMvc.perform(post(URL)
                            .header(HttpHeaders.AUTHORIZATION, token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(commentDto)))
                    .andExpectAll(
                            status().isCreated(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            content().json(mapper.writeValueAsString(expected))
                    );
        }

        @SneakyThrows
        @ParameterizedTest
        @ValueSource(strings = {ADMIN_TOKEN, SUBSCRIBER_TOKEN})
        void checkDeleteComment(String token) {
            ResponseCommentNews currentComment = getTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_01_JSON);
            Long id = 1L;

            doReturn(currentComment)
                    .when(service).getById(id);
            doNothing()
                    .when(service).delete(id);

            mockMvc.perform(delete(URL + "/{id}", id)
                            .header(HttpHeaders.AUTHORIZATION, token))
                    .andExpect(status().isOk());
        }
    }
}
