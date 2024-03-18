package com.wks.wikisearch.service;

import com.wks.wikisearch.cache.CacheManager;
import com.wks.wikisearch.dto.ArticleDTO;
import com.wks.wikisearch.dto.ArticleDTOWithTopics;
import com.wks.wikisearch.dto.TopicDTO;
import com.wks.wikisearch.dto.TopicDTOWithArticles;
import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.repository.ArticleCustomRepository;
import com.wks.wikisearch.repository.ArticleRepository;
import com.wks.wikisearch.repository.TopicCustomRepository;
import com.wks.wikisearch.repository.TopicRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
@Transactional
public class TopicService {
    private final TopicRepository topicRepository;
    private final TopicCustomRepository topicCustomRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCustomRepository articleCustomRepository;
    private static final String ARTICLE_PRIMARY_KEY = "articles";
    private static final String TOPIC_PRIMARY_KEY = "topics";

    public List<TopicDTOWithArticles> findAllTopics() {
        if (CacheManager.containsKey(TOPIC_PRIMARY_KEY)) {
            return (List<TopicDTOWithArticles>) CacheManager.get(TOPIC_PRIMARY_KEY);
        } else {
            List<Topic> topics = topicCustomRepository.findAllTopicsWithArticles();
            List<TopicDTOWithArticles> topicDTOs = new ArrayList<>();
            for(Topic topic : topics) {
                TopicDTOWithArticles topicDTO = Conversion.convertTopicToDTOWithArticles(topic);
                topicDTOs.add(topicDTO);
            }
            for(TopicDTOWithArticles topic : topicDTOs){
                CacheManager.put(TOPIC_PRIMARY_KEY + "/" + topic.getName(), topic);
            }
            CacheManager.put(TOPIC_PRIMARY_KEY, topicDTOs);
            return topicDTOs;
        }
    }

    public TopicDTOWithArticles findByName(String name) {
        String cacheKey = TOPIC_PRIMARY_KEY + "/" + name;
        if (CacheManager.containsKey(cacheKey)) {
            return (TopicDTOWithArticles) CacheManager.get(cacheKey);
        } else {
            if(CacheManager.containsKey(TOPIC_PRIMARY_KEY)) {
                List<TopicDTOWithArticles> topics = (List<TopicDTOWithArticles>) CacheManager.get(TOPIC_PRIMARY_KEY);
                Optional<TopicDTOWithArticles> findResult = topics.stream()
                        .filter(topicDTOWithArticles -> topicDTOWithArticles
                                .getName().equals(name)).findFirst();
                if(findResult.isPresent()){
                    CacheManager.put(cacheKey, findResult.get());
                    return findResult.get();
                }
            }
            if(topicRepository.existsByName(name)) {
                TopicDTOWithArticles result = Conversion.convertTopicToDTOWithArticles(topicCustomRepository.findTopicByName(name));
                if (result != null) {
                    CacheManager.put(cacheKey, result);
                }
                return result;
            }
            return null;
        }
    }

    public void saveTopic(Topic topic) {
        if (topicRepository.existsByName(topic.getName())) {
            throw new IllegalStateException("Topic exists.");
        }
        if (CacheManager.containsKey(TOPIC_PRIMARY_KEY)) {
            List<TopicDTOWithArticles> topics = (List<TopicDTOWithArticles>) CacheManager.get(TOPIC_PRIMARY_KEY);
            topics.add(Conversion.convertTopicToDTOWithArticles(topic));
            CacheManager.put(TOPIC_PRIMARY_KEY, topics);
        }
        topicRepository.save(topic);
    }

    public void addNewArticleByTopicName(String topicName, String articleTitle) {
        if(!articleRepository.existsByTitle(articleTitle) || !topicRepository.existsByName(topicName)){
            return;
        }
        clearCacheItems(topicName, articleTitle);
        topicCustomRepository.addArticleToTopic(topicCustomRepository.findTopicByName(topicName).getId(),
                articleCustomRepository.findArticleByTitle(articleTitle).getId());
    }

    public void deleteTopic(String name) {
        String cacheKey = TOPIC_PRIMARY_KEY + "/" + name;
        CacheManager.remove(cacheKey);
        if(CacheManager.containsKey(TOPIC_PRIMARY_KEY)) {
            List<TopicDTOWithArticles> topics = (List<TopicDTOWithArticles>) CacheManager.get(TOPIC_PRIMARY_KEY);
            topics.removeIf(topicDTOWithArticles -> topicDTOWithArticles.getName().equals(name));
            CacheManager.put(TOPIC_PRIMARY_KEY, topics);
        }
        CacheManager.remove(ARTICLE_PRIMARY_KEY);
        if(topicRepository.existsByName(name)) {
            Topic topic = topicCustomRepository.findTopicByName(name);
            topicCustomRepository.deleteTopic(topic.getId());
        }
    }

    public void clearCacheItems(String topicName, String articleTitle) {
        String cacheKeyTopic = TOPIC_PRIMARY_KEY + "/" + topicName;
        String cacheKeyArticle = ARTICLE_PRIMARY_KEY + "/" + articleTitle;
        if(CacheManager.containsKey(cacheKeyTopic)){
            CacheManager.remove(cacheKeyTopic);
        }
        if(CacheManager.containsKey(cacheKeyArticle)){
            CacheManager.remove(cacheKeyArticle);
        }
        CacheManager.remove(TOPIC_PRIMARY_KEY);
        CacheManager.remove(ARTICLE_PRIMARY_KEY);
    }

    public void detachArticleByTopicName(String topicName, String articleTitle){
        clearCacheItems(topicName, articleTitle);
        topicCustomRepository.detachArticleFromTopic(topicCustomRepository.findTopicByName(topicName).getId(),
                articleCustomRepository.findArticleByTitle(articleTitle).getId());
    }

    public void updateTopic(String topicOldName, Topic topic) {
        String cacheKey = TOPIC_PRIMARY_KEY + "/" + topicOldName;
        if(CacheManager.containsKey(cacheKey)) {
            TopicDTOWithArticles temp = (TopicDTOWithArticles) CacheManager.get(cacheKey);
            if(!Objects.equals(temp.getName(), topic.getName())){
                temp.setName(topic.getName());
            }
            for(ArticleDTO article : temp.getArticles()){
                String articleCacheKey = ARTICLE_PRIMARY_KEY + "/" + article.getTitle();
                ArticleDTOWithTopics tempArticle = (ArticleDTOWithTopics) CacheManager.get(articleCacheKey);
                List<TopicDTO> tempArticleTopics = tempArticle.getTopics();
                tempArticleTopics.removeIf(topicDTO -> topicDTO.getName().equals(topicOldName));
                tempArticleTopics.add(Conversion.convertTopicToDTO(topic));
                tempArticle.setTopics(tempArticleTopics);
                CacheManager.put(articleCacheKey, tempArticle);
            }
            CacheManager.put(cacheKey, temp);

        }
        CacheManager.remove(TOPIC_PRIMARY_KEY);
        Optional<Topic> temp = topicRepository.findById(topic.getId());
        if(temp.isPresent()){
            topicCustomRepository.updateTopic(topic);
        }
    }
}
