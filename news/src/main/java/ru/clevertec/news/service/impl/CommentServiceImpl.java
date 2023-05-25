package ru.clevertec.news.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.news.data.CreateCommentDto;
import ru.clevertec.news.data.Filter;
import ru.clevertec.news.data.ResponseComment;
import ru.clevertec.news.data.ResponseCommentNews;
import ru.clevertec.news.data.UpdateCommentDto;
import ru.clevertec.news.entity.Comment;
import ru.clevertec.exception.handling.exception.NewsNotFoundException;
import ru.clevertec.exception.handling.exception.CommentNotFoundException;
import ru.clevertec.news.mapper.CommentMapper;
import ru.clevertec.news.repository.CommentRepository;
import ru.clevertec.news.service.CommentService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public Page<ResponseComment> getAllByNewsId(Long newsId,
                                                Pageable pageable) {
        return commentRepository.findByNews_Id(newsId, pageable)
                .map(commentMapper::toResponse);
    }

    @Override
    @Cacheable(cacheNames = "comments", key = "#p0")
    public ResponseCommentNews getById(Long id) {
        return commentRepository.findById(id)
                .map(commentMapper::toResponseWithNewsId)
                .orElseThrow(() -> new CommentNotFoundException(id));
    }

    @Override
    public Page<ResponseCommentNews> getAll(Pageable pageable) {
        return commentRepository.findAll(pageable)
                .map(commentMapper::toResponseWithNewsId);
    }

    @Override
    public Page<ResponseCommentNews> getAllByFilter(Filter filter, Pageable pageable) {
        return Optional.ofNullable(filter.part())
                .map(part -> "%" + part + "%")
                .map(part -> (Specification<Comment>) (root, query, cb) -> cb.like(root.get("text"), part))
                .map(spec -> commentRepository.findAll(spec, pageable))
                .orElseGet(() -> commentRepository.findAll(pageable))
                .map(commentMapper::toResponseWithNewsId);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = "comments", key = "#result.id()")
    public ResponseCommentNews create(CreateCommentDto dto) {
        try {
            return Optional.of(dto)
                    .map(commentMapper::toComment)
                    .map(commentRepository::save)
                    .map(commentMapper::toResponseWithNewsId)
                    .orElseThrow();
        } catch (DataAccessException e) {
            throw new NewsNotFoundException(dto.newsId());
        }
    }

    @Override
    @Transactional
    @CachePut(cacheNames = "comments", key = "#p0")
    public ResponseCommentNews update(Long id, UpdateCommentDto dto) {
        return commentRepository.findById(id)
                .map(current -> commentMapper.update(dto, current))
                .map(commentRepository::save)
                .map(commentMapper::toResponseWithNewsId)
                .orElseThrow(() -> new CommentNotFoundException(id));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "comments", key = "#p0")
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }
}
