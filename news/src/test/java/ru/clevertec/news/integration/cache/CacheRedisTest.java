package ru.clevertec.news.integration.cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import ru.clevertec.exception.handling.exception.CommentNotFoundException;
import ru.clevertec.news.data.CreateCommentDto;
import ru.clevertec.news.data.ResponseCommentNews;
import ru.clevertec.news.entity.Comment;
import ru.clevertec.news.entity.News;
import ru.clevertec.news.repository.CommentRepository;
import ru.clevertec.news.service.CommentService;
import ru.clevertec.news.util.IntegrationTest;
import ru.clevertec.news.util.PostgresqlTestContainer;
import ru.clevertec.news.util.RedisTestConfig;
import ru.clevertec.news.util.RedisTestContainer;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.clevertec.news.util.TestDataUtil.*;

@IntegrationTest
@Import(RedisTestConfig.class)
class CacheRedisTest extends PostgresqlTestContainer implements RedisTestContainer {

    @Autowired
    private CommentService service;

    @MockBean
    private CommentRepository repository;

    @Autowired
    private CacheManager manager;

    @AfterEach
    void clear() {
        Objects.requireNonNull(manager.getCache("comments"))
                .clear();
    }

    @Test
    void findById() {
        News news = getTestData(News.class, NEWS_01_JSON);
        Comment cached = getTestData(Comment.class, COMMENT_01_JSON);
        cached.setNews(news);
        ResponseCommentNews expected = getTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_01_JSON);
        Long id = expected.id();

        doReturn(Optional.of(cached))
                .when(repository).findById(id);

        service.getById(id);
        ResponseCommentNews actual = service.getById(id);

        verify(repository, times(1))
                .findById(1L);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void findByIdNull() {
        Long id = 1L;

        doReturn(Optional.empty(), Optional.empty())
                .when(repository).findById(id);

        assertThatThrownBy(() -> service.getById(id)).isExactlyInstanceOf(CommentNotFoundException.class);
        assertThatThrownBy(() -> service.getById(id)).isExactlyInstanceOf(CommentNotFoundException.class);

        verify(repository, times(2)).findById(id);
    }

    @Test
    void delete() {
        Comment cached = getTestData(Comment.class, COMMENT_01_JSON);
        Long id = cached.getId();

        doReturn(Optional.of(cached), Optional.empty())
                .when(repository).findById(id);
        doNothing()
                .when(repository).deleteById(id);

        service.getById(id);
        service.delete(id);

        assertThatThrownBy(() -> service.getById(id)).isExactlyInstanceOf(CommentNotFoundException.class);
        verify(repository, times(2))
                .findById(id);
    }

    @Test
    void save() {
        CreateCommentDto create = getTestData(CreateCommentDto.class, CREATE_COMMENT_DTO_01_JSON);
        Comment toSave = getTestData(Comment.class, COMMENT_01_JSON);
        toSave.setId(null);
        toSave.setTime(null);

        Comment comment = getTestData(Comment.class, COMMENT_01_JSON);
        Long id = comment.getId();

        doReturn(comment)
                .when(repository).save(toSave);

        service.create(create);

        ResponseCommentNews byId = service.getById(id);

        assertThat(byId).extracting(
                        "id", "text", "username")
                .containsExactly(
                        comment.getId(), comment.getText(), comment.getUsername());
        verify(repository, times(0))
                .findById(id);
    }
}
