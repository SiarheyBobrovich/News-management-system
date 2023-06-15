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
import ru.clevertec.news.controller.open_api.CommentOpenApi;
import ru.clevertec.news.data.CreateCommentDto;
import ru.clevertec.news.data.Filter;
import ru.clevertec.news.data.ResponseComment;
import ru.clevertec.news.data.ResponseCommentNews;
import ru.clevertec.news.data.UpdateCommentDto;
import ru.clevertec.news.service.CommentService;

@Logging
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController implements CommentOpenApi {

    private final CommentService service;

    /**
     * Возвращает объект {@link ResponseEntity}, содержащий объект {@link ResponseCommentNews}
     * по заданному идентификатору.
     *
     * @param id идентификатор комментария
     * @return объект {@link ResponseEntity}, содержащий объект {@link ResponseCommentNews} и статус ответа
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ResponseCommentNews> getById(@PathVariable Long id) {
        ResponseCommentNews commentNews = service.getById(id);

        return ResponseEntity.ok(commentNews);
    }

    /**
     * Возвращает объект {@link ResponseEntity}, содержащий страницу объектов {@link ResponseCommentNews}
     * с заданными параметрами пагинации.
     *
     * @param pageable объект {@link Pageable}, содержащий информацию о номере и размере страницы
     * @return объект {@link ResponseEntity}, содержащий {@link Page} объектов {@link ResponseCommentNews} и статус ответа
     */
    @Override
    @GetMapping
    public ResponseEntity<Page<ResponseCommentNews>> getAll(@PageableDefault(20) Pageable pageable) {
        Page<ResponseCommentNews> commentNewsPage = service.getAll(pageable);

        return ResponseEntity.ok(commentNewsPage);
    }

    /**
     * Возвращает объект {@link ResponseEntity}, содержащий страницу комментариев {@link ResponseCommentNews}
     * с заданными параметрами пагинации и фильтрации.
     *
     * @param filter   объект {@link Filter}, содержащий критерии фильтрации
     * @param pageable объект {@link Pageable}, содержащий информацию о номере и размере страницы
     * @return объект {@link ResponseEntity}, содержащий страницу {@link Page} с комментариями {@link ResponseCommentNews}> и статус ответа
     */
    @Override
    @GetMapping("/filter")
    public ResponseEntity<Page<ResponseCommentNews>> getAllByFilter(@Valid Filter filter,
                                                                    @PageableDefault(20) Pageable pageable) {
        Page<ResponseCommentNews> commentNewsPage = service.getAllByFilter(filter, pageable);

        return ResponseEntity.ok(commentNewsPage);
    }

    /**
     * Возвращает объект {@link ResponseEntity}, содержащий страницу комментариев {@link ResponseComment}
     * по заданному идентификатору новости и параметрам пагинации.
     *
     * @param newsId   число, содержащее идентификатор новости
     * @param pageable объект {@link Pageable}, содержащий информацию о номере и размере страницы
     * @return объект {@link ResponseEntity}, содержащий страницу {@link Page} c комментариями {@link ResponseComment} и статус ответа
     */
    @Override
    @GetMapping("/news/{newsId}")
    public ResponseEntity<Page<ResponseComment>> getPageNewsComments(@PathVariable Long newsId,
                                                                     @PageableDefault(20) Pageable pageable) {
        Page<ResponseComment> commentPageByNewsId = service.getAllByNewsId(newsId, pageable);

        return ResponseEntity.ok(commentPageByNewsId);
    }

    /**
     * Возвращает объект {@link ResponseEntity}, содержащий комментарий {@link ResponseCommentNews},
     * созданный на основе заданного объекта {@link CreateCommentDto}<br/>
     * Публиковать комментарии могут пользователи с доступом: <strong>comments:write</strong>
     *
     * @param dto объект {@link CreateCommentDto}, содержащий данные для создания комментария
     * @return объект {@link ResponseEntity}, содержащий комментарий {@link ResponseCommentNews} и статус ответа
     */
    @Override
    @PostMapping
    @PreAuthorize("hasAuthority('comments:write')")
    public ResponseEntity<ResponseCommentNews> postComment(@RequestBody @Valid CreateCommentDto dto) {
        ResponseCommentNews newComment = service.create(dto);

        return ResponseEntity.status(201).body(newComment);
    }

    /**
     * Возвращает объект {@link ResponseEntity}, содержащий комментарий {@link ResponseCommentNews},
     * обновленный на основе заданного объекта {@link UpdateCommentDto}.
     * Изменять комментарий могут пользователи с доступом:<br/>
     * 1) <strong>admin</strong><br/>
     * 2) <strong>comments:write</strong> и имя совпадает
     *
     * @param id  идентификатор комментария
     * @param dto объект {@link UpdateCommentDto}, содержащий данные для обновления комментария
     * @return объект {@link ResponseEntity}, содержащий комментарий {@link ResponseCommentNews} и статус ответа
     */
    @Override
    @PutMapping("/{id}")
    @PreAuthorize("""
            hasAuthority('admin') ||
            (hasAuthority('comments:write') && @commentServiceImpl.getById(#id).username().equals(principal.username))
            """)
    public ResponseEntity<ResponseCommentNews> putComment(@PathVariable Long id,
                                                          @RequestBody @Valid UpdateCommentDto dto) {
        ResponseCommentNews updatedComment = service.update(id, dto);

        return ResponseEntity.ok(updatedComment);
    }

    /**
     * Возвращает объект {@link ResponseEntity},
     * содержащий статус ответа, после удаления комментария по заданному идентификатору.
     * Удалять комментарий могут пользователи с доступом:<br/>
     * 1) <strong>admin</strong><br/>
     * 2) <strong>comments:write</strong> и имя совпадает
     *
     * @param id идентификатор комментария
     * @return объект {@link ResponseEntity}, содержащий статус ответа
     */
    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("""
            hasAuthority('admin') ||
            (hasAuthority('comments:write') && @commentServiceImpl.getById(#id).username().equals(principal.username))
            """)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.ok().build();
    }
}
