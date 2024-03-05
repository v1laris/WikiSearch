package com.wks.wikisearch.controller;

import com.wks.wikisearch.servise.impl.RequestsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
@AllArgsConstructor
public class RequestsController {
    private final RequestsService service;

    @GetMapping("/{keyword}")
    public String search(@PathVariable String keyword) {
        return service.search(keyword);
    }
}
