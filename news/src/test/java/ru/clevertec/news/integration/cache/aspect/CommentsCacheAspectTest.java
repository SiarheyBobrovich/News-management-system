package ru.clevertec.news.integration.cache.aspect;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import ru.clevertec.news.cache.aspect.CommentsCacheAspect;
import ru.clevertec.news.data.CreateCommentDto;
import ru.clevertec.news.entity.Comment;
import ru.clevertec.news.entity.News;
import ru.clevertec.exception.handling.exception.CommentNotFoundException;
import ru.clevertec.news.repository.CommentRepository;
import ru.clevertec.news.service.CommentService;
import ru.clevertec.news.util.IntegrationTest;
import ru.clevertec.news.util.PostgresqlTestContainer;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static ru.clevertec.news.util.TestDataUtil.*;

@IntegrationTest
@TestPropertySource(properties = {
        "spring.cache.algorithm=lfu",
        "spring.cache.size=2",
})
class CommentsCacheAspectTest extends PostgresqlTestContainer {

    @Autowired
    private CommentService service;

    @MockBean
    private CommentRepository repository;

    @Autowired
    private CommentsCacheAspect commentsCacheAspect;

    @AfterEach
    void clear() {
        commentsCacheAspect.clear();
    }

    @Test
    void getCached() {
        Comment comment = getTestData(Comment.class, COMMENT_01_JSON);
        Long id = comment.getId();

        doReturn(Optional.of(comment))
                .when(repository).findById(id);

        service.getById(id);
        service.getById(id);

        verify(repository, times(1))
                .findById(id);
    }

    @Test
    void deleteFromCache() {
        Comment comment = getTestData(Comment.class, COMMENT_01_JSON);
        Long id = comment.getId();

        doReturn(Optional.of(comment), Optional.empty())
                .when(repository).findById(id);

        service.getById(id);
        service.delete(id);
        assertThatThrownBy(() -> service.getById(id))
                .isExactlyInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void putToCache() {
        Comment newComment = getTestData(Comment.class, NEW_COMMENT_01_JSON);
        Comment comment = getTestData(Comment.class, COMMENT_01_JSON);
        News news = getTestData(News.class, NEWS_01_JSON);
        comment.setNews(news);
        CreateCommentDto commentDto = getTestData(CreateCommentDto.class, CREATE_COMMENT_DTO_01_JSON);
        Long id = comment.getId();

        doReturn(comment)
                .when(repository).save(newComment);

        service.create(commentDto);
        service.getById(id);

        verify(repository, never())
                .findById(id);
    }
}
