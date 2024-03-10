package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Long>, JpaRepository<Article, Long> {
    void deleteByTitle(String title);
    Article findByTitle(String title);
    boolean existsByTitle(String title);

    @Query("SELECT a.topics FROM Article a WHERE a.id = :articleId")
    Set<Topic> findTopicsByArticleId(@Param("articleId") Long articleId);
}
