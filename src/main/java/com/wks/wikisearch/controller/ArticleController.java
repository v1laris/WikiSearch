package com.wks.wikisearch.controller;

import com.wks.wikisearch.dto.ArticleDTO;
import com.wks.wikisearch.dto.ArticleDTOWithTopics;
import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.service.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@AllArgsConstructor
public class ArticleController {
    private final ArticleService service;

    @GetMapping
    public List<ArticleDTOWithTopics> findAllArticles() {
        return service.findAllArticles();
    }

    @PostMapping("save_article")
    public void saveArticle(@RequestBody Article article) {
        service.saveArticle(article);
    }

    @GetMapping("/{title}")
    public ArticleDTOWithTopics findByTitle(@PathVariable String title) {
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

    @PostMapping("/{articleTitle}/topics/add_new")
    public void addNewTopicByArticleName(@PathVariable String articleTitle, @RequestParam String topicName) {
        service.addNewTopicByArticleTitle(articleTitle, topicName);
    }

    @DeleteMapping("/{articleTitle}/topics/detach")
    public void detachTopicFromArticleByName(@PathVariable String articleTitle, @RequestParam String topicName){
        service.detachTopicByArticleName(articleTitle, topicName);
    }

}
