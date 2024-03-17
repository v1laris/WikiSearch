package com.wks.wikisearch.controller;

import com.wks.wikisearch.dto.ArticleDTOWithTopics;
import com.wks.wikisearch.dto.TopicDTOWithArticles;
import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.service.ArticleService;
import com.wks.wikisearch.service.Conversion;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
@AllArgsConstructor
public class ArticleController {
    private final ArticleService service;
    private CacheManager cacheManager;

    @GetMapping
    public List<ArticleDTOWithTopics> findAllArticles() {
        String cacheKey = "/api/articles";
        if (CacheManager.containsKey(cacheKey)) {
            return (List<ArticleDTOWithTopics>)CacheManager.get(cacheKey);
        } else {
            List<ArticleDTOWithTopics> result = service.findAllArticles();
            for(ArticleDTOWithTopics article : result){
                CacheManager.put("/api/articles/" + article.getTitle(), article);
            }
            CacheManager.put(cacheKey, result);
            return result;
        }
    }

    @PostMapping("save_article")
    public void saveArticle(@RequestBody Article article) {
        Article isSaved = service.saveArticle(article);
        if(isSaved != null){
            if(CacheManager.containsKey("/api/articles")){
                List<ArticleDTOWithTopics> articles = (List<ArticleDTOWithTopics>) CacheManager.get("/api/articles");
                articles.add(Conversion.convertArticleToDTOWithTopics(article));
                CacheManager.put("/api/articles", articles);
            }
        }
    }

    @GetMapping("/{title}")
    public ArticleDTOWithTopics findByTitle(@PathVariable String title) {
        String cacheKey = "/api/articles/" + title;
        if (CacheManager.containsKey(cacheKey)) {
            return (ArticleDTOWithTopics) CacheManager.get(cacheKey);
        } else {
            if(CacheManager.containsKey("/api/articles")) {
                List<ArticleDTOWithTopics> articles = (List<ArticleDTOWithTopics>) CacheManager.get("/api/articles");
                Optional<ArticleDTOWithTopics> findResult = articles.stream()
                        .filter(ArticleDTOWithTopics -> ArticleDTOWithTopics
                                .getTitle().equals(title)).findFirst();
                if(findResult.isPresent()){
                    CacheManager.put(cacheKey, findResult.get());
                    return findResult.get();
                }
                else {
                    ArticleDTOWithTopics result = service.findByTitle(title);
                    if(result != null) {
                        CacheManager.put(cacheKey, result);
                    }
                    return result;
                }
            } else {
                ArticleDTOWithTopics result = service.findByTitle(title);
                if(result != null) {
                    CacheManager.put(cacheKey, result);
                }
                return result;
            }
        }
    }

    @PutMapping("update_article")
    public void updateArticle(@RequestBody Article article) {
        String cacheKey = "/api/article/" + article.getTopics();
        if(cacheManager.get(cacheKey) != null) {
            cacheManager.put(cacheKey, article);
            cacheManager.remove("/api/article");
        }
        service.updateArticle(article);
    }

    @DeleteMapping("delete_article/{title}")
    public void deleteArticle(@PathVariable String title) {
        String cacheKey = "/api/articles/" + title;
        CacheManager.remove(cacheKey);
        if(CacheManager.containsKey("/api/articles")) {
            List<ArticleDTOWithTopics> topics = (List<ArticleDTOWithTopics>) CacheManager.get("/api/articles");
            topics.removeIf(ArticleDTOWithTopics -> ArticleDTOWithTopics.getTitle().equals(title));
            CacheManager.put("/api/articles", topics);
        }
        CacheManager.remove("/api/topics");
        service.deleteArticle(title);
    }

    private void clearCacheItems(String topicName, String articleTitle) {
        String cacheKeyTopic = "/api/topics/" + topicName;
        String cacheKeyArticle = "/api/articles/" + articleTitle;
        if(CacheManager.containsKey(cacheKeyTopic)){
            CacheManager.remove(cacheKeyTopic);
        }
        if(CacheManager.containsKey(cacheKeyArticle)){
            CacheManager.remove(cacheKeyArticle);
        }
        CacheManager.remove("/api/topics");
        CacheManager.remove("/api/articles");
    }

    @PutMapping("/{articleTitle}/topics/add_new")
    public void addNewTopicByArticleName(@PathVariable String articleTitle, @RequestParam String topicName) {
        clearCacheItems(topicName, articleTitle);
        service.addNewTopicByArticleTitle(articleTitle, topicName);
    }

    @PutMapping("/{articleTitle}/topics/detach")
    public void detachTopicFromArticleByName(@PathVariable String articleTitle, @RequestParam String topicName){
        clearCacheItems(topicName, articleTitle);
        service.detachTopicByArticleName(articleTitle, topicName);
    }

}
