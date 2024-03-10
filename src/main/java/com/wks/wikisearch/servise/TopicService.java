package com.wks.wikisearch.servise;

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
    private final ArticleRepository articleRepository;
    private final TopicCustomRepository topicCustomRepository;
    private final ArticleCustomRepository articleCustomRepository;

    public List<Topic> findAllTopics() {
        return topicRepository.findAll();
    }

    public Topic findByTitle(String name){
        return topicCustomRepository.findTopicByName(name);
    }

    public void saveTopic(Topic Topic) {
        if(topicRepository.existsByName(Topic.getName())) {
            throw new IllegalStateException("Topic exists.");
        }
        topicRepository.save(Topic);
    }

    public void updateTopic(Topic Topic) {
        Topic topic = topicRepository.findById(Topic.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Topic with id " + Topic.getId() + "can not be updated, because it does not exist"));

    }

    public void addNewArticleByTopicName(String topicName, String articleTitle) {
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

}
