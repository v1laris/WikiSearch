package com.wks.wikisearch.controller;

import com.wks.wikisearch.service.RequestsService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class RequestsController {
    private final RequestsService service;

    @GetMapping("/{keyword}")
    public List<Map<String, String>> search(@PathVariable final String keyword) {
        return service.search(keyword);
    }
}
