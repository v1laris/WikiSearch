package com.wks.wikisearch.controller;

import com.wks.wikisearch.model.Request;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wks.wikisearch.servise.RequestsService;

@RestController
@RequestMapping("api/v1/wiki-search")
@AllArgsConstructor
public class RequestsController {

    private final RequestsService requestsService;
    @GetMapping("/get")
    public Request get(@RequestParam String word) {
        return requestsService.get(word);
    }
}
