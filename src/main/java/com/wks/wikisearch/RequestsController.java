package com.wks.wikisearch;

import com.model.Request;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/wiki-search")
public class RequestsController {
    @GetMapping("/get")
    public Request get(@RequestParam String word){
        // github конченый
        return Request.builder().query("You have searched " + word).build();
    }
}
