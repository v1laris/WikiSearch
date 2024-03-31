package com.wks.wikisearch.service;

import com.wks.wikisearch.cache.CacheManager;
import com.wks.wikisearch.dto.*;
import com.wks.wikisearch.model.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CacheService {
    public void addCountry(final CountryDTOWithUsers country) {
        CacheManager.put(CachePrimaryKeys.COUNTRY_PRIMARY_KEY + country.getName(), country);
    }

    public void addUser(final UserDTOWithCountry user) {
        CacheManager.put(CachePrimaryKeys.USER_PRIMARY_KEY + user.getEmail(), user);
    }

    public void addArticle(final ArticleDTOWithTopics article) {
        CacheManager.put(CachePrimaryKeys.ARTICLE_PRIMARY_KEY + article.getTitle(), article);
    }

    public void addTopic(final TopicDTOWithArticles topic) {
        CacheManager.put(CachePrimaryKeys.TOPIC_PRIMARY_KEY + topic.getName(), topic);
    }

    public CountryDTOWithUsers getCountry(final String countryName) {
        String cacheKey = CachePrimaryKeys.COUNTRY_PRIMARY_KEY + countryName;
        CountryDTOWithUsers cachedValue = (CountryDTOWithUsers) CacheManager.get(cacheKey);
        if (cachedValue != null) {
            return cachedValue;
        } else {
            if (CacheManager.containsKey(CachePrimaryKeys.COUNTRY_PRIMARY_KEY)) {
                List<CountryDTOWithUsers> countries =
                        (List<CountryDTOWithUsers>)
                                CacheManager.get(CachePrimaryKeys.COUNTRY_PRIMARY_KEY);
                Optional<CountryDTOWithUsers> findResult = countries.stream()
                        .filter(countryDTOWithUsers -> countryDTOWithUsers
                                .getName().equals(countryName)).findFirst();
                if (findResult.isPresent()) {
                    CacheManager.put(cacheKey, findResult.get());
                    return findResult.get();
                }

            }
            return null;
        }
    }

    public UserDTOWithCountry getUser(final String email) {
        String cacheKey = CachePrimaryKeys.USER_PRIMARY_KEY + email;
        UserDTOWithCountry cachedValue = (UserDTOWithCountry) CacheManager.get(cacheKey);
        if (cachedValue != null) {
            return cachedValue;
        } else {
            if (CacheManager.containsKey(CachePrimaryKeys.USER_PRIMARY_KEY)) {
                List<UserDTOWithCountry> users =
                        (List<UserDTOWithCountry>) CacheManager
                                .get(CachePrimaryKeys.USER_PRIMARY_KEY);
                Optional<UserDTOWithCountry> findResult = users.stream()
                        .filter(userDTOWithCountry -> userDTOWithCountry
                                .getEmail().equals(email)).findFirst();
                if (findResult.isPresent()) {
                    CacheManager.put(cacheKey, findResult.get());
                    return findResult.get();
                }
            }
            return null;
        }
    }

    public TopicDTOWithArticles getTopic(final String name) {
        String cacheKey = CachePrimaryKeys.TOPIC_PRIMARY_KEY + name;
        if (CacheManager.containsKey(cacheKey)) {
            return (TopicDTOWithArticles) CacheManager.get(cacheKey);
        } else {
            if (CacheManager.containsKey(CachePrimaryKeys.TOPIC_PRIMARY_KEY)) {
                List<TopicDTOWithArticles> topics =
                        (List<TopicDTOWithArticles>)
                                CacheManager.get(CachePrimaryKeys.TOPIC_PRIMARY_KEY);
                Optional<TopicDTOWithArticles> findResult = topics.stream()
                        .filter(topicDTOWithArticles -> topicDTOWithArticles
                                .getName().equals(name)).findFirst();
                if (findResult.isPresent()) {
                    CacheManager.put(cacheKey, findResult.get());
                    return findResult.get();
                }
            }
            return null;
        }
    }

    public ArticleDTOWithTopics getArticle(final String title) {
        ArticleDTOWithTopics article =
                (ArticleDTOWithTopics)
                        CacheManager.get(CachePrimaryKeys.ARTICLE_PRIMARY_KEY + title);
        if (article != null) {
            return article;
        } else {
            if (CacheManager.containsKey(CachePrimaryKeys.ARTICLE_PRIMARY_KEY)) {
                List<ArticleDTOWithTopics> articles =
                        (List<ArticleDTOWithTopics>) CacheManager
                                .get(CachePrimaryKeys.ARTICLE_PRIMARY_KEY);
                Optional<ArticleDTOWithTopics> findResult = articles.stream()
                        .filter(articleDTOWithTopics -> articleDTOWithTopics
                                .getTitle().equals(title)).findFirst();
                if (findResult.isPresent()) {
                    addArticle(findResult.get());
                    return findResult.get();
                }
            }
            return null;
        }
    }

    public void removeCountry(final String name) {
        CountryDTOWithUsers country =
                (CountryDTOWithUsers)
                        CacheManager.get(CachePrimaryKeys.COUNTRY_PRIMARY_KEY + name);
        if (country != null) {
            String cacheKey = CachePrimaryKeys.COUNTRY_PRIMARY_KEY + name;
            CacheManager.remove(cacheKey);
            if (CacheManager.containsKey(CachePrimaryKeys.COUNTRY_PRIMARY_KEY)) {
                List<CountryDTOWithUsers> countries =
                        (List<CountryDTOWithUsers>)
                                CacheManager.get(CachePrimaryKeys.COUNTRY_PRIMARY_KEY);
                countries.removeIf(countryDTOWithUsers -> countryDTOWithUsers.getName().equals(name));
                CacheManager.put(CachePrimaryKeys.COUNTRY_PRIMARY_KEY, countries);
            }
            for (UserDTO userDTO : country.getUsers()) {
                CacheManager.remove(CachePrimaryKeys.USER_PRIMARY_KEY + userDTO.getEmail());
            }
        }
    }

    public void removeUser(final String email) {
        UserDTOWithCountry user =
                (UserDTOWithCountry)
                        CacheManager.get(CachePrimaryKeys.COUNTRY_PRIMARY_KEY + email);
        if (user != null) {
            CacheManager.remove(CachePrimaryKeys.USER_PRIMARY_KEY + email);
            if (CacheManager.containsKey(CachePrimaryKeys.USER_PRIMARY_KEY)) {
                List<UserDTOWithCountry> users =
                        (List<UserDTOWithCountry>) CacheManager.get(CachePrimaryKeys.USER_PRIMARY_KEY);
                users.removeIf(userDTOWithCountry -> userDTOWithCountry.getEmail().equals(email));
                CacheManager.put(CachePrimaryKeys.USER_PRIMARY_KEY, users);
            }
            CacheManager.remove(CachePrimaryKeys.COUNTRY_PRIMARY_KEY);
            CacheManager.remove(
                    CachePrimaryKeys.COUNTRY_PRIMARY_KEY
                            + user.getCountry().getName());
        }
    }

    public void removeTopic(final String name) {
        TopicDTOWithArticles topic =
                (TopicDTOWithArticles)
                        CacheManager.get(CachePrimaryKeys.TOPIC_PRIMARY_KEY + name);
        if (topic != null) {
            CacheManager.remove(CachePrimaryKeys.TOPIC_PRIMARY_KEY + topic.getName());
            if (CacheManager.containsKey(CachePrimaryKeys.TOPIC_PRIMARY_KEY)) {
                List<TopicDTOWithArticles> topics =
                        (List<TopicDTOWithArticles>)
                                CacheManager.get(CachePrimaryKeys.TOPIC_PRIMARY_KEY);
                topics.removeIf(topicDTOWithArticles -> topicDTOWithArticles
                        .getName()
                        .equals(topic.getName()));
                CacheManager.put(CachePrimaryKeys.TOPIC_PRIMARY_KEY, topics);
            }
            CacheManager.remove(CachePrimaryKeys.ARTICLE_PRIMARY_KEY);
            for (ArticleDTO article : topic.getArticles()) {
                CacheManager.remove(CachePrimaryKeys.ARTICLE_PRIMARY_KEY + article.getTitle());
            }
        }
    }

    public void removeArticle(final String title) {
        ArticleDTOWithTopics article =
                (ArticleDTOWithTopics)
                        CacheManager.get(CachePrimaryKeys.ARTICLE_PRIMARY_KEY + title);
        if (article != null) {
            CacheManager.remove(CachePrimaryKeys.ARTICLE_PRIMARY_KEY + title);
            if (CacheManager.containsKey(CachePrimaryKeys.ARTICLE_PRIMARY_KEY)) {
                List<ArticleDTOWithTopics> topics =
                        (List<ArticleDTOWithTopics>)
                                CacheManager.get(CachePrimaryKeys.ARTICLE_PRIMARY_KEY);
                topics.removeIf(articleDTOWithTopics -> articleDTOWithTopics.getTitle().equals(title));
                CacheManager.put(CachePrimaryKeys.ARTICLE_PRIMARY_KEY, topics);
            }
            CacheManager.remove(CachePrimaryKeys.TOPIC_PRIMARY_KEY);
            for (TopicDTO topic : article.getTopics()) {
                CacheManager.remove(CachePrimaryKeys.TOPIC_PRIMARY_KEY + topic.getName());
            }
        }
    }

    public void updateTopic(final String topicOldName, final Topic topic) {
        if (CacheManager.containsKey(CachePrimaryKeys.TOPIC_PRIMARY_KEY + topic.getName())) {
            return;
        }
        TopicDTOWithArticles cachedTopic =
                (TopicDTOWithArticles)
                        CacheManager.get(CachePrimaryKeys.TOPIC_PRIMARY_KEY + topicOldName);
        if (cachedTopic != null) {
            if (!Objects.equals(cachedTopic.getName(), topic.getName())) {
                cachedTopic.setName(topic.getName());
            }
            // updating articles that contain this topic
            for (ArticleDTO article : cachedTopic.getArticles()) {
                String articleCacheKey = CachePrimaryKeys.ARTICLE_PRIMARY_KEY + article.getTitle();
                ArticleDTOWithTopics tempArticle = (ArticleDTOWithTopics) CacheManager.get(articleCacheKey);
                List<TopicDTO> tempArticleTopics = tempArticle.getTopics();
                tempArticleTopics.removeIf(topicDTO -> topicDTO.getName().equals(topicOldName));
                tempArticleTopics.add(Conversion.convertTopicToDTO(topic));
                tempArticle.setTopics(tempArticleTopics);
                CacheManager.put(articleCacheKey, tempArticle);
            }
            CacheManager.put(CachePrimaryKeys.TOPIC_PRIMARY_KEY + cachedTopic.getName(), cachedTopic);

        }
        if (CacheManager.containsKey(CachePrimaryKeys.TOPIC_PRIMARY_KEY)) {
            List<TopicDTOWithArticles> topics =
                    (List<TopicDTOWithArticles>)
                            CacheManager.get(CachePrimaryKeys.TOPIC_PRIMARY_KEY);
            topics.removeIf(topicDTOWithArticles -> topicDTOWithArticles
                    .getName()
                    .equals(topicOldName));
            topics.add(cachedTopic);
            CacheManager.put(CachePrimaryKeys.TOPIC_PRIMARY_KEY, topics);
        }
        CacheManager.remove(CachePrimaryKeys.ARTICLE_PRIMARY_KEY);
    }

    public void updateArticle(final String articleOldTitle, final Article article) {
        String cacheKey = CachePrimaryKeys.ARTICLE_PRIMARY_KEY + articleOldTitle;
        if (CacheManager.containsKey(cacheKey)) {
            ArticleDTOWithTopics temp = (ArticleDTOWithTopics) CacheManager.get(cacheKey);
            if (article.getTitle() != null) {
                temp.setTitle(article.getTitle());
            }
            if (article.getUrl() != null) {
                temp.setUrl(article.getUrl());
            }
            for (TopicDTO topic : temp.getTopics()) {
                String topicCacheKey = CachePrimaryKeys.TOPIC_PRIMARY_KEY + topic.getName();
                TopicDTOWithArticles tempTopic =
                        (TopicDTOWithArticles) CacheManager
                                .get(topicCacheKey);

                List<ArticleDTO> tempTopicsArticles
                        = tempTopic.getArticles();

                tempTopicsArticles.removeIf(
                        articleDTO -> articleDTO
                                .getTitle()
                                .equals(articleOldTitle));

                tempTopicsArticles.add(Conversion
                        .convertArticleToDTO(article));
                tempTopic.setArticles(tempTopicsArticles);
                CacheManager.put(topicCacheKey, tempTopic);
            }
            CacheManager.put(cacheKey, temp);
            CacheManager.remove(CachePrimaryKeys.TOPIC_PRIMARY_KEY);
        }
    }

    public void clearCacheItems(
            final String topicName,
            final String articleTitle) {
        String cacheKeyTopic = CachePrimaryKeys.TOPIC_PRIMARY_KEY + topicName;
        String cacheKeyArticle = CachePrimaryKeys.ARTICLE_PRIMARY_KEY + articleTitle;
        if (CacheManager.containsKey(cacheKeyTopic)) {
            CacheManager.remove(cacheKeyTopic);
        }
        if (CacheManager.containsKey(cacheKeyArticle)) {
            CacheManager.remove(cacheKeyArticle);
        }
        CacheManager.remove(CachePrimaryKeys.TOPIC_PRIMARY_KEY);
        CacheManager.remove(CachePrimaryKeys.ARTICLE_PRIMARY_KEY);
    }

    public void addCountryToList(final Country country) {
        if (CacheManager.containsKey(CachePrimaryKeys.COUNTRY_PRIMARY_KEY)) {
            List<CountryDTOWithUsers> countries =
                    (List<CountryDTOWithUsers>)
                            CacheManager.get(CachePrimaryKeys.COUNTRY_PRIMARY_KEY);
            countries.add(Conversion.convertCountryToDTOWithUsers(country));
            CacheManager.put(CachePrimaryKeys.COUNTRY_PRIMARY_KEY, countries);
        }
    }

    public List<CountryDTOWithUsers> findAllCountries() {
        if (CacheManager.containsKey(CachePrimaryKeys.COUNTRY_PRIMARY_KEY)) {
            return (List<CountryDTOWithUsers>)
                    CacheManager.get(CachePrimaryKeys.COUNTRY_PRIMARY_KEY);
        }
        return Collections.emptyList();
    }

    public void updateCountry(final Country country, final String countryOldName) {

        CountryDTOWithUsers updateResult = Conversion.convertCountryToDTOWithUsers(country);
        String cacheKey = CachePrimaryKeys.COUNTRY_PRIMARY_KEY + countryOldName;
        if (CacheManager.containsKey(cacheKey)) {
            CacheManager.remove(cacheKey);
            cacheKey = CachePrimaryKeys.COUNTRY_PRIMARY_KEY + country.getName();
            CacheManager.put(cacheKey, country);
        }

        if (CacheManager.containsKey(CachePrimaryKeys.COUNTRY_PRIMARY_KEY)) {
            List<CountryDTOWithUsers> countries =
                    (List<CountryDTOWithUsers>)
                            CacheManager.get(CachePrimaryKeys.COUNTRY_PRIMARY_KEY);
            Optional<CountryDTOWithUsers> findResult = countries.stream()
                    .filter(countryDTOWithUsers -> countryDTOWithUsers
                            .getName().equals(countryOldName)).findFirst();
            CountryDTOWithUsers temp = Conversion.convertCountryToDTOWithUsers(country);
            List<UserDTO> countryUsers = findResult.get().getUsers();

            temp.setUsers(countryUsers);
            countries.removeIf(countryDTOWithUsers ->
                    countryDTOWithUsers.getName().equals(countryOldName));
            countries.add(temp);
            CacheManager.put(CachePrimaryKeys.COUNTRY_PRIMARY_KEY, countries);
        }
        CacheManager.remove(CachePrimaryKeys.USER_PRIMARY_KEY);

        for (UserDTO countryUser : updateResult.getUsers()) {
            String userKey = CachePrimaryKeys.USER_PRIMARY_KEY + countryUser.getEmail();
            if (CacheManager.containsKey(userKey)) {
                UserDTOWithCountry result = (UserDTOWithCountry) CacheManager.get(userKey);
                result.setCountry(Conversion.convertCountryToDTO(country));
                CacheManager.put(userKey, result);
            }
        }
    }

    public void updateUser(final User oldUser, final User user) {
        CacheManager.remove(CachePrimaryKeys.USER_PRIMARY_KEY + oldUser.getEmail());
        oldUser.setEmail(user.getEmail());
        if (oldUser.getCountry() != user.getCountry()) {
            CacheManager.remove(CachePrimaryKeys.COUNTRY_PRIMARY_KEY);
        }
        CacheManager.remove(CachePrimaryKeys.COUNTRY_PRIMARY_KEY + oldUser.getCountry().getName());

        oldUser.setCountry(user.getCountry());

        oldUser.setFirstName(user.getFirstName());
        oldUser.setLastName(user.getLastName());
        oldUser.setDateOfBirth(user.getDateOfBirth());

        if (CacheManager.containsKey(CachePrimaryKeys.USER_PRIMARY_KEY)) {
            List<UserDTOWithCountry> users =
                    (List<UserDTOWithCountry>)
                            CacheManager.get(CachePrimaryKeys.USER_PRIMARY_KEY);
            users.removeIf(userDTOWithCountry -> userDTOWithCountry.getEmail().equals(user.getEmail()));
            users.add(Conversion.convertAppUserWithCountry(user));
            CacheManager.put(CachePrimaryKeys.USER_PRIMARY_KEY, users);
        }
        CacheManager.put(user.getEmail(), Conversion.convertAppUserWithCountry(user));
    }
}
