package ru.clevertec.news.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.news.data.CreateCommentDto;
import ru.clevertec.news.data.CreateNewsDto;
import ru.clevertec.news.data.Filter;
import ru.clevertec.news.data.ResponseComment;
import ru.clevertec.news.data.ResponseCommentNews;
import ru.clevertec.news.data.UpdateCommentDto;
import ru.clevertec.news.entity.Comment;

public interface CommentService {

    /**
     * Ищет сохранённый {@link Comment} и возвращает {@link ResponseCommentNews} с информацией о комментарии.
     *
     * @param id идентификатор комментария
     * @return {@link ResponseCommentNews} с информацией о {@link Comment}
     */
    ResponseCommentNews getById(Long id);


    /**
     * Ищет все {@link Comment} по идентификатору новости и
     * возвращает страницу с {@link ResponseComment} с информацией о комментарии, с заданными параметрами пагинации.
     *
     * @param newsId   идентификатор {@link ru.clevertec.news.entity.News}
     * @param pageable объект {@link Pageable}, содержащий информацию о номере и размере страницы
     * @return найденую {@link Page} с {@link ResponseComment} с информацией о комментарии
     */
    Page<ResponseComment> getAllByNewsId(Long newsId,
                                         Pageable pageable);

    /**
     * Метод для получения страницы {@link ResponseComment} с информацией о комментарии
     * с заданными параметрами пагинации.
     *
     * @param pageable объект {@link Pageable}, содержащий информацию о номере и размере страницы
     * @return объект {@link Page} с {@link ResponseComment} с информацией о комментарии
     */
    Page<ResponseCommentNews> getAll(Pageable pageable);

    /**
     * Возвращает {@link Page} содержащий объекты {@link ResponseCommentNews}
     * с заданными параметрами пагинации и фильтрации.
     *
     * @param filter   объект {@link Filter}, содержащий критерии фильтрации
     * @param pageable объект {@link  Pageable}, содержащий информацию о номере и размере страницы
     * @return объект {@link Page} содержащую объекты {@link ResponseCommentNews} с информацией о новости
     */
    Page<ResponseCommentNews> getAllByFilter(Filter filter,
                                             Pageable pageable);

    /**
     * Создаёт новый {@link Comment} на основе заданного объекта {@link CreateCommentDto}.
     * и возвращает {@link ResponseCommentNews} с информацией о созданном комментарии.
     *
     * @param dto объект {@link CreateNewsDto}, содержащий данные для создания новости
     * @return объект {@link ResponseCommentNews} с информацией о {@link Comment}
     */
    ResponseCommentNews create(CreateCommentDto dto);

    /**
     * Обновляет существующий {@link Comment} используя данные из {@link UpdateCommentDto}.
     *
     * @param id  идентификатор комментария
     * @param dto объект {@link UpdateCommentDto}, содержащий данные для обновления комментария
     * @return объект {@link ResponseCommentNews} с информацией об обновлённом {@link Comment}
     */
    ResponseCommentNews update(Long id, UpdateCommentDto dto);

    /**
     * Удаляет {@link Comment} по идентификатору.
     *
     * @param id идентификатор комментария
     */
    void delete(Long id);
}
