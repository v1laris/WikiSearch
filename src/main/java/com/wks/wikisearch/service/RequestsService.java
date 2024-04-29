package com.wks.wikisearch.service;

import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class RequestsService {
    private static final String WIKIPEDIA_API_URL = "https://ru.wikipedia.org/w/api.php";

    public List<Map<String, String>> search(final String query) {
        RestTemplate restTemplate = new RestTemplate();

        String url = WIKIPEDIA_API_URL
                + "?action=query&list=search&srsearch="
                + query + "&format=json";

        String response = restTemplate.getForObject(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(response);
            JsonNode searchResults = root.path("query").path("search");

            List<Map<String, String>> resultList = new ArrayList<>();
            for (JsonNode result : searchResults) {
                String title = result.path("title").asText();
                String pageUrl = "https://ru.wikipedia.org/wiki/" + title.replace(" ", "_");

                Map<String, String> resultMap = new HashMap<>();
                resultMap.put("title", title);
                resultMap.put("url", pageUrl);

                resultList.add(resultMap);
            }
            return resultList;
        } catch (Exception e) {
            // Возвращаем пустой список в случае ошибки
            return new ArrayList<>();
        }
    }
}
