package ru.clevertec.news.entity.listener;

import jakarta.persistence.PrePersist;
import ru.clevertec.news.entity.Comment;

import java.time.LocalDateTime;

public class CommentListener {

    /**
     * Устанавливает время создания комментария
     *
     * @param comment Сохраняемый {@link Comment}
     */
    @PrePersist
    public void prePersist(Comment comment) {
        comment.setTime(LocalDateTime.now());
    }
}
