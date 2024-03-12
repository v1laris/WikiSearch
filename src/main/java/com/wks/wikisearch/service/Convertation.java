package com.wks.wikisearch.service;

import com.wks.wikisearch.dto.*;
import com.wks.wikisearch.model.AppUser;
import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.Country;
import com.wks.wikisearch.model.Topic;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class Convertation {
    public static TopicDTO convertTopicToDTO(Topic topic) {
        TopicDTO topicDTO = new TopicDTO();
        topicDTO.setId(topic.getId());
        topicDTO.setName(topic.getName());
        return topicDTO;
    }

    public static TopicDTOWithArticles convertTopicToDTOWithArticles(Topic topic) {
        TopicDTOWithArticles topicDTO = new TopicDTOWithArticles();
        topicDTO.setId(topic.getId());
        topicDTO.setName(topic.getName());

        List<Article> articles = topic.getArticles().stream().toList();
        List<ArticleDTO> articleDTOs = new ArrayList<>();
        for(Article article : articles) {
            articleDTOs.add(convertArticleToDTO(article));
        }
        topicDTO.setArticles(articleDTOs);
        return topicDTO;
    }

    public static ArticleDTO convertArticleToDTO(Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(article.getId());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setUrl(article.getUrl());
        return articleDTO;
    }

    public static ArticleDTOWithTopics convertArticleToDTOWithTopics(Article article) {
        ArticleDTOWithTopics articleDTOWithTopics = new ArticleDTOWithTopics();
        articleDTOWithTopics.setId(article.getId());
        articleDTOWithTopics.setTitle(article.getTitle());
        articleDTOWithTopics.setUrl(article.getUrl());

        List<Topic> topics = article.getTopics().stream().toList();
        List<TopicDTO> topicDTOs = new ArrayList<>();
        for(Topic topic : topics) {
            topicDTOs.add(convertTopicToDTO(topic));
        }
        articleDTOWithTopics.setTopics(topicDTOs);

        return articleDTOWithTopics;
    }

    public static CountryDTO convertCountryToDTO(Country country) {
        CountryDTO countryDTO = new CountryDTO();

        countryDTO.setId(country.getId());
        countryDTO.setName(country.getName());

        return countryDTO;
    }

    public static CountryDTOWithUsers convertCountryToDTOWithUsers(Country country) {
        CountryDTOWithUsers countryDTOWithUsers = new CountryDTOWithUsers();

        countryDTOWithUsers.setId(country.getId());
        countryDTOWithUsers.setName(country.getName());

        List<AppUser> users = country.getCountryUsers().stream().toList();
        List<AppUserDTO> userDTOs = new ArrayList<>();
        for(AppUser user : users) {
            userDTOs.add(convertAppUserToDTO(user));
        }
        countryDTOWithUsers.setUsers(userDTOs);

        return countryDTOWithUsers;
    }

    public static AppUserDTO convertAppUserToDTO(AppUser appUser) {
        AppUserDTO appUserDTO = new AppUserDTO();
        appUserDTO.setId(appUser.getId());
        appUserDTO.setFirstName(appUser.getFirstName());
        appUserDTO.setLastName(appUser.getLastName());
        appUserDTO.setEmail(appUser.getEmail());
        appUserDTO.setDateOfBirth(appUser.getDateOfBirth());
        appUserDTO.setAge(appUser.getAge());
        return appUserDTO;
    }

    public static AppUserDTOWithCountry convertAppUserWithCountry(AppUser appUser) {
        AppUserDTOWithCountry appUserDTOWithCountry = new AppUserDTOWithCountry();

        appUserDTOWithCountry.setId(appUser.getId());
        appUserDTOWithCountry.setFirstName(appUser.getFirstName());
        appUserDTOWithCountry.setLastName(appUser.getLastName());
        appUserDTOWithCountry.setCountry(convertCountryToDTO(appUser.getCountry()));
        appUserDTOWithCountry.setDateOfBirth(appUser.getDateOfBirth());
        appUserDTOWithCountry.setEmail(appUser.getEmail());
        appUserDTOWithCountry.setAge(appUser.getAge());

        return appUserDTOWithCountry;
    }


}
