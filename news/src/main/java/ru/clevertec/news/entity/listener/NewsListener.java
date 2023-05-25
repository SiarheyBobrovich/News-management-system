package ru.clevertec.news.entity.listener;


import jakarta.persistence.PrePersist;
import ru.clevertec.news.entity.News;

import java.time.LocalDateTime;

public class NewsListener {

    /**
     * Установливает время создания новости
     *
     * @param news Сохраняемая {@link News}
     */
    @PrePersist
    public void prePersist(News news) {
        news.setTime(LocalDateTime.now());
    }
}
