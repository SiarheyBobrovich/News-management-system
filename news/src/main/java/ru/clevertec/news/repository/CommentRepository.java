package ru.clevertec.news.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.clevertec.news.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByNews_Id(Long id,
                                Pageable pageable);

    Page<Comment> findAll(Specification<Comment> specification,
                          Pageable pageable);
}
