package ru.clevertec.news.integration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import ru.clevertec.news.data.CreateCommentDto;
import ru.clevertec.news.data.Filter;
import ru.clevertec.news.data.ResponseComment;
import ru.clevertec.news.data.ResponseCommentNews;
import ru.clevertec.news.data.UpdateCommentDto;
import ru.clevertec.news.entity.Comment;
import ru.clevertec.news.entity.News;
import ru.clevertec.exception.handling.exception.CommentNotFoundException;
import ru.clevertec.exception.handling.exception.NewsNotFoundException;
import ru.clevertec.news.service.CommentService;
import ru.clevertec.news.util.IntegrationTest;
import ru.clevertec.news.util.PostgresqlTestContainer;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.clevertec.news.util.TestDataUtil.*;

@IntegrationTest
@Sql("classpath:sql/integration_news.sql")
class CommentServiceImplTest extends PostgresqlTestContainer {

    @Autowired
    private CommentService service;

    @Test
    void checkGetAllByNewsId() {
        Pageable pageable = Pageable.unpaged();
        News news = getTestData(News.class, NEWS_01_JSON);
        Long newsId = news.getId();
        List<ResponseComment> expectedComments = getListTestData(ResponseComment.class, RESP_COMMENT_01_JSON, RESP_COMMENT_02_JSON);

        List<ResponseComment> actualComments =
                service
                        .getAllByNewsId(newsId, pageable)
                        .getContent();

        assertThat(actualComments).isEqualTo(expectedComments);
    }

    @Test
    void checkGetById() {
        ResponseCommentNews expected = getTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_01_JSON);

        ResponseCommentNews actualComment = service.getById(expected.id());

        assertThat(actualComment).isEqualTo(expected);
    }

    @Test
    void checkGetAll() {
        Pageable pageable = Pageable.unpaged();
        List<ResponseCommentNews> expectedComments = getListTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_01_JSON, RESP_COMMENT_NEWS_02_JSON);

        List<ResponseCommentNews> actual =
                service
                        .getAll(pageable)
                        .getContent();

        assertThat(actual).isEqualTo(expectedComments);
    }

    @Test
    void checkGetByFilter() {
        Pageable pageable = Pageable.unpaged();
        Filter filter = getTestData(Filter.class, FILTER_01_JSON);
        List<ResponseCommentNews> expectedComments = getListTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_01_JSON);

        List<ResponseCommentNews> actualComments =
                service
                        .getAllByFilter(filter, pageable)
                        .getContent();

        assertThat(actualComments).isEqualTo(expectedComments);
    }

    @Test
    void checkGetByFilterEmpty() {
        Pageable pageable = Pageable.unpaged();
        Filter filter = new Filter(null);
        List<ResponseCommentNews> expectedComments =
                getListTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_01_JSON, RESP_COMMENT_NEWS_02_JSON);

        List<ResponseCommentNews> actualComments =
                service
                        .getAllByFilter(filter, pageable)
                        .getContent();

        assertThat(actualComments).isEqualTo(expectedComments);
    }

    @Test
    @WithMockUser(username = "user1")
    void createComment() {
        CreateCommentDto commentDto = getTestData(CreateCommentDto.class, CREATE_COMMENT_DTO_01_JSON);
        ResponseCommentNews expectedResponse = getTestData(ResponseCommentNews.class, RESP_COMMENT_NEWS_03_JSON);

        ResponseCommentNews actualResponse = service.create(commentDto);

        assertThat(actualResponse)
                .hasFieldOrPropertyWithValue("id", expectedResponse.id())
                .hasFieldOrPropertyWithValue("text", expectedResponse.text())
                .hasFieldOrPropertyWithValue("username", expectedResponse.username())
                .hasFieldOrPropertyWithValue("newsId", expectedResponse.newsId());
    }

    @Test
    @WithMockUser(username = "user1")
    void createNewCommentSetTime() {
        CreateCommentDto commentDto = getTestData(CreateCommentDto.class, CREATE_COMMENT_DTO_01_JSON);

        LocalDateTime before = LocalDateTime.now();
        ResponseCommentNews actualResponse = service.create(commentDto);
        LocalDateTime after = LocalDateTime.now();

        assertThat(actualResponse.time())
                .isAfter(before)
                .isBefore(after);
    }

    @Test
    void createNewNegateByNewsId() {
        CreateCommentDto commentDto = getTestData(CreateCommentDto.class, CREATE_COMMENT_DTO_01_NEGATE_JSON);

        assertThatThrownBy(() -> service.create(commentDto))
                .isExactlyInstanceOf(NewsNotFoundException.class);
    }

    @Test
    void checkUpdate() {
        UpdateCommentDto update = getTestData(UpdateCommentDto.class, UPDATE_COMMENT_DTO_JSON);
        Comment comment = getTestData(Comment.class, COMMENT_01_JSON);
        Long commentId = comment.getId();
        ResponseCommentNews expected = getTestData(ResponseCommentNews.class, UPDATED_RESP_COMMENT_NEWS_01_JSON);

        ResponseCommentNews actual = service.update(commentId, update);

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    void checkUpdateThrows() {
        long notExistedId = -1;
        UpdateCommentDto dto = getTestData(UpdateCommentDto.class, UPDATE_COMMENT_DTO_JSON);

        assertThatThrownBy(() -> service.update(notExistedId, dto))
                .isExactlyInstanceOf(CommentNotFoundException.class);
    }

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, 1, Long.MAX_VALUE})
    void checkDeleteComment(Long id) {
        assertThatNoException()
                .isThrownBy(() -> service.delete(id));
    }
}
