package ru.clevertec.news.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.clevertec.news.data.CreateNewsDto;
import ru.clevertec.news.data.Filter;
import ru.clevertec.news.data.ResponseNewsView;
import ru.clevertec.news.entity.News;
import ru.clevertec.news.service.NewsService;
import ru.clevertec.news.util.IntegrationTest;
import ru.clevertec.news.util.PostgresqlTestContainer;
import ru.clevertec.news.util.aggregator.CreateNewsDtoAggregator;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.clevertec.news.util.TestDataUtil.*;

@IntegrationTest
@AutoConfigureMockMvc
class NewsControllerTest extends PostgresqlTestContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private NewsService service;

    private static final String URL = "/api/v1/news";

    @Test
    @SneakyThrows
    void checkGetNewsById() {
        News news = getTestData(News.class, NEWS_01_JSON);
        ResponseNewsView newsView = getTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON);
        Long newsId = news.getId();

        doReturn(newsView)
                .when(service).getById(newsId);

        mockMvc.perform(get(URL + "/{id}", 1L))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mapper.writeValueAsString(newsView))
                );
    }

    @Test
    @SneakyThrows
    void checkGetAllNews() {
        List<ResponseNewsView> news =
                getListTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON, VIEW_NEWS_02_JSON);
        Pageable pageable = Pageable.ofSize(20);
        PageImpl<ResponseNewsView> expectedPage = new PageImpl<>(news, pageable, 2);

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
    void checkGetAllNewsByFilter() {
        Filter filter = getTestData(Filter.class, FILTER_01_JSON);
        Pageable pageable = Pageable.ofSize(20);
        List<ResponseNewsView> content = getListTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON);
        PageImpl<ResponseNewsView> expectedPage = new PageImpl<>(content, pageable, 2L);

        doReturn(expectedPage)
                .when(service).getByFilter(filter, pageable);

        mockMvc.perform(get(URL + "/filter")
                        .param("part", filter.part()))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(mapper.writeValueAsString(expectedPage))
                );
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {""})
    void checkGetAllNewsByFilterNegate(String part) {
        mockMvc.perform(get(URL + "/filter")
                        .param("part", part))
                .andExpect(status().isBadRequest());
    }


    @SneakyThrows
    @ParameterizedTest
    @CsvFileSource(resources = "/data/csv/negate_news_create_dto.csv", delimiter = ';')
    void postNewsNegate(@AggregateWith(CreateNewsDtoAggregator.class) CreateNewsDto createNewsDto) {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createNewsDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvFileSource(resources = "/data/csv/negate_news_create_dto.csv", delimiter = ';')
    void putNewsNegate(@AggregateWith(CreateNewsDtoAggregator.class) CreateNewsDto createNewsDto) {
        mockMvc.perform(put(URL + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createNewsDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Nested
    @WireMockTest(httpPort = 8081)
    class WithUserToken {

        @SneakyThrows
        @ParameterizedTest
        @ValueSource(strings = {ADMIN_TOKEN, JOURNALIST_TOKEN})
        void postNews(String token) {
            ResponseNewsView expectedView = getTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON);
            CreateNewsDto createNewsDto = getTestData(CreateNewsDto.class, POST_DTO_NEWS_01_JSON);


            doReturn(expectedView)
                    .when(service).create(createNewsDto);

            mockMvc.perform(post(URL)
                            .header(HttpHeaders.AUTHORIZATION, token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(createNewsDto)))
                    .andExpectAll(
                            status().isCreated(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            content().json(mapper.writeValueAsString(expectedView))
                    );
        }


        @SneakyThrows
        @ParameterizedTest
        @ValueSource(strings = {ADMIN_TOKEN, JOURNALIST_TOKEN})
        void putNews(String token) {
            ResponseNewsView currentNewsView = getTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON);
            ResponseNewsView expectedView = getTestData(ResponseNewsView.class, VIEW_UPDATED_NEWS_01_JSON);
            CreateNewsDto updateDto = getTestData(CreateNewsDto.class, PUT_DTO_NEWS_01_JSON);
            Long newsId = expectedView.id();

            doReturn(currentNewsView)
                    .when(service).getById(newsId);
            doReturn(expectedView)
                    .when(service).update(newsId, updateDto);

            mockMvc.perform(put(URL + "/{id}", newsId)
                            .header(HttpHeaders.AUTHORIZATION, token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(updateDto)))
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            content().json(mapper.writeValueAsString(expectedView))
                    );
        }

        @SneakyThrows
        @ParameterizedTest
        @ValueSource(strings = {ADMIN_TOKEN, JOURNALIST_TOKEN})
        void deleteNews(String token) {
            ResponseNewsView currentNewsView = getTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON);
            Long id = currentNewsView.id();

            doReturn(currentNewsView)
                    .when(service).getById(id);
            doNothing()
                    .when(service).delete(id);

            mockMvc.perform(delete(URL + "/{id}", id)
                            .header(HttpHeaders.AUTHORIZATION, token))
                    .andExpect(status().isOk());
        }
    }
}
