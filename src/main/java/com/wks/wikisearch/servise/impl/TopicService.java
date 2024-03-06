package com.wks.wikisearch.servise.impl;

import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.repository.TopicRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class TopicService {
    private final TopicRepository TopicRepository;

    public List<Topic> findAllTopics() {
        return TopicRepository.findAll();
    }

    public Topic findByTitle(String name){
        return TopicRepository.findByName(name);
    }

    public void saveTopic(Topic Topic) {
        if(TopicRepository.existsByName(Topic.getName())) {
            throw new IllegalStateException("Topic exists.");
        }
        TopicRepository.save(Topic);
    }

    @Transactional
    public void deleteTopic(String name) {
        boolean exists = TopicRepository.existsByName(name);
        if(!exists) {
            throw new IllegalStateException(
                    "Topic with name " + name + " can not be deleted, because id does not exist");
        }
        TopicRepository.deleteByName(name);
    }

    public void updateTopic(Topic Topic) {
        Topic topic = TopicRepository.findById(Topic.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Topic with id " + Topic.getId() + "can not be updated, because it does not exist"));

    }
}
