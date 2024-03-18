package com.wks.wikisearch.controller;

import com.wks.wikisearch.dto.*;
import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.service.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/topics")
@AllArgsConstructor
public class TopicController {
    private final TopicService service;

    @GetMapping
    public List<TopicDTOWithArticles> findAllTopics() {
        return service.findAllTopics();
    }

    @PostMapping("save_topic")
    public void saveTopic(@RequestBody Topic topic) {
        service.saveTopic(topic);
    }

    @GetMapping("/{name}")
    public TopicDTOWithArticles findByName(@PathVariable String name) {
        return service.findByName(name);
    }

    @DeleteMapping("delete_topic/{name}")
    public void deleteTopic(@PathVariable String name) {
        service.deleteTopic(name);
    }

    @PutMapping("/{topicName}/articles/add_new")
    public void addNewArticleByTopicName(@PathVariable String topicName, @RequestParam String articleTitle) {
        service.addNewArticleByTopicName(topicName, articleTitle);
    }

    @PutMapping("/{topicName}/articles/detach")
    public void detachArticleFromTopicByName(@PathVariable String topicName, @RequestParam String articleTitle) {
        service.detachArticleByTopicName(topicName, articleTitle);
    }

    @PutMapping("update_topic/{topicOldName}")
    public void updateTopic(@PathVariable String topicOldName, @RequestBody Topic topic) {
        service.updateTopic(topicOldName, topic);
    }
}
