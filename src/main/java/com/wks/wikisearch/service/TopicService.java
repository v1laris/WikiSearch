package com.wks.wikisearch.service;

import com.wks.wikisearch.cache.CacheManager;
import com.wks.wikisearch.dto.TopicDTOWithArticles;
import com.wks.wikisearch.exception.ResourceAlreadyExistsException;
import com.wks.wikisearch.exception.ResourceNotFoundException;
import com.wks.wikisearch.model.CachePrimaryKeys;
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
    private final CacheService cacheService;

    public List<TopicDTOWithArticles> findAllTopics() {
        if (CacheManager.containsKey(CachePrimaryKeys.TOPIC_PRIMARY_KEY)) {
            return (List<TopicDTOWithArticles>) CacheManager.get(CachePrimaryKeys.TOPIC_PRIMARY_KEY);
        } else {
            List<Topic> topics = topicCustomRepository.findAllTopicsWithArticles();
            List<TopicDTOWithArticles> topicDTOs = new ArrayList<>();
            for (Topic topic : topics) {
                TopicDTOWithArticles topicDTO = Conversion.convertTopicToDTOWithArticles(topic);
                topicDTOs.add(topicDTO);
            }
            for (TopicDTOWithArticles topic : topicDTOs) {
                cacheService.addTopic(topic);
            }
            CacheManager.put(CachePrimaryKeys.TOPIC_PRIMARY_KEY, topicDTOs);
            return topicDTOs;
        }
    }

    public TopicDTOWithArticles findByName(final String name) {
        TopicDTOWithArticles topic = cacheService.getTopic(name);
        if (topic != null) {
            return topic;
        } else {
            if (topicRepository.existsByName(name)) {
                topic = Conversion
                        .convertTopicToDTOWithArticles(
                                topicCustomRepository
                                        .findTopicByName(name));
                cacheService.addTopic(topic);
                return topic;
            } else {
                throw new ResourceNotFoundException("Requested non-existent country");
            }
        }
    }

    public void saveTopic(final Topic topic) {
        if (!topicRepository.existsByName(topic.getName())) {
            if (CacheManager.containsKey(CachePrimaryKeys.TOPIC_PRIMARY_KEY)) {
                List<TopicDTOWithArticles> topics =
                        (List<TopicDTOWithArticles>)
                                CacheManager.get(CachePrimaryKeys.TOPIC_PRIMARY_KEY);
                topics.add(Conversion.convertTopicToDTOWithArticles(topic));
                CacheManager.put(CachePrimaryKeys.TOPIC_PRIMARY_KEY, topics);
            }
            topicRepository.save(topic);
        } else {
            throw new ResourceAlreadyExistsException("This topic already exists");
        }
    }

    public void addNewArticleByTopicName(final String topicName, final String articleTitle) {
        if (articleRepository.existsByTitle(articleTitle) || topicRepository.existsByName(topicName)) {
            cacheService.clearCacheItems(topicName, articleTitle);
            topicCustomRepository.addArticleToTopic(topicCustomRepository.findTopicByName(topicName).getId(),
                    articleCustomRepository.findArticleByTitle(articleTitle).getId());
        } else {
            throw new ResourceNotFoundException("This article or topic doesn't exist");
        }
    }

    public void deleteTopic(final String name) {
        if (topicRepository.existsByName(name)) {
            Topic topic = topicCustomRepository.findTopicByName(name);
            topicCustomRepository.deleteTopic(topic.getId());
            cacheService.removeTopic(name);
        } else {
            throw new ResourceNotFoundException("Error deleting non-existent topic");

        }
    }

    public void detachArticleByTopicName(final String topicName, final String articleTitle) {
        if (articleRepository.existsByTitle(articleTitle) && topicRepository.existsByName(topicName)) {
            cacheService.clearCacheItems(topicName, articleTitle);
            topicCustomRepository.detachArticleFromTopic(
                    topicCustomRepository
                            .findTopicByName(topicName)
                            .getId(),
                    articleCustomRepository.findArticleByTitle(articleTitle).getId());
        } else {
            throw new ResourceNotFoundException("This article or topic doesn't exist");
        }
    }

    public void updateTopic(final String topicOldName, final Topic topic) {
        cacheService.updateTopic(topicOldName, topic);
        Optional<Topic> temp = topicRepository.findById(topic.getId());
        if (temp.isPresent()) {
            topicCustomRepository.updateTopic(topic);
        } else {
            throw new ResourceNotFoundException("Error updating non-existent topic");
        }
    }
}
