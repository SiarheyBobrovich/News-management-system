package ru.clevertec.news.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.clevertec.news.data.CreateCommentDto;
import ru.clevertec.news.data.ResponseComment;
import ru.clevertec.news.data.ResponseCommentNews;
import ru.clevertec.news.data.UpdateCommentDto;
import ru.clevertec.news.entity.Comment;
import ru.clevertec.news.entity.News;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    /**
     * Конвертирует {@link Comment} в {@link ResponseComment} содержащий информацию о комментарии
     *
     * @param comment комментарий {@link Comment}
     * @return {@link ResponseCommentNews} c информацией о комментарии
     */
    ResponseComment toResponse(Comment comment);

    /**
     * Конвертирует {@link Comment} в {@link ResponseCommentNews} содержащий информацию о комментарии
     *
     * @param comment объект {@link Comment}
     * @return {@link ResponseCommentNews} c информацией о комментарии
     */
    @Mapping(target = "newsId", source = "news.id")
    ResponseCommentNews toResponseWithNewsId(Comment comment);

    /**
     * Конвертирует {@link CreateCommentDto} с информацией о комментарии в {@link Comment}
     *
     * @param commentDto объект {@link CreateCommentDto}, содержащий данные для создания комментария
     * @return {@link Comment}
     */
    @Mapping(target = "news", source = "newsId", qualifiedByName = "newsId")
    Comment toComment(CreateCommentDto commentDto);

    /**
     * Обновляет {@link Comment} по {@link UpdateCommentDto} с информацией о комментарии
     *
     * @param update  объект {@link UpdateCommentDto}, содержащий данные для обновления комментария
     * @param comment обновляемый {@link Comment}
     * @return {@link Comment}
     */
    @Mapping(target = "text", source = "text")
    Comment update(UpdateCommentDto update,
                   @MappingTarget Comment comment);

    /**
     * Создаёт новый объект {@link News} с идентификатором
     *
     * @param newsId идентификатор новости
     * @return новый объект {@link News} с идентификатором
     */
    @Named("newsId")
    default News getNews(Long newsId) {
        News news = new News();
        news.setId(newsId);
        return news;
    }
}
