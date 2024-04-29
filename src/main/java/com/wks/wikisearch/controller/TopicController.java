package com.wks.wikisearch.controller;


import com.wks.wikisearch.dto.TopicDTOWithArticles;
import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.service.TopicService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/topics")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TopicController {
    private final TopicService service;

    @GetMapping
    public List<TopicDTOWithArticles> findAllTopics() {
        return service.findAllTopics();
    }

    @PostMapping("save_topic")
    public ResponseEntity<String> saveTopic(@RequestBody final Topic topic) {
        service.saveTopic(topic);
        return new ResponseEntity<>("Topic saved successfully", HttpStatus.OK);
    }

    @GetMapping("/{name}")
    public ResponseEntity<TopicDTOWithArticles> findByName(@PathVariable final String name) {
        return ResponseEntity.ok(service.findByName(name));
    }

    @DeleteMapping("delete_topic/{name}")
    public ResponseEntity<String> deleteTopic(@PathVariable final String name) {
        service.deleteTopic(name);
        return new ResponseEntity<>("Topic deleted successfully", HttpStatus.CREATED);
    }

    @PutMapping("/{topicName}/articles/add_new")
    public ResponseEntity<String> addNewArticleByTopicName(
            @PathVariable final String topicName,
            @RequestParam final String articleTitle) {
        service.addNewArticleByTopicName(topicName, articleTitle);
        return new ResponseEntity<>("Successfully added new article to the topic", HttpStatus.OK);
    }

    @PutMapping("/{topicName}/articles/detach")
    public ResponseEntity<String> detachArticleFromTopicByName(
            @PathVariable final String topicName,
            @RequestParam final String articleTitle) {
        service.detachArticleByTopicName(topicName, articleTitle);
        return new ResponseEntity<>("Successfully detached article from the topic", HttpStatus.OK);
    }

    @PutMapping("update_topic/{topicOldName}")
    public ResponseEntity<Map<String, String>> updateTopic(@PathVariable final String topicOldName,
                                                           @RequestBody final Topic topic) {
        service.updateTopic(topicOldName, topic);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Topic updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
