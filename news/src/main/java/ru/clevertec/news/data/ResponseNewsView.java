package ru.clevertec.news.data;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ResponseNewsView(Long id,
                               LocalDateTime time,
                               String title,
                               String text,
                               String author
) implements Serializable {
}
