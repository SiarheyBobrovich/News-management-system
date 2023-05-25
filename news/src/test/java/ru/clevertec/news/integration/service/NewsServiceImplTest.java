package ru.clevertec.news.integration.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import ru.clevertec.news.data.CreateNewsDto;
import ru.clevertec.news.data.Filter;
import ru.clevertec.news.data.ResponseNewsView;
import ru.clevertec.exception.handling.exception.NewsNotFoundException;
import ru.clevertec.news.service.NewsService;
import ru.clevertec.news.util.IntegrationTest;
import ru.clevertec.news.util.PostgresqlTestContainer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.clevertec.news.util.TestDataUtil.*;

@IntegrationTest
@Sql("classpath:sql/integration_news.sql")
class NewsServiceImplTest extends PostgresqlTestContainer {

    @Autowired
    private NewsService newsService;

    @Test
    void checkGetById() {
        ResponseNewsView responseNewsView = getTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON);
        Long expectedId = responseNewsView.id();

        ResponseNewsView actual = newsService.getById(expectedId);

        assertThat(actual).isEqualTo(responseNewsView);
    }

    @Test
    void checkGetByIdThrows() {
        assertThatThrownBy(() -> newsService.getById(-1L))
                .isExactlyInstanceOf(NewsNotFoundException.class);
    }

    @Test
    void checkGetAll() {
        List<ResponseNewsView> expectedContent = getListTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON, VIEW_NEWS_02_JSON);
        Pageable pageable = Pageable.unpaged();

        List<ResponseNewsView> actualPage =
                newsService.getAll(pageable)
                        .getContent();

        assertThat(actualPage).isEqualTo(expectedContent);
    }

    @Test
    void checkGetAllSize_1() {
        List<ResponseNewsView> expectedContent = getListTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON);
        Pageable pageable = Pageable.ofSize(1);

        List<ResponseNewsView> actualPage =
                newsService.getAll(pageable)
                        .getContent();

        assertThat(actualPage).isEqualTo(expectedContent);
    }

    @Test
    void checkGetByFilter() {
        Filter filter = getTestData(Filter.class, FILTER_01_JSON);
        List<ResponseNewsView> expectedContent = getListTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON);
        Pageable pageable = Pageable.unpaged();

        List<ResponseNewsView> actualPage =
                newsService.getByFilter(filter, pageable)
                        .getContent();

        assertThat(actualPage).isEqualTo(expectedContent);
    }

    @Test
    void checkCreate() {
        CreateNewsDto newsDto = getTestData(CreateNewsDto.class, POST_DTO_NEWS_01_JSON);

        ResponseNewsView newsView = newsService.create(newsDto);

        assertThat(newsView)
                .hasNoNullFieldsOrProperties()
                .extracting("title", "text")
                .containsExactly(newsDto.title(), newsDto.text());
    }

    @Test
    void checkUpdate() {
        CreateNewsDto newsDto = getTestData(CreateNewsDto.class, PUT_DTO_NEWS_01_JSON);

        ResponseNewsView newsView = newsService.update(1L, newsDto);

        assertThat(newsView)
                .hasNoNullFieldsOrPropertiesExcept()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("title", newsDto.title())
                .hasFieldOrPropertyWithValue("text", newsDto.text());
    }

    @Test
    void checkUpdateThrows() {
        CreateNewsDto newsDto = getTestData(CreateNewsDto.class, PUT_DTO_NEWS_01_JSON);

        assertThatThrownBy(() -> newsService.update(-1L, newsDto))
                .isExactlyInstanceOf(NewsNotFoundException.class);
    }

    @Test
    void checkDelete() {
        assertThatNoException()
                .isThrownBy(() -> newsService.delete(1L));
    }
}
