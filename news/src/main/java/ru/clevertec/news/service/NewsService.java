package ru.clevertec.news.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.news.data.CreateNewsDto;
import ru.clevertec.news.data.Filter;
import ru.clevertec.news.data.ResponseNewsView;
import ru.clevertec.news.entity.News;

public interface NewsService {

    /**
     * Ищет сохранённую {@link News} и возвращает {@link ResponseNewsView} с информацией о новости.
     *
     * @param id идентификатор новости
     * @return {@link ResponseNewsView} с информацией о {@link News}
     */
    ResponseNewsView getById(Long id);

    /**
     * Метод для получения страницы {@link ResponseNewsView} с информацией о новости
     * с заданными параметрами пагинации.
     *
     * @param pageable объект {@link Pageable}, содержащий информацию о номере и размере страницы
     * @return объект {@link Page} с {@link  ResponseNewsView} с информацией о новости
     */
    Page<ResponseNewsView> getAll(Pageable pageable);

    /**
     * Возвращает {@link Page} содержащий объекты {@link ResponseNewsView}
     * с заданными параметрами пагинации и фильтрации.
     *
     * @param filter   объект {@link Filter}, содержащий критерии фильтрации
     * @param pageable объект {@link  Pageable}, содержащий информацию о номере и размере страницы
     * @return объект {@link Page} содержащую объекты {@link ResponseNewsView} с информацией о новости
     */
    Page<ResponseNewsView> getByFilter(Filter filter,
                                       Pageable pageable);

    /**
     * Создаёт новую {@link News} созданную на основе заданного объекта {@link CreateNewsDto}.
     * и возвращает {@link ResponseNewsView} с информацией о новости
     *
     * @param dto объект {@link CreateNewsDto}, содержащий данные для создания новости
     * @return объект {@link ResponseNewsView} с информацией о {@link News}
     */
    ResponseNewsView create(CreateNewsDto dto);

    /**
     * Обновляет существующую {@link News} используя данные из {@link CreateNewsDto}.
     *
     * @param id  идентификатор новости
     * @param dto объект {@link CreateNewsDto}, содержащий данные для обновления новости
     * @return объект {@link ResponseNewsView} с информацией об обновлённой {@link News}
     */
    ResponseNewsView update(Long id,
                            CreateNewsDto dto);

    /**
     * Удаляет {@link News} по идентификатору.
     *
     * @param id идентификатор новости
     */
    void delete(Long id);
}
