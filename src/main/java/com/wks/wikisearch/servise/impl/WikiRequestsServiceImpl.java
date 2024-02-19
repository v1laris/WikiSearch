package com.wks.wikisearch.servise.impl;

import com.wks.wikisearch.model.Request;
import com.wks.wikisearch.servise.RequestsService;
import org.springframework.stereotype.Service;

@Service
public class WikiRequestsServiceImpl implements RequestsService {

    @Override
    public Request get(String word) {
        return Request.builder().query("You have searched " + word).build();
    }
}
