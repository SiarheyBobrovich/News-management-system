package ru.clevertec.news.mapper;

import org.junit.jupiter.api.Test;
import ru.clevertec.news.data.CreateNewsDto;
import ru.clevertec.news.data.ResponseNewsView;
import ru.clevertec.news.entity.News;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.clevertec.news.util.TestDataUtil.*;

class NewsMapperTest {

    private final NewsMapper mapper = new NewsMapperImpl();

    @Test
    void checkToResponseNewsView() {
        News news = getTestData(News.class, NEWS_01_JSON);
        ResponseNewsView expected = getTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON);

        ResponseNewsView actual = mapper.toResponseNewsView(news);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkToNews() {
        CreateNewsDto createNewsDto = getTestData(CreateNewsDto.class, POST_DTO_NEWS_01_JSON);
        News expected = getTestData(News.class, POST_DTO_NEWS_01_JSON);

        News actual = mapper.toNews(createNewsDto);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkMerge() {
        CreateNewsDto createNewsDto = getTestData(CreateNewsDto.class, PUT_DTO_NEWS_01_JSON);
        News oldNews = getTestData(News.class, NEWS_01_JSON);
        News expected = getTestData(News.class, UPDATED_NEWS_01_JSON);

        News actual = mapper.merge(oldNews, createNewsDto);

        assertThat(actual).isEqualTo(expected);
    }
}
