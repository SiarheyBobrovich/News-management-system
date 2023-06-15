package ru.clevertec.news.mapper;

import org.junit.jupiter.api.Test;
import ru.clevertec.news.data.CreateCommentDto;
import ru.clevertec.news.data.ResponseComment;
import ru.clevertec.news.data.ResponseCommentNews;
import ru.clevertec.news.data.UpdateCommentDto;
import ru.clevertec.news.entity.Comment;
import ru.clevertec.news.entity.News;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.clevertec.news.util.TestDataUtil.*;

class CommentMapperTest {

    private final CommentMapper mapper = new CommentMapperImpl();

    @Test
    void checkToResponse() {
        Comment comment = getTestData(Comment.class, COMMENT_01_JSON);
        ResponseComment responseComment = getTestData(ResponseComment.class, RESP_COMMENT_01_JSON);

        ResponseComment actual = mapper.toResponse(comment);

        assertThat(actual)
                .isEqualTo(responseComment);
    }

    @Test
    void checkToResponseWithNewsId() {
        Comment comment = getTestData(Comment.class, COMMENT_01_JSON);
        News news = getTestData(News.class, NEWS_01_JSON);
        comment.setNews(news);

        ResponseCommentNews actualResponse = mapper.toResponseWithNewsId(comment);

        assertThat(actualResponse)
                .extracting(
                        "id", "time", "text", "username", "newsId")
                .containsExactly(
                        comment.getId(), comment.getTime(), comment.getText(), comment.getUsername(), news.getId());
    }

    @Test
    void checkToComment() {
        CreateCommentDto commentDto = getTestData(CreateCommentDto.class, CREATE_COMMENT_DTO_01_JSON);
        News news = getTestData(News.class, NEWS_02_JSON);
        Comment comment = mapper.toComment(commentDto);

        assertThat(comment)
                .hasFieldOrPropertyWithValue("id", null)
                .hasFieldOrPropertyWithValue("time", null)
                .hasFieldOrPropertyWithValue("text", commentDto.text())
                .hasFieldOrPropertyWithValue("username", null)
                .hasFieldOrPropertyWithValue("news.id", news.getId());
    }

    @Test
    void checkMerge() {
        Comment comment = getTestData(Comment.class, COMMENT_01_JSON);
        UpdateCommentDto update = getTestData(UpdateCommentDto.class, UPDATE_COMMENT_DTO_JSON);
        Comment expectedComment = getTestData(Comment.class, UPDATED_COMMENT_01_JSON);

        Comment actualComment = mapper.update(update, comment);

        assertThat(actualComment).isEqualTo(expectedComment);
    }
}
