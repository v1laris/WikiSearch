package com.wks.wikisearch.controller;

import com.wks.wikisearch.dto.TopicDTO;
import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.servise.impl.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@AllArgsConstructor
public class TopicController {
    private final TopicService service;

    @GetMapping
    public List<Topic> findAllTopics() {
        return service.findAllTopics();
    }

    @PostMapping("save_topic")
    public String saveTopic(@RequestBody Topic topic) {
        service.saveTopic(topic);
        return "Topic successfully saved";
    }

    @GetMapping("/{title}")
    public Topic findByTitle(@PathVariable String title) {
        return service.findByTitle(title);
    }

    @PutMapping("update_topic")
    public void updateCountry(@RequestBody Topic topic) {
        service.updateTopic(topic);
    }

    @DeleteMapping("delete_topic/{name}")
    public void deleteTopic(@PathVariable String name) {
        service.deleteTopic(name);
    }

    @PostMapping("/{articleTitle}/topics/add_new")
    public void addNewTopicByArticleName(@PathVariable String articleTitle, @RequestParam String topicName) {
        service.addNewTopicByArticleName(articleTitle, topicName);
    }

    @GetMapping("/withArticles")
    public List<TopicDTO> getAllTopicsWithArticles() {
        return service.getAllTopicsWithArticles();
    }
}
