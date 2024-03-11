package com.wks.wikisearch.service;

import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.repository.ArticleCustomRepository;
import com.wks.wikisearch.repository.ArticleRepository;
import com.wks.wikisearch.repository.TopicCustomRepository;
import com.wks.wikisearch.repository.TopicRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;
    private final TopicCustomRepository topicCustomRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCustomRepository articleCustomRepository;

    public List<Topic> findAllTopics() {
        return topicCustomRepository.findAllTopicsWithArticles();
    }

    public Topic findByTitle(String name){
        return topicCustomRepository.findTopicByName(name);
    }

    public void saveTopic(Topic topic) {
        if(topicRepository.existsByName(topic.getName())) {
            throw new IllegalStateException("Topic exists.");
        }
        topicRepository.save(topic);
    }

    public void addNewArticleByTopicName(String topicName, String articleTitle) {
        if(!articleRepository.existsByTitle(articleTitle) || !topicRepository.existsByName(topicName)){
            return;
        }
        topicCustomRepository.addArticleToTopic(topicCustomRepository.findTopicByName(topicName).getId(),
                articleCustomRepository.findArticleByTitle(articleTitle).getId());
    }

    public void deleteTopic(String name) {
        Topic topic = topicCustomRepository.findTopicByName(name);
        topicCustomRepository.deleteTopic(topic.getId());
    }

    public void detachArticleByTopicName(String topicName, String articleTitle){
        topicCustomRepository.detachArticleFromTopic(topicCustomRepository.findTopicByName(topicName).getId(),
                articleCustomRepository.findArticleByTitle(articleTitle).getId());
    }

    public void updateTopic(Topic topic) {
        topicCustomRepository.updateTopic(topic);
    }
}
