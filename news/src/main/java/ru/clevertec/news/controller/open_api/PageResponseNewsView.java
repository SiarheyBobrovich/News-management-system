package ru.clevertec.news.controller.open_api;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.clevertec.news.data.ResponseNewsView;

import java.util.List;

class PageResponseNewsView extends PageImpl<ResponseNewsView> {

    public PageResponseNewsView(List<ResponseNewsView> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }
}
