package ru.clevertec.news.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.logging.annotation.Logging;
import ru.clevertec.news.controller.open_api.NewsOpenApi;
import ru.clevertec.news.data.CreateNewsDto;
import ru.clevertec.news.data.Filter;
import ru.clevertec.news.data.ResponseNewsView;
import ru.clevertec.news.service.NewsService;

@Logging
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/news")
public class NewsController implements NewsOpenApi {

    private final NewsService newsService;

    /**
     * Возвращает объект {@link ResponseEntity}, содержащий новость {@link ResponseNewsView}
     * по заданному идентификатору новости.
     *
     * @param id идентификатор новости
     * @return объект {@link ResponseEntity}, содержащий новость {@link ResponseNewsView} и статус ответа
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ResponseNewsView> getNewsById(@PathVariable Long id) {
        ResponseNewsView newsView = newsService.getById(id);

        return ResponseEntity.ok(newsView);
    }

    /**
     * Возвращает объект {@link ResponseEntity}, содержащий страницу новостей {@link ResponseNewsView}
     * с заданными параметрами пагинации.
     *
     * @param pageable объект {@link Pageable}, содержащий информацию о номере и размере страницы
     * @return объект {@link ResponseEntity}, содержащий {@link Page} с новостями {@link  ResponseNewsView}
     * и статус ответа
     */
    @Override
    @GetMapping
    public ResponseEntity<Page<ResponseNewsView>> getAllNews(@PageableDefault(20) Pageable pageable) {
        Page<ResponseNewsView> allNewsView = newsService.getAll(pageable);

        return ResponseEntity.ok(allNewsView);
    }

    /**
     * Возвращает объект {@link ResponseEntity},
     * содержащий страницу новостей {@link ResponseNewsView} с заданными параметрами пагинации и фильтрации.
     *
     * @param filter   объект {@link Filter}, содержащий критерии фильтрации
     * @param pageable объект {@link  Pageable}, содержащий информацию о номере и размере страницы
     * @return объект {@link  ResponseEntity}, содержащий страницу {@link Page} с новостями {@link ResponseNewsView}
     * и статус ответа
     */
    @Override
    @GetMapping("/filter")
    public ResponseEntity<Page<ResponseNewsView>> getAllNewsByFilter(@Valid Filter filter,
                                                                     @PageableDefault(20) Pageable pageable) {
        Page<ResponseNewsView> responseNewsViews = newsService.getByFilter(filter, pageable);

        return ResponseEntity.ok(responseNewsViews);
    }

    /**
     * Возвращает объект {@link ResponseEntity}, содержащий новость {@link ResponseNewsView},
     * созданную на основе заданного объекта {@link CreateNewsDto}.
     *
     * @param newsDto объект {@link CreateNewsDto}, содержащий данные для создания новости
     * @return объект {@link ResponseEntity}, содержащий новость {@link ResponseNewsView} и статус ответа
     */
    @Override
    @PostMapping
    @PreAuthorize("hasAuthority('news:write')")
    public ResponseEntity<ResponseNewsView> postNews(@Valid @RequestBody CreateNewsDto newsDto) {
        ResponseNewsView news = newsService.create(newsDto);

        return ResponseEntity.status(201).body(news);
    }

    /**
     * Возвращает объект {@link ResponseEntity}, содержащий новость {@link ResponseNewsView},
     * обновленный на основе заданного объекта {@link CreateNewsDto}.
     *
     * @param id      идентификатор новости
     * @param newsDto объект {@link CreateNewsDto}, содержащий данные для обновления новости
     * @return объект {@link ResponseEntity}, содержащий новость {@link ResponseNewsView} и статус ответа
     */
    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('news:write')")
    public ResponseEntity<ResponseNewsView> putNews(@PathVariable Long id,
                                                    @Valid @RequestBody CreateNewsDto newsDto) {
        ResponseNewsView responseNewsView = newsService.update(id, newsDto);

        return ResponseEntity.ok(responseNewsView);
    }

    /**
     * Возвращает объект {@link ResponseEntity}, содержащий статус ответа,
     * после удаления новости по заданному идентификатору.
     *
     * @param id идентификатор новости
     * @return объект {@link ResponseEntity}, содержащий статус ответа
     */
    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('news:delete')")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.delete(id);

        return ResponseEntity.ok().build();
    }
}
