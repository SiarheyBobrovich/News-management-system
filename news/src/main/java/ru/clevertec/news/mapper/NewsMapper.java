package ru.clevertec.news.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.clevertec.news.data.CreateNewsDto;
import ru.clevertec.news.data.ResponseCommentNews;
import ru.clevertec.news.data.ResponseNewsView;
import ru.clevertec.news.entity.News;

@Mapper
public interface NewsMapper {

    /**
     * Конвертирует {@link News} в {@link ResponseNewsView} содержащий информацию о новости
     *
     * @param news объект {@link News}
     * @return {@link ResponseCommentNews} c информацией о новости
     */
    ResponseNewsView toResponseNewsView(News news);

    /**
     * Конвертирует {@link CreateNewsDto} содержащий информацию о новости в {@link News}
     *
     * @param createNewsDto объект {@link News}
     * @return {@link News} без идентификатора, времени создания и комментариев
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "time", ignore = true)
    @Mapping(target = "comments", ignore = true)
    News toNews(CreateNewsDto createNewsDto);

    /**
     * Обновляет {@link News} по информации из {@link CreateNewsDto}
     *
     * @param oldNews       обновляемая {@link News}
     * @param createNewsDto объект {@link CreateNewsDto} с информацией об объекте
     * @return {@link News} обновлённая новость
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "time", ignore = true)
    @Mapping(target = "comments", ignore = true)
    News merge(@MappingTarget News oldNews,
               CreateNewsDto createNewsDto);
}
