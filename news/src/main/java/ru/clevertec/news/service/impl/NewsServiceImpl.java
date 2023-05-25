package ru.clevertec.news.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.news.data.CreateNewsDto;
import ru.clevertec.news.data.Filter;
import ru.clevertec.news.data.ResponseNewsView;
import ru.clevertec.news.entity.News;
import ru.clevertec.exception.handling.exception.NewsNotFoundException;
import ru.clevertec.news.mapper.NewsMapper;
import ru.clevertec.news.repository.NewsRepository;
import ru.clevertec.news.service.NewsService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;

    @Override
    @Cacheable(cacheNames = "news", key = "#p0")
    public ResponseNewsView getById(Long id) {
        return newsRepository.findById(id)
                .map(newsMapper::toResponseNewsView)
                .orElseThrow(() -> new NewsNotFoundException(id));
    }

    @Override
    public Page<ResponseNewsView> getAll(Pageable pageable) {
        return newsRepository.findAll(pageable)
                .map(newsMapper::toResponseNewsView);
    }

    @Override
    public Page<ResponseNewsView> getByFilter(Filter filter,
                                              Pageable pageable) {
        return Optional.ofNullable(filter.part())
                .map(part -> "%" + part + "%")
                .map(part -> Specification.anyOf(
                        (Specification<News>) (root, query, cb) -> cb.like(root.get("text"), part),
                        (Specification<News>) (root, query, cb) -> cb.like(root.get("title"), part)))
                .map(spec -> newsRepository.findAll(spec, pageable))
                .orElseGet(() -> newsRepository.findAll(pageable))
                .map(newsMapper::toResponseNewsView);
    }

    @Override
    @Transactional
    @CachePut(cacheNames = "news", key = "#result.id")
    public ResponseNewsView create(CreateNewsDto dto) {
        return Optional.of(dto)
                .map(newsMapper::toNews)
                .map(newsRepository::save)
                .map(newsMapper::toResponseNewsView)
                .orElseThrow();
    }

    @Override
    @Transactional
    @CachePut(cacheNames = "news", key = "#p0")
    public ResponseNewsView update(Long id,
                                   CreateNewsDto dto) {
        return newsRepository.findById(id)
                .map(n -> newsMapper.merge(n, dto))
                .map(newsRepository::save)
                .map(newsMapper::toResponseNewsView)
                .orElseThrow(() -> new NewsNotFoundException(id));
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "news", key = "#p0")
    public void delete(Long id) {
        newsRepository.deleteById(id);
    }
}
