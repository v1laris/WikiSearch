package com.wks.wikisearch.controller;

import com.wks.wikisearch.dto.ArticleDTO;
import com.wks.wikisearch.dto.ArticleDTOWithTopics;
import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.service.ArticleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
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
    public ResponseEntity<String> saveArticle(@RequestBody final Article article) {
        service.saveArticle(article);
        return new ResponseEntity<>("Article added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/{title}")
    public ResponseEntity<ArticleDTOWithTopics> findByTitle(@PathVariable final String title) {
        return ResponseEntity.ok(service.findByTitle(title));
    }

    @PutMapping("update_article/{articleOldTitle}")
    public ResponseEntity<Map<String, String>> updateArticle(
            @PathVariable final String articleOldTitle,
            @RequestBody final Article article) {
        service.updateArticle(article, articleOldTitle);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Article updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("delete_article/{title}")
    public ResponseEntity<String> deleteArticle(@PathVariable final String title) {
        service.deleteArticle(title);
        return new ResponseEntity<>("Article deleted successfully", HttpStatus.OK);
    }

    @PutMapping("/{articleTitle}/topics/add_new")
    public ResponseEntity<String> addNewTopicByArticleName(
            @PathVariable final String articleTitle,
            @RequestParam final String topicName) {
        service.addNewTopicByArticleTitle(articleTitle, topicName);
        return new ResponseEntity<>("Successfully added new topic to the article", HttpStatus.OK);
    }

    @PutMapping("/{articleTitle}/topics/detach")
    public ResponseEntity<String> detachTopicFromArticleByName(
            @PathVariable final String articleTitle,
            @RequestParam final String topicName) {
        service.detachTopicByArticleName(articleTitle, topicName);
        return new ResponseEntity<>("Successfully detached topic from the article", HttpStatus.OK);
    }

    @PostMapping("/addListArticles")
    public ResponseEntity<List<ArticleDTO>> addMultipleCommand(@RequestBody List<Article> articlesList) {
        return new ResponseEntity<>(service.addMultipleArticles(articlesList), HttpStatus.OK);
    }
}
