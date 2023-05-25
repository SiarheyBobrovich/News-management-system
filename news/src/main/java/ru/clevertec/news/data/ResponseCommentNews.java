package ru.clevertec.news.data;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ResponseCommentNews(Long id,
                                  LocalDateTime time,
                                  String text,
                                  String username,
                                  Long newsId) implements Serializable {
}
