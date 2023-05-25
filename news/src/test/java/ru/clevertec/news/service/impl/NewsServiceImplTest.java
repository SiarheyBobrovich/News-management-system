package ru.clevertec.news.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.clevertec.news.data.CreateNewsDto;
import ru.clevertec.news.data.Filter;
import ru.clevertec.news.data.ResponseNewsView;
import ru.clevertec.news.entity.News;
import ru.clevertec.exception.handling.exception.NewsNotFoundException;
import ru.clevertec.news.mapper.NewsMapper;
import ru.clevertec.news.repository.NewsRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.clevertec.news.util.TestDataUtil.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    @InjectMocks
    private NewsServiceImpl newsService;

    @Mock
    private NewsRepository repository;

    @Mock
    private NewsMapper mapper;

    public static Stream<Arguments> getFilterPageableListNewsListResponseNewsView() {
        return Stream.of(
                Arguments.of(
                        null,
                        Pageable.unpaged(),
                        getListTestData(News.class, NEWS_01_JSON, NEWS_02_JSON),
                        getListTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON, VIEW_NEWS_02_JSON)
                ), Arguments.of(
                        "%t n%",
                        Pageable.unpaged(),
                        getListTestData(News.class, NEWS_01_JSON),
                        getListTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON)
                )
        );
    }

    @Test
    void checkGetById() {
        News news = getTestData(News.class, NEWS_01_JSON);
        Long id = news.getId();
        ResponseNewsView responseNewsView = getTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON);

        doReturn(Optional.of(news))
                .when(repository).findById(id);
        doReturn(responseNewsView)
                .when(mapper).toResponseNewsView(news);

        ResponseNewsView actual = newsService.getById(id);

        assertThat(actual).isEqualTo(responseNewsView);
    }

    @Test
    void checkGetByIdThrows() {
        doReturn(Optional.empty())
                .when(repository).findById(any());

        assertThatThrownBy(() -> newsService.getById(1L))
                .isExactlyInstanceOf(NewsNotFoundException.class);
    }

    @Test
    void checkGetAll() {
        Pageable pageable = Pageable.unpaged();
        List<News> listTestData = getListTestData(News.class, NEWS_01_JSON, NEWS_02_JSON);
        List<ResponseNewsView> expectedViews = getListTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON, VIEW_NEWS_02_JSON);
        PageImpl<News> newsPage = new PageImpl<>(listTestData, pageable, listTestData.size());

        doReturn(newsPage)
                .when(repository).findAll(pageable);
        IntStream.range(0, listTestData.size())
                .forEach(i -> doReturn(expectedViews.get(i))
                        .when(mapper).toResponseNewsView(listTestData.get(i)));

        List<ResponseNewsView> actualNews = newsService.getAll(pageable)
                .getContent();

        assertThat(actualNews).isEqualTo(expectedViews);
    }

    @ParameterizedTest
    @MethodSource("getFilterPageableListNewsListResponseNewsView")
    void checkGetByFilter(String partLike,
                          Pageable pageable,
                          List<News> newsList,
                          List<ResponseNewsView> responseNewsViewList) {
        PageImpl<News> newsPage = new PageImpl<>(newsList, pageable, newsList.size());
        Filter filter = new Filter(partLike);

        Optional.ofNullable(partLike)
                .ifPresentOrElse(
                        p -> doReturn(newsPage)
                                .when(repository).findAll(any(Specification.class), eq(pageable)),
                        () -> doReturn(newsPage)
                                .when(repository).findAll(pageable));
        IntStream.range(0, newsList.size())
                .forEach(i -> doReturn(responseNewsViewList.get(i))
                        .when(mapper).toResponseNewsView(newsList.get(i)));

        List<ResponseNewsView> actualViews =
                newsService
                        .getByFilter(filter, pageable)
                        .getContent();

        assertThat(actualViews).isEqualTo(responseNewsViewList);
    }

    @Test
    void checkCreate() {
        CreateNewsDto createNewsDto = getTestData(CreateNewsDto.class, POST_DTO_NEWS_01_JSON);
        ResponseNewsView expectedView = getTestData(ResponseNewsView.class, VIEW_NEWS_01_JSON);
        News newNews = getTestData(News.class, POST_DTO_NEWS_01_JSON);
        News news = getTestData(News.class, NEWS_01_JSON);

        doReturn(newNews)
                .when(mapper).toNews(createNewsDto);
        doReturn(news)
                .when(repository).save(newNews);
        doReturn(expectedView)
                .when(mapper).toResponseNewsView(news);

        ResponseNewsView actualView = newsService.create(createNewsDto);

        assertThat(actualView).isEqualTo(expectedView);
    }

    @Test
    void checkUpdate() {
        CreateNewsDto createNewsDto = getTestData(CreateNewsDto.class, PUT_DTO_NEWS_01_JSON);
        News oldNews = getTestData(News.class, NEWS_01_JSON);
        News updated = getTestData(News.class, UPDATED_NEWS_01_JSON);
        ResponseNewsView expectedView = getTestData(ResponseNewsView.class, VIEW_UPDATED_NEWS_01_JSON);

        Long newsId = oldNews.getId();

        doReturn(Optional.of(oldNews))
                .when(repository).findById(newsId);
        doReturn(updated)
                .when(mapper).merge(oldNews, createNewsDto);
        doReturn(updated)
                .when(repository).save(updated);
        doReturn(expectedView)
                .when(mapper).toResponseNewsView(updated);

        ResponseNewsView actualView = newsService.update(newsId, createNewsDto);

        assertThat(actualView).isEqualTo(expectedView);
    }

    @Test
    void checkUpdateThrows() {
        CreateNewsDto createNewsDto = getTestData(CreateNewsDto.class, PUT_DTO_NEWS_01_JSON);

        doReturn(Optional.empty())
                .when(repository).findById(any());

        assertThatThrownBy(() -> newsService.update(1L, createNewsDto))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void checkDelete() {
        Long id = 1L;

        newsService.delete(id);

        verify(repository, times(1))
                .deleteById(id);
    }
}
