package com.wks.wikisearch.service;

import com.wks.wikisearch.cache.CacheManager;
import com.wks.wikisearch.dto.ArticleDTO;
import com.wks.wikisearch.dto.ArticleDTOWithTopics;
import com.wks.wikisearch.dto.TopicDTO;
import com.wks.wikisearch.dto.TopicDTOWithArticles;
import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.repository.ArticleCustomRepository;
import com.wks.wikisearch.repository.ArticleRepository;
import com.wks.wikisearch.repository.TopicCustomRepository;
import com.wks.wikisearch.repository.TopicRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
@Transactional
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final TopicRepository topicRepository;
    private final ArticleCustomRepository articleCustomRepository;
    private final TopicCustomRepository topicCustomRepository;
    private static final String ARTICLE_PRIMARY_KEY = "articles";
    private static final String TOPIC_PRIMARY_KEY = "topics";

    public List<ArticleDTOWithTopics> findAllArticles(){
        if (CacheManager.containsKey(ARTICLE_PRIMARY_KEY)) {
            return (List<ArticleDTOWithTopics>)CacheManager.get(ARTICLE_PRIMARY_KEY);
        } else {
            List<Article> articles = articleCustomRepository.findAllArticlesWithTopics();
            List<ArticleDTOWithTopics> articleDTOsWithTopics = new ArrayList<>();
            for (Article article : articles) {
                ArticleDTOWithTopics articleDTOWithTopics = Conversion.convertArticleToDTOWithTopics(article);
                articleDTOsWithTopics.add(articleDTOWithTopics);
            }
            for(ArticleDTOWithTopics article : articleDTOsWithTopics){
                CacheManager.put(ARTICLE_PRIMARY_KEY + "/" + article.getTitle(), article);
            }
            CacheManager.put(ARTICLE_PRIMARY_KEY, articleDTOsWithTopics);
            return articleDTOsWithTopics;
        }
    }

    public ArticleDTOWithTopics findByTitle(String title) {
        String cacheKey = ARTICLE_PRIMARY_KEY + "/" + title;
        if (CacheManager.containsKey(cacheKey)) {
            return (ArticleDTOWithTopics) CacheManager.get(cacheKey);
        } else {
            if(CacheManager.containsKey(ARTICLE_PRIMARY_KEY)) {
                List<ArticleDTOWithTopics> articles = (List<ArticleDTOWithTopics>) CacheManager.get(ARTICLE_PRIMARY_KEY);
                Optional<ArticleDTOWithTopics> findResult = articles.stream()
                        .filter(articleDTOWithTopics -> articleDTOWithTopics
                                .getTitle().equals(title)).findFirst();
                if(findResult.isPresent()){
                    CacheManager.put(cacheKey, findResult.get());
                    return findResult.get();
                }
            }
            if(articleRepository.existsByTitle(title)) {
                ArticleDTOWithTopics result = Conversion.convertArticleToDTOWithTopics(articleCustomRepository.findArticleByTitle(title));

                CacheManager.put(cacheKey, result);
                return result;
            }
            return null;
        }
    }

    public void saveArticle(Article article) {
        if(articleRepository.existsByTitle(article.getTitle())) {
            throw new IllegalStateException("Article exists.");
        }
        if(CacheManager.containsKey(ARTICLE_PRIMARY_KEY)){
            List<ArticleDTOWithTopics> articles = (List<ArticleDTOWithTopics>) CacheManager.get(ARTICLE_PRIMARY_KEY);
            articles.add(Conversion.convertArticleToDTOWithTopics(article));
            CacheManager.put(ARTICLE_PRIMARY_KEY, articles);
        }
        articleRepository.save(article);
    }

    @Transactional
    public void deleteArticle(String title) {
        String cacheKey = ARTICLE_PRIMARY_KEY + "/" + title;
        CacheManager.remove(cacheKey);
        if(CacheManager.containsKey(ARTICLE_PRIMARY_KEY)) {
            List<ArticleDTOWithTopics> topics = (List<ArticleDTOWithTopics>) CacheManager.get(ARTICLE_PRIMARY_KEY);
            topics.removeIf(articleDTOWithTopics -> articleDTOWithTopics.getTitle().equals(title));
            CacheManager.put(ARTICLE_PRIMARY_KEY, topics);
        }
        CacheManager.remove(TOPIC_PRIMARY_KEY);
        if(articleRepository.existsByTitle(title)) {
            Article article = articleCustomRepository.findArticleByTitle(title);
            articleCustomRepository.deleteArticle(article.getId());
        }
    }

    public void updateArticle(Article article, String articleOldTitle) {
        String cacheKey = ARTICLE_PRIMARY_KEY + "/" + article.getTitle();
        if(CacheManager.containsKey(cacheKey)) {
            ArticleDTOWithTopics temp = (ArticleDTOWithTopics) CacheManager.get(cacheKey);
            if(article.getTitle() != null) {
                temp.setTitle(article.getTitle());
            }
            if(article.getUrl() != null) {
                temp.setUrl(article.getUrl());
            }
            for(TopicDTO topic : temp.getTopics()){
                String topicCacheKey = TOPIC_PRIMARY_KEY + "/" + topic.getName();
                TopicDTOWithArticles tempTopic = (TopicDTOWithArticles) CacheManager.get(topicCacheKey);
                List<ArticleDTO> tempTopicsArticles = tempTopic.getArticles();
                tempTopicsArticles.removeIf(articleDTO -> articleDTO.getTitle().equals(articleOldTitle));
                tempTopicsArticles.add(Conversion.convertArticleToDTO(article));
                tempTopic.setArticles(tempTopicsArticles);
                CacheManager.put(topicCacheKey, tempTopic);
            }
            CacheManager.put(cacheKey, temp);

        }
        CacheManager.remove(TOPIC_PRIMARY_KEY);
        Optional<Article> temp = articleRepository.findById(article.getId());
        if(temp.isPresent()){
            articleCustomRepository.updateArticle(article);
        }
    }

    private void clearCacheItems(String topicName, String articleTitle) {
        String cacheKeyTopic = "/api/topics/" + topicName;
        String cacheKeyArticle = "/api/articles/" + articleTitle;
        if(CacheManager.containsKey(cacheKeyTopic)){
            CacheManager.remove(cacheKeyTopic);
        }
        if(CacheManager.containsKey(cacheKeyArticle)){
            CacheManager.remove(cacheKeyArticle);
        }
        CacheManager.remove("/api/topics");
        CacheManager.remove("/api/articles");
    }

    public void addNewTopicByArticleTitle(String articleTitle, String topicName) {
        if(!articleRepository.existsByTitle(articleTitle) || !topicRepository.existsByName(topicName)){
            return;
        }
        clearCacheItems(topicName, articleTitle);
        articleCustomRepository.addTopicToArticle(articleCustomRepository.findArticleByTitle(articleTitle).getId(),
                topicCustomRepository.findTopicByName(topicName).getId());
    }

    public void detachTopicByArticleName(String articleTitle, String topicName) {
        if(!articleRepository.existsByTitle(articleTitle) || !topicRepository.existsByName(topicName)){
            return;
        }
        clearCacheItems(topicName, articleTitle);
        articleCustomRepository.detachTopicFromArticle(articleCustomRepository.findArticleByTitle(articleTitle).getId(),
                topicCustomRepository.findTopicByName(topicName).getId());
    }
}
