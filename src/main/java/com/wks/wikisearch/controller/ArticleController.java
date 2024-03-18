package com.wks.wikisearch.controller;

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

    @PutMapping("update_article/{articleOldTitle}")
    public void updateArticle(@PathVariable String articleOldTitle, @RequestBody Article article) {
        service.updateArticle(article, articleOldTitle);
    }

    @DeleteMapping("delete_article/{title}")
    public void deleteArticle(@PathVariable String title) {
        service.deleteArticle(title);
    }

    @PutMapping("/{articleTitle}/topics/add_new")
    public void addNewTopicByArticleName(@PathVariable String articleTitle, @RequestParam String topicName) {
        service.addNewTopicByArticleTitle(articleTitle, topicName);
    }

    @PutMapping("/{articleTitle}/topics/detach")
    public void detachTopicFromArticleByName(@PathVariable String articleTitle, @RequestParam String topicName){
        service.detachTopicByArticleName(articleTitle, topicName);
    }
}
