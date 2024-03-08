package com.wks.wikisearch.servise.impl;

import com.wks.wikisearch.dto.ArticleDTO;
import com.wks.wikisearch.dto.TopicDTO;
import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.repository.ArticleRepository;
import com.wks.wikisearch.repository.TopicRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;
    private final ArticleRepository articleRepository;

    public List<Topic> findAllTopics() {
        return topicRepository.findAll();
    }

    public Topic findByTitle(String name){
        return topicRepository.findByName(name);
    }

    public void saveTopic(Topic Topic) {
        if(topicRepository.existsByName(Topic.getName())) {
            throw new IllegalStateException("Topic exists.");
        }
        topicRepository.save(Topic);
    }

    @Transactional
    public void deleteTopic(String name) {
        boolean exists = topicRepository.existsByName(name);
        if(!exists) {
            throw new IllegalStateException(
                    "Topic with name " + name + " can not be deleted, because id does not exist");
        }
        topicRepository.deleteByName(name);
    }

    public void updateTopic(Topic Topic) {
        Topic topic = topicRepository.findById(Topic.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Topic with id " + Topic.getId() + "can not be updated, because it does not exist"));

    }

    public void addNewTopicByArticleName(String articleTitle, String topicName) {
        Topic topic = topicRepository.findByName(topicName);
        Article article = articleRepository.findByTitle(articleTitle);
        article.getTopics().add(topic);
        articleRepository.save(article);

        //article.getTopics().add(topic);
        /*if (country.getNations().stream().noneMatch(nationFunc -> nationFunc.getName().equals(nationRequest.getName()))) {
            if(nation != null) {
                nationRepository.save(nation);
                country.getNations().add(nation);
                countryRepository.save(country);
            }
            else {
                nationRepository.save(nationRequest);
                country.getNations().add(nationRequest);
                countryRepository.save(country);
            }
        } else {
            throw new IllegalStateException("nation with name " + nationRequest.getName() + " already exists in the country " + country.getName() + ".");
        }*/

    }

    private TopicDTO convertToDTO(Topic topic) {
        TopicDTO topicDTO = new TopicDTO();
        topicDTO.setId(topic.getId());
        topicDTO.setName(topic.getName());
        topicDTO.setArticles(topic.getArticles().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return topicDTO;
    }

    private ArticleDTO convertToDTO(Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(article.getId());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setContent(article.getContent());
        return articleDTO;
    }

    public List<TopicDTO> getAllTopicsWithArticles() {
        List<Topic> topics = topicRepository.findAll();
        return topics.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
