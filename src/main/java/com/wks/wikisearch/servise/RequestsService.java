package com.wks.wikisearch.servise;

import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.node.ArrayNode;
@Service
@AllArgsConstructor
public class RequestsService {
    private final String WIKIPEDIA_API_URL = "https://ru.wikipedia.org/w/api.php";

    public String search(String query) {
        RestTemplate restTemplate = new RestTemplate();

        String url = WIKIPEDIA_API_URL + "?action=query&list=search&srsearch=" + query + "&format=json";

        String response = restTemplate.getForObject(url, String.class);

        // Парсим JSON-ответ
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(response);
            JsonNode searchResults = root.path("query").path("search");
            ArrayNode resultArray = mapper.createArrayNode();
            for (JsonNode result : searchResults) {
                String title = result.path("title").asText();
                String pageUrl = "https://ru.wikipedia.org/wiki/" + title.replace(" ", "_");
                resultArray.add(mapper.createObjectNode().put("title", title).put("url", pageUrl));
            }
            return resultArray.toString();
        } catch (Exception e) {
            return "Error occurred";
        }
    }
}