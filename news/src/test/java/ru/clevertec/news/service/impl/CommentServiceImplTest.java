package ru.clevertec.news.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.clevertec.news.data.CreateCommentDto;
import ru.clevertec.news.data.Filter;
import ru.clevertec.news.data.ResponseComment;
import ru.clevertec.news.data.ResponseCommentNews;
import ru.clevertec.news.data.UpdateCommentDto;
import ru.clevertec.news.entity.Comment;
import ru.clevertec.news.entity.News;
import ru.clevertec.exception.handling.exception.CommentNotFoundException;
import ru.clevertec.news.mapper.CommentMapper;
import ru.clevertec.news.repository.CommentRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static ru.clevertec.news.util.TestDataUtil.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl service;

    @Mock
    private CommentRepository repository;

    @Mock
    private CommentMapper mapper;

    @Test
    void checkGetAllByNewsId() {
        Long newsId = 1L;
        Pageable pageable = Pageable.unpaged();
        List<Comment> comments =
                getListTestData(Comment.class, COMMENT_01_JSON, COMMENT_02_JSON);
        int total = comments.size();
        List<ResponseComment> expectedContent =
                getListTestData(ResponseComment.class, RESP_COMMENT_01_JSON, RESP_COMMENT_02_JSON);
        PageImpl<Comment> commentPage = new PageImpl<>(comments, pageable, total);

        IntStream.range(0, total)
                .forEach(i ->
                        doReturn(expectedContent.get(i))
                                .when(mapper).toResponse(comments.get(i)));
        doReturn(commentPage)
                .when(repository).findByNews_Id(newsId, pageable);

        List<ResponseComment> actualContent = service.getAllByNewsId(newsId, pageable)
                .getContent();

        assertThat(actualContent).isEqualTo(expectedContent);
    }

    @Test
    void checkGetById() {
        Comment comment = getTestData(Comment.class, COMMENT_01_JSON);
        ResponseCommentNews expectedComment = getTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_01_JSON);
        Long commentId = comment.getId();

        doReturn(Optional.of(comment))
                .when(repository).findById(commentId);
        doReturn(expectedComment)
                .when(mapper).toResponseWithNewsId(comment);

        ResponseCommentNews actualComment = service.getById(commentId);

        assertThat(actualComment).isEqualTo(expectedComment);
    }

    @Test
    void checkGetAll() {
        Pageable pageable = Pageable.unpaged();
        List<Comment> comments = getListTestData(Comment.class, COMMENT_01_JSON, COMMENT_02_JSON);
        PageImpl<Comment> commentPage = new PageImpl<>(comments, pageable, 2);
        List<ResponseCommentNews> expectedComments =
                getListTestData(ResponseCommentNews.class, RESP_COMMENT_01_JSON, RESP_COMMENT_NEWS_02_JSON);

        doReturn(commentPage)
                .when(repository).findAll(pageable);
        IntStream.range(0, comments.size())
                .forEach(i -> doReturn(expectedComments.get(i))
                        .when(mapper).toResponseWithNewsId(comments.get(i)));

        List<ResponseCommentNews> actualComments =
                service
                        .getAll(pageable)
                        .getContent();

        assertThat(actualComments).isEqualTo(expectedComments);
    }


    @Test
    void checkGetAllByFilter() {
        Pageable pageable = Pageable.unpaged();
        Filter filter = getTestData(Filter.class, FILTER_01_JSON);
        List<Comment> comments = getListTestData(Comment.class, COMMENT_01_JSON);
        PageImpl<Comment> commentPage = new PageImpl<>(comments, pageable, 1);
        List<ResponseCommentNews> expectedComments =
                getListTestData(ResponseCommentNews.class, RESP_COMMENT_01_JSON);

        doReturn(commentPage)
                .when(repository).findAll(any(Specification.class), eq(pageable));
        IntStream.range(0, comments.size())
                .forEach(i -> doReturn(expectedComments.get(i))
                        .when(mapper).toResponseWithNewsId(comments.get(i)));

        List<ResponseCommentNews> actualComments =
                service
                        .getAllByFilter(filter, pageable)
                        .getContent();

        assertThat(actualComments).isEqualTo(expectedComments);
    }


    @Test
    void checkGetAllByFilterEmpty() {
        Pageable pageable = Pageable.unpaged();
        Filter emptyFilter = new Filter(null);
        List<Comment> comments = getListTestData(Comment.class, COMMENT_01_JSON, COMMENT_02_JSON);
        PageImpl<Comment> commentPage = new PageImpl<>(comments, pageable, 2);
        List<ResponseCommentNews> expectedComments =
                getListTestData(ResponseCommentNews.class, RESP_COMMENT_01_JSON, RESP_COMMENT_NEWS_02_JSON);

        doReturn(commentPage)
                .when(repository).findAll(pageable);
        IntStream.range(0, comments.size())
                .forEach(i -> doReturn(expectedComments.get(i))
                        .when(mapper).toResponseWithNewsId(comments.get(i)));

        List<ResponseCommentNews> actualComments =
                service
                        .getAllByFilter(emptyFilter, pageable)
                        .getContent();

        assertThat(actualComments).isEqualTo(expectedComments);
    }

    @Test
    void checkCreate() {
        CreateCommentDto commentDto = getTestData(CreateCommentDto.class, CREATE_COMMENT_DTO_01_JSON);
        Comment comment = getTestData(Comment.class, COMMENT_01_JSON);
        News news = getTestData(News.class, NEWS_01_JSON);
        comment.setId(null);
        comment.setNews(news);
        Comment saved = getTestData(Comment.class, COMMENT_01_JSON);
        ResponseCommentNews expectedComment = getTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_01_JSON);

        doReturn(comment)
                .when(mapper).toComment(commentDto);
        doReturn(saved)
                .when(repository).save(comment);
        doReturn(expectedComment)
                .when(mapper).toResponseWithNewsId(saved);

        ResponseCommentNews actualResponse = service.create(commentDto);

        assertThat(actualResponse).isEqualTo(expectedComment);
    }

    @Test
    void checkUpdate() {
        Comment comment = getTestData(Comment.class, COMMENT_01_JSON);
        Long commentId = comment.getId();
        UpdateCommentDto update = getTestData(UpdateCommentDto.class, UPDATE_COMMENT_DTO_JSON);
        Comment updated = getTestData(Comment.class, UPDATED_COMMENT_01_JSON);
        ResponseCommentNews expected = getTestData(ResponseCommentNews.class, UPDATED_RESP_COMMENT_NEWS_01_JSON);

        doReturn(Optional.of(comment))
                .when(repository).findById(commentId);
        doReturn(updated)
                .when(mapper).update(update, comment);
        doReturn(updated)
                .when(repository).save(updated);
        doReturn(expected)
                .when(mapper).toResponseWithNewsId(updated);

        ResponseCommentNews actual = service.update(commentId, update);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkUpdateThrows() {
        Comment comment = getTestData(Comment.class, COMMENT_01_JSON);
        UpdateCommentDto dto = getTestData(UpdateCommentDto.class, UPDATE_COMMENT_DTO_JSON);
        Long commentId = comment.getId();

        doReturn(Optional.empty())
                .when(repository).findById(commentId);

        assertThatThrownBy(() -> service.update(commentId, dto))
                .isExactlyInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void checkDelete() {
        Long id = 1L;

        doNothing()
                .when(repository).deleteById(id);

        assertThatNoException()
                .isThrownBy(() -> service.delete(id));
    }
}
