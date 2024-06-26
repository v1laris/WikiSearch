package com.wks.wikisearch.service;

import com.wks.wikisearch.dto.*;
import com.wks.wikisearch.model.User;
import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.model.Topic;

import java.util.ArrayList;
import java.util.List;


public class Conversion {

    private Conversion() {

    }

    public static TopicDTO convertTopicToDTO(final Topic topic) {
        TopicDTO topicDTO = new TopicDTO();
        topicDTO.setId(topic.getId());
        topicDTO.setName(topic.getName());
        return topicDTO;
    }

    public static TopicDTOWithArticles convertTopicToDTOWithArticles(final Topic topic) {
        if (topic == null) {
            return null;
        }
        TopicDTOWithArticles topicDTO = new TopicDTOWithArticles();
        topicDTO.setId(topic.getId());
        topicDTO.setName(topic.getName());

        if (topic.getArticles() != null) {
            List<Article> articles = topic.getArticles().stream().toList();
            List<ArticleDTO> articleDTOs = new ArrayList<>();
            for (Article article : articles) {
                articleDTOs.add(convertArticleToDTO(article));
            }
            topicDTO.setArticles(articleDTOs);
        }
        return topicDTO;
    }

    public static ArticleDTO convertArticleToDTO(final Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(article.getId());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setUrl(article.getUrl());
        return articleDTO;
    }

    public static ArticleDTOWithTopics convertArticleToDTOWithTopics(final Article article) {
        if (article == null) {
            return null;
        }
        ArticleDTOWithTopics articleDTOWithTopics = new ArticleDTOWithTopics();
        articleDTOWithTopics.setId(article.getId());
        articleDTOWithTopics.setTitle(article.getTitle());
        articleDTOWithTopics.setUrl(article.getUrl());

        if (article.getTopics() != null) {
            List<Topic> topics = article.getTopics().stream().toList();
            List<TopicDTO> topicDTOs = new ArrayList<>();
            for (Topic topic : topics) {
                topicDTOs.add(convertTopicToDTO(topic));
            }
            articleDTOWithTopics.setTopics(topicDTOs);
        }
        return articleDTOWithTopics;
    }

    public static CountryDTO convertCountryToDTO(final Country country) {
        CountryDTO countryDTO = new CountryDTO();

        countryDTO.setId(country.getId());
        countryDTO.setName(country.getName());

        return countryDTO;
    }

    public static CountryDTOWithUsers convertCountryToDTOWithUsers(final Country country) {
        if (country == null) {
            return null;
        }
        CountryDTOWithUsers countryDTOWithUsers = new CountryDTOWithUsers();

        countryDTOWithUsers.setId(country.getId());
        countryDTOWithUsers.setName(country.getName());
        if (country.getCountryUsers() != null) {
            List<User> users = country.getCountryUsers().stream().toList();
            List<UserDTO> userDTOs = new ArrayList<>();
            for (User user : users) {
                userDTOs.add(convertAppUserToDTO(user));
            }
            countryDTOWithUsers.setUsers(userDTOs);
        }

        return countryDTOWithUsers;
    }

    public static UserDTO convertAppUserToDTO(final User appUser) {
        UserDTO appUserDTO = new UserDTO();
        appUserDTO.setId(appUser.getId());
        appUserDTO.setFirstName(appUser.getFirstName());
        appUserDTO.setLastName(appUser.getLastName());
        appUserDTO.setEmail(appUser.getEmail());
        appUserDTO.setDateOfBirth(appUser.getDateOfBirth());
        return appUserDTO;
    }

    public static UserDTOWithCountry convertAppUserWithCountry(final User appUser) {
        UserDTOWithCountry appUserDTOWithCountry = new UserDTOWithCountry();

        appUserDTOWithCountry.setId(appUser.getId());
        appUserDTOWithCountry.setFirstName(appUser.getFirstName());
        appUserDTOWithCountry.setLastName(appUser.getLastName());
        appUserDTOWithCountry.setCountry(convertCountryToDTO(appUser.getCountry()));
        appUserDTOWithCountry.setDateOfBirth(appUser.getDateOfBirth());
        appUserDTOWithCountry.setEmail(appUser.getEmail());

        return appUserDTOWithCountry;
    }
}
