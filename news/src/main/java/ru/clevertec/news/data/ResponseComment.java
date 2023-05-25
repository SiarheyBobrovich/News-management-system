package ru.clevertec.news.data;

import java.time.LocalDateTime;

public record ResponseComment(Long id,
                              LocalDateTime time,
                              String text,
                              String username) {
}
