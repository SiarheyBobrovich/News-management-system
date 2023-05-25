package ru.clevertec.news.data;

import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ResponseNews(Long id,
                           LocalDateTime time,
                           String title,
                           String text,
                           Page<ResponseComment> comments
) implements Serializable {
}
