package com.wks.wikisearch.controller;

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

    @DeleteMapping("delete_topic/{name}")
    public void deleteTopic(@PathVariable String name) {
        service.deleteTopic(name);
    }

    @PostMapping("/{topicName}/articles/add_new")
    public void addNewArticleByTopicName(@PathVariable String topicName, @RequestParam String articleTitle) {
        service.addNewArticleByTopicName(topicName, articleTitle);
    }
    @DeleteMapping("/{topicName}/articles/detach")
    public void detachArticleFromTopicByName(@PathVariable String topicName, @RequestParam String articleTitle){
        service.detachArticleByTopicName(topicName, articleTitle);
    }

    @PutMapping("update_topic")
    public void updateCountry(@RequestBody Topic topic) {
        service.updateTopic(topic);
    }
}
