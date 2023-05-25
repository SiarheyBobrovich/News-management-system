package ru.clevertec.news.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.clevertec.news.entity.News;

public interface NewsRepository extends JpaRepository<News, Long> {

    Page<News> findAll(Specification<News> specification,
                       Pageable pageable);
}
