package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsByTitle(String title);

    @Query(value = "SELECT a.*, t.id AS topic_id, t.name AS topic_name "
            + "FROM article a "
            + "LEFT JOIN article_topic at ON a.id = at.article_id "
            + "LEFT JOIN topic t ON at.topic_id = t.id "
            + "WHERE a.title = ?", nativeQuery = true)
    Optional<Article> findArticleByTitle(String title);
}
