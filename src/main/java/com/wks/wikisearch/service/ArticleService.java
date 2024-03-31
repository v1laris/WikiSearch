package com.wks.wikisearch.service;

import com.wks.wikisearch.cache.CacheManager;
import com.wks.wikisearch.dto.ArticleDTOWithTopics;
import com.wks.wikisearch.exception.ResourceAlreadyExistsException;
import com.wks.wikisearch.exception.ResourceNotFoundException;
import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.CachePrimaryKeys;
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
    private final CacheService cacheService;

    public List<ArticleDTOWithTopics> findAllArticles() {
        if (CacheManager.containsKey(CachePrimaryKeys.ARTICLE_PRIMARY_KEY)) {
            return (List<ArticleDTOWithTopics>) CacheManager
                    .get(CachePrimaryKeys.ARTICLE_PRIMARY_KEY);
        } else {
            List<Article> articles =
                    articleCustomRepository.findAllArticlesWithTopics();
            List<ArticleDTOWithTopics> articleDTOsWithTopics =
                    new ArrayList<>();
            for (Article article : articles) {
                ArticleDTOWithTopics articleDTOWithTopics =
                        Conversion.convertArticleToDTOWithTopics(article);
                articleDTOsWithTopics.add(articleDTOWithTopics);
            }
            for (ArticleDTOWithTopics article : articleDTOsWithTopics) {
                cacheService.addArticle(article);
            }
            CacheManager.put(CachePrimaryKeys.ARTICLE_PRIMARY_KEY, articleDTOsWithTopics);
            return articleDTOsWithTopics;
        }

    }

    public ArticleDTOWithTopics findByTitle(final String title) {
        ArticleDTOWithTopics article = cacheService.getArticle(title);
        if (article != null) {
            return article;
        } else {
            if (articleRepository.existsByTitle(title)) {
                ArticleDTOWithTopics result =
                        Conversion.convertArticleToDTOWithTopics(
                                articleCustomRepository
                                        .findArticleByTitle(title));
                cacheService.addArticle(result);
                return result;
            } else {
                throw new ResourceNotFoundException("Requested non-existent article");
            }
        }
    }

    public void saveArticle(final Article article) {
        if (articleRepository.existsByTitle(article.getTitle())) {
            throw new ResourceAlreadyExistsException("Article with this title already exists");
        }
        cacheService.addArticle(Conversion.convertArticleToDTOWithTopics(article));
        if (CacheManager.containsKey(CachePrimaryKeys.ARTICLE_PRIMARY_KEY)) {
            List<ArticleDTOWithTopics> articles =
                    (List<ArticleDTOWithTopics>) CacheManager.get(CachePrimaryKeys.ARTICLE_PRIMARY_KEY);
            articles.add(Conversion.convertArticleToDTOWithTopics(article));
            CacheManager.put(CachePrimaryKeys.ARTICLE_PRIMARY_KEY, articles);
        }
        articleRepository.save(article);
    }

    @Transactional
    public void deleteArticle(final String title) {
        cacheService.removeArticle(title);
        if (articleRepository.existsByTitle(title)) {
            Article article = articleCustomRepository.findArticleByTitle(title);
            articleCustomRepository.deleteArticle(article.getId());
        } else {
            throw new ResourceNotFoundException("Error deleting non-existent article");
        }
    }

    public void updateArticle(
            final Article article,
            final String articleOldTitle) {
        if (articleRepository.existsByTitle(articleOldTitle)) {
            if (!articleRepository.existsByTitle(article.getTitle())) {
                cacheService.updateArticle(articleOldTitle, article);
                articleCustomRepository.updateArticle(article);
            } else {
                throw new ResourceAlreadyExistsException("Article with this title already exists");
            }
        } else {
            throw new ResourceNotFoundException("Updating non-existent article");
        }
    }

    public void addNewTopicByArticleTitle(
            final String articleTitle,
            final String topicName) {
        if (!articleRepository.existsByTitle(articleTitle)
                || !topicRepository.existsByName(topicName)) {
            throw new ResourceNotFoundException("This article or topic doesn't exist");
        }
        cacheService.clearCacheItems(topicName, articleTitle);
        articleCustomRepository.addTopicToArticle(
                articleCustomRepository
                        .findArticleByTitle(articleTitle)
                        .getId(),
                topicCustomRepository
                        .findTopicByName(topicName)
                        .getId());
    }

    public void detachTopicByArticleName(final String articleTitle,
                                         final String topicName) {
        if (!articleRepository.existsByTitle(articleTitle)
                || !topicRepository.existsByName(topicName)) {
            throw new ResourceNotFoundException("This article or topic doesn't exist");
        }
        cacheService.clearCacheItems(topicName, articleTitle);
        articleCustomRepository.detachTopicFromArticle(
                articleCustomRepository
                        .findArticleByTitle(articleTitle)
                        .getId(),
                topicCustomRepository
                        .findTopicByName(topicName).getId());
    }
}
