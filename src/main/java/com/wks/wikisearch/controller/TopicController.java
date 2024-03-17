package com.wks.wikisearch.controller;

import com.wks.wikisearch.dto.*;
import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.model.User;
import com.wks.wikisearch.service.Conversion;
import com.wks.wikisearch.service.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/topics")
@AllArgsConstructor
public class TopicController {
    private final TopicService service;
    private CacheManager cacheManager;

    @GetMapping
    public List<TopicDTOWithArticles> findAllTopics() {
        String cacheKey = "/api/topics";
        if (CacheManager.containsKey(cacheKey)) {
            return (List<TopicDTOWithArticles>) CacheManager.get(cacheKey);
        } else {
            List<TopicDTOWithArticles> result = service.findAllTopics();
            for(TopicDTOWithArticles topic : result){
                CacheManager.put("/api/topics/" + topic.getName(), topic);
            }
            CacheManager.put(cacheKey, result);
            return result;
        }
    }

    @PostMapping("save_topic")
    public void saveTopic(@RequestBody Topic topic) {
        Topic isSaved = service.saveTopic(topic);
        if(isSaved != null){
            if(CacheManager.containsKey("/api/topics")){
                List<TopicDTOWithArticles> topics = (List<TopicDTOWithArticles>) CacheManager.get("/api/topics");
                topics.add(Conversion.convertTopicToDTOWithArticles(topic));
                CacheManager.put("/api/topics", topics);
            }
        }
    }

    @GetMapping("/{name}")
    public TopicDTOWithArticles findByName(@PathVariable String name) {
        String cacheKey = "/api/topics/" + name;
        if (CacheManager.containsKey(cacheKey)) {
            return (TopicDTOWithArticles) CacheManager.get(cacheKey);
        } else {
            if(CacheManager.containsKey("/api/topics")) {
                List<TopicDTOWithArticles> topics = (List<TopicDTOWithArticles>) CacheManager.get("/api/topics");
                Optional<TopicDTOWithArticles> findResult = topics.stream()
                        .filter(TopicDTOWithArticles -> TopicDTOWithArticles
                                .getName().equals(name)).findFirst();
                if(findResult.isPresent()){
                    CacheManager.put(cacheKey, findResult.get());
                    return findResult.get();
                }
                else {
                    TopicDTOWithArticles result = service.findByName(name);
                    if(result != null) {
                        CacheManager.put(cacheKey, result);
                    }
                    return result;
                }
            } else {
                TopicDTOWithArticles result = service.findByName(name);
                if(result != null){
                    CacheManager.put(cacheKey, result);
                }
                return result;
            }
        }
    }

    @DeleteMapping("delete_topic/{name}")
    public void deleteTopic(@PathVariable String name) {
        String cacheKey = "/api/topics/" + name;
        CacheManager.remove(cacheKey);
        if(CacheManager.containsKey("/api/topics")) {
            List<TopicDTOWithArticles> topics = (List<TopicDTOWithArticles>) CacheManager.get("/api/topics");
            topics.removeIf(TopicDTOWithArticles -> TopicDTOWithArticles.getName().equals(name));
            CacheManager.put("/api/topics", topics);
        }
        CacheManager.remove("/api/articles");
        service.deleteTopic(name);
    }

    @PutMapping("/{topicName}/articles/add_new")
    public void addNewArticleByTopicName(@PathVariable String topicName, @RequestParam String articleTitle) {
        clearCacheItems(topicName, articleTitle);
        service.addNewArticleByTopicName(topicName, articleTitle);
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

    @PutMapping("/{topicName}/articles/detach")
    public void detachArticleFromTopicByName(@PathVariable String topicName, @RequestParam String articleTitle) {
        clearCacheItems(topicName, articleTitle);
        service.detachArticleByTopicName(topicName, articleTitle);
    }

    @PutMapping("update_topic/{topicOldName}")
    public void updateTopic(@PathVariable String topicOldName, @RequestBody Topic topic) {
        String cacheKey = "/api/topics/" + topicOldName;
        if(CacheManager.containsKey(cacheKey)) {
            TopicDTOWithArticles temp = (TopicDTOWithArticles) CacheManager.get(cacheKey);
            if(!Objects.equals(temp.getName(), topic.getName())){
                temp.setName(topic.getName());
            }
            for(ArticleDTO article : temp.getArticles()){
                String articleCacheKey = "/api/articles" + article.getTitle();
                ArticleDTOWithTopics tempArticle = (ArticleDTOWithTopics) CacheManager.get(articleCacheKey);
                List<TopicDTO> tempArticleTopics = tempArticle.getTopics();
                tempArticleTopics.removeIf(TopicDTO -> TopicDTO.getName().equals(topicOldName));
                tempArticleTopics.add(Conversion.convertTopicToDTO(topic));
                tempArticle.setTopics(tempArticleTopics);
                CacheManager.put(articleCacheKey, tempArticle);
            }
            CacheManager.put(cacheKey, temp);

        }
        CacheManager.remove("/api/topics");
        service.updateTopic(topic);
    }
}
