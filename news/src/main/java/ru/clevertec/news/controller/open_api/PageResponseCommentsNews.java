package ru.clevertec.news.controller.open_api;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.clevertec.news.data.ResponseCommentNews;

import java.util.List;

class PageResponseCommentsNews extends PageImpl<ResponseCommentNews> {
    public PageResponseCommentsNews(List<ResponseCommentNews> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }
}
