package ru.clevertec.news.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import ru.clevertec.news.data.ResponseComment;
import ru.clevertec.news.data.ResponseNews;
import ru.clevertec.news.entity.News;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class TestDataUtil {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public final String COMMENT_01_JSON = "/data/comment_01.json";
    public final String COMMENT_02_JSON = "/data/comment_02.json";
    public final String FILTER_01_JSON = "/data/filter_01.json";
    public final String FILTER_02_JSON = "/data/filter_02.json";
    public final String NEWS_01_JSON = "/data/news_01.json";
    public final String NEWS_02_JSON = "/data/news_02.json";
    public final String POST_DTO_NEWS_01_JSON = "/data/post_dto_news_01.json";
    public final String PUT_DTO_NEWS_01_JSON = "/data/put_dto_news_01.json";
    public final String RESP_COMMENT_01_JSON = "/data/resp_comment_01.json";
    public final String RESP_COMMENT_02_JSON = "/data/resp_comment_02.json";
    public final String UPDATED_NEWS_01_JSON = "/data/updated_news_01.json";
    public final String VIEW_NEWS_01_JSON = "/data/view_news_01.json";
    public final String VIEW_NEWS_02_JSON = "/data/view_news_02.json";
    public final String VIEW_UPDATED_NEWS_01_JSON = "/data/view_updated_news_01.json";
    public final String RESP_COMMENT_NEWS_01_JSON = "/data/resp_comment_news_01.json";
    public final String RESP_COMMENT_NEWS_02_JSON = "/data/resp_comment_news_02.json";
    public final String CREATE_COMMENT_DTO_01_JSON = "/data/create_comment_dto_01.json";
    public final String RESP_COMMENT_NEWS_03_JSON = "/data/resp_comment_news_03.json";
    public final String UPDATE_COMMENT_DTO_JSON = "/data/update_comment_dto.json";
    public final String UPDATED_COMMENT_01_JSON = "/data/updated_comment_01.json";
    public final String UPDATED_RESP_COMMENT_NEWS_01_JSON = "/data/updated_resp_comment_news_01.json";
    public final String CREATE_COMMENT_DTO_01_NEGATE_JSON = "/data/create_comment_dto_01_negate.json";
    public final String NEW_COMMENT_01_JSON = "/data/new_comment_01.json";
    public final String ADMIN_TOKEN = "Bearer admin";
    public final String JOURNALIST_TOKEN = "Bearer journalist";
    public final String SUBSCRIBER_TOKEN = "Bearer subscriber";

    @SneakyThrows
    public <T> T getTestData(Class<T> t, String filename) {
        InputStream resourceAsStream = TestDataUtil.class.getResourceAsStream(filename);
        Objects.requireNonNull(resourceAsStream, "Can not find resources/" + filename);
        return mapper.readValue(resourceAsStream, t);
    }

    @SneakyThrows
    public <T> List<T> getListTestData(Class<T> t, String... filename) {
        return Arrays.stream(filename)
                .map(TestDataUtil.class::getResourceAsStream)
                .map(stream -> {
                    Objects.requireNonNull(stream, "Can not find resources: " + Arrays.toString(filename));
                    try {
                        return mapper.readValue(stream, t);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

    public ResponseNews toResponse(News news, Page<ResponseComment> page) {
        return new ResponseNews(news.getId(), news.getTime(), news.getTitle(), news.getText(), page);
    }
}
