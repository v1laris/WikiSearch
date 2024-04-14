package com.wks.wikisearch.serviceTests.utilsTests;

import com.wks.wikisearch.dto.*;
import com.wks.wikisearch.model.User;
import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.service.Conversion;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ConversionTests {

    @Test
    void testConvertTopicToDTO() {
        Topic topic = new Topic();
        topic.setId(1L);
        topic.setName("Test Topic");

        TopicDTO topicDTO = Conversion.convertTopicToDTO(topic);

        assertNotNull(topicDTO);
        assertEquals(topic.getId(), topicDTO.getId());
        assertEquals(topic.getName(), topicDTO.getName());
    }

    @Test
    void testConvertTopicToDTOWithArticles() {
        Topic topic = new Topic();
        topic.setId(1L);
        topic.setName("Test Topic");

        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setUrl("https://test.com/article");

        Set<Article> articles = new HashSet<>();
        articles.add(article);
        topic.setArticles(articles);

        TopicDTOWithArticles topicDTO = Conversion.convertTopicToDTOWithArticles(topic);

        assertNotNull(topicDTO);
        assertEquals(topic.getId(), topicDTO.getId());
        assertEquals(topic.getName(), topicDTO.getName());
        assertEquals(1, topicDTO.getArticles().size());
        assertEquals(article.getTitle(), topicDTO.getArticles().get(0).getTitle());
    }

    @Test
    void testConvertArticleToDTO() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setUrl("https://test.com/article");

        ArticleDTO articleDTO = Conversion.convertArticleToDTO(article);

        assertNotNull(articleDTO);
        assertEquals(article.getId(), articleDTO.getId());
        assertEquals(article.getTitle(), articleDTO.getTitle());
        assertEquals(article.getUrl(), articleDTO.getUrl());
    }

    @Test
    void testConvertArticleToDTOWithTopics() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Article");
        article.setUrl("https://test.com/article");

        Topic topic = new Topic();
        topic.setId(1L);
        topic.setName("Test Topic");

        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        article.setTopics(topics);

        ArticleDTOWithTopics articleDTO = Conversion.convertArticleToDTOWithTopics(article);

        assertNotNull(articleDTO);
        assertEquals(article.getId(), articleDTO.getId());
        assertEquals(article.getTitle(), articleDTO.getTitle());
        assertEquals(article.getUrl(), articleDTO.getUrl());
        assertEquals(1, articleDTO.getTopics().size());
        assertEquals(topic.getId(), articleDTO.getTopics().get(0).getId());
        assertEquals(topic.getName(), articleDTO.getTopics().get(0).getName());
    }

    @Test
    void testConvertCountryToDTO() {
        Country country = new Country();
        country.setId(1L);
        country.setName("Test Country");

        CountryDTO countryDTO = Conversion.convertCountryToDTO(country);

        assertNotNull(countryDTO);
        assertEquals(country.getId(), countryDTO.getId());
        assertEquals(country.getName(), countryDTO.getName());
    }

    @Test
    void testConvertCountryToDTOWithUsers() {
        Country country = new Country();
        country.setId(1L);
        country.setName("Test Country");

        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setDateOfBirth(LocalDate.of(1980, 1, 1));

        List<User> users = new ArrayList();
        users.add(user);
        country.setCountryUsers(users);

        CountryDTOWithUsers countryDTO = Conversion.convertCountryToDTOWithUsers(country);

        assertNotNull(countryDTO);
        assertEquals(country.getId(), countryDTO.getId());
        assertEquals(country.getName(), countryDTO.getName());
        assertEquals(1, countryDTO.getUsers().size());
        assertEquals(user.getId(), countryDTO.getUsers().get(0).getId());
        assertEquals(user.getFirstName(), countryDTO.getUsers().get(0).getFirstName());
        assertEquals(user.getLastName(), countryDTO.getUsers().get(0).getLastName());
        assertEquals(user.getEmail(), countryDTO.getUsers().get(0).getEmail());
        assertEquals(user.getDateOfBirth(), countryDTO.getUsers().get(0).getDateOfBirth());
    }

    @Test
    void testConvertAppUserToDTO() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setDateOfBirth(LocalDate.of(1980, 1, 1));

        UserDTO userDTO = Conversion.convertAppUserToDTO(user);

        assertNotNull(userDTO);
        assertEquals(user.getId(), userDTO.getId());
        assertEquals(user.getFirstName(), userDTO.getFirstName());
        assertEquals(user.getLastName(), userDTO.getLastName());
        assertEquals(user.getEmail(), userDTO.getEmail());
        assertEquals(user.getDateOfBirth(), userDTO.getDateOfBirth());
    }

    @Test
    void testConvertAppUserWithCountry() {
        Country country = new Country();
        country.setId(1L);
        country.setName("Test Country");

        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setDateOfBirth(LocalDate.of(1980, 1, 1));
        user.setCountry(country);

        UserDTOWithCountry userDTO = Conversion.convertAppUserWithCountry(user);

        assertNotNull(userDTO);
        assertEquals(user.getId(), userDTO.getId());
        assertEquals(user.getFirstName(), userDTO.getFirstName());
        assertEquals(user.getLastName(), userDTO.getLastName());
        assertEquals(user.getEmail(), userDTO.getEmail());
        assertEquals(user.getDateOfBirth(), userDTO.getDateOfBirth());
        assertNotNull(userDTO.getCountry());
        assertEquals(country.getId(), userDTO.getCountry().getId());
        assertEquals(country.getName(), userDTO.getCountry().getName());
    }
}
