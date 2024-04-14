package com.wks.wikisearch.service;

import com.wks.wikisearch.dto.TopicDTOWithArticles;
import com.wks.wikisearch.exception.ObjectAlreadyExistsException;
import com.wks.wikisearch.exception.ObjectNotFoundException;
import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.repository.ArticleCustomRepository;
import com.wks.wikisearch.repository.ArticleRepository;
import com.wks.wikisearch.repository.TopicCustomRepository;
import com.wks.wikisearch.repository.TopicRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class TopicService {
    private final TopicRepository topicRepository;
    private final TopicCustomRepository topicCustomRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCustomRepository articleCustomRepository;

    public List<TopicDTOWithArticles> findAllTopics() {
        return topicCustomRepository.findAllTopicsWithArticles().stream()
                .map(Conversion::convertTopicToDTOWithArticles)
                .collect(Collectors.toList());
    }


    public TopicDTOWithArticles findByName(final String name) {
        if (topicRepository.existsByName(name)) {
            return Conversion
                    .convertTopicToDTOWithArticles(
                            topicCustomRepository
                                    .findTopicByName(name));
        } else {
            throw new ObjectNotFoundException("Requested non-existent country");
        }
    }

    public void saveTopic(final Topic topic) {
        if (!topicRepository.existsByName(topic.getName())) {
            topicRepository.save(topic);
        } else {
            throw new ObjectAlreadyExistsException("This topic already exists");
        }
    }

    public void addNewArticleByTopicName(final String topicName, final String articleTitle) {
        if (articleRepository.existsByTitle(articleTitle) || topicRepository.existsByName(topicName)) {
            topicCustomRepository.addArticleToTopic(
                    topicCustomRepository.findTopicByName(topicName).getId(),
                    articleCustomRepository.findArticleByTitle(articleTitle).getId());
        } else {
            throw new ObjectNotFoundException("This article or topic doesn't exist");
        }
    }

    public void deleteTopic(final String name) {
        Topic topic = topicRepository.findByName(name)
                .orElseThrow(() -> new ObjectNotFoundException("Error deleting non-existent topic"));
        topicCustomRepository.deleteTopic(topic.getId());
    }


    public void detachArticleByTopicName(final String topicName, final String articleTitle) {
        if (articleRepository.existsByTitle(articleTitle)
                && topicRepository.existsByName(topicName)) {
            topicCustomRepository.detachArticleFromTopic(
                    topicCustomRepository
                            .findTopicByName(topicName)
                            .getId(),
                    articleCustomRepository.findArticleByTitle(articleTitle).getId());
        } else {
            throw new ObjectNotFoundException("This article or topic doesn't exist");
        }
    }

    public void updateTopic(final String topicOldName, final Topic topic) {
        if (!topicRepository.existsByName(topicOldName)) {
            throw new ObjectNotFoundException("Error updating non-existent topic");
        }
        if(!Objects.equals(topic.getName(), topicOldName)
                    && topicRepository.existsByName(topic.getName())) {
                throw new ObjectAlreadyExistsException("New name already exists");
        }
        topicCustomRepository.updateTopic(topic);
    }
}
