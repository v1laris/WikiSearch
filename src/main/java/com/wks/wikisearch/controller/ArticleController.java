package com.wks.wikisearch.controller;

import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.servise.impl.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@AllArgsConstructor
public class ArticleController {
    private final ArticleService service;

    @GetMapping
    public List<Article> findAllArticles() {

        return service.findAllArticles();
    }

    @PostMapping("save_article")
    public String saveArticle(@RequestBody Article article) {
        service.saveArticle(article);
        return "Article successfully saved";
    }

    @GetMapping("/{title}")
    public Article findByTitle(@PathVariable String title) {
        return service.findByTitle(title);
    }

    @PutMapping("update_article")
    public void updateCountry(@RequestBody Article article) {
        service.updateArticle(article);
    }

    @DeleteMapping("delete_article/{title}")
    public void deleteArticle(@PathVariable String title) {
        service.deleteArticle(title);
    }

}
