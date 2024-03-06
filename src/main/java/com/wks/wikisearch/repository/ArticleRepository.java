package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    void deleteByTitle(String title);
    Article findByTitle(String title);
    boolean existsByTitle(String title);


}
