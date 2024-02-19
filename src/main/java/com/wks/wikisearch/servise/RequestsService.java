package com.wks.wikisearch.servise;

import com.wks.wikisearch.model.Request;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

public interface RequestsService {
    public Request get(@RequestParam String word);
}
