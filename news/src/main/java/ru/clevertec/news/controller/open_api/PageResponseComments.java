package ru.clevertec.news.controller.open_api;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.clevertec.news.data.ResponseComment;

import java.util.List;

class PageResponseComments extends PageImpl<ResponseComment> {

    public PageResponseComments(List<ResponseComment> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }
}
