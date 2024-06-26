package com.wks.wikisearch.service;

import com.wks.wikisearch.dto.ArticleDTO;
import com.wks.wikisearch.dto.ArticleDTOWithTopics;
import com.wks.wikisearch.exception.ObjectAlreadyExistsException;
import com.wks.wikisearch.exception.ObjectNotFoundException;
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

    public List<ArticleDTOWithTopics> findAllArticles() {
        return articleCustomRepository.findAllArticlesWithTopics().stream()
                .map(Conversion::convertArticleToDTOWithTopics)
                .toList();
    }


    public ArticleDTOWithTopics findByTitle(final String title) {
        if (articleRepository.existsByTitle(title)) {
            return Conversion.convertArticleToDTOWithTopics(
                    articleCustomRepository
                            .findArticleByTitle(title));
        } else {
            throw new ObjectNotFoundException("Requested non-existent article");
        }
    }

    public ArticleDTO saveArticle(final Article article) {
        if (articleRepository.existsByTitle(article.getTitle())) {
            throw new ObjectAlreadyExistsException("Article with this title already exists");
        }
        articleRepository.save(article);
        return Conversion.convertArticleToDTO(article);
    }

    @Transactional
    public void deleteArticle(final String title) {
        if (articleRepository.existsByTitle(title)) {
            Article article = articleCustomRepository.findArticleByTitle(title);
            articleCustomRepository.deleteArticle(article.getId());
        } else {
            throw new ObjectNotFoundException("Error deleting non-existent article");
        }
    }

    public void updateArticle(
            final Article article,
            final String articleOldTitle) {
        if (articleRepository.existsByTitle(articleOldTitle)) {
            if (!Objects.equals(article.getTitle(), articleOldTitle) && articleRepository.existsByTitle(article.getTitle())) {
                throw new ObjectAlreadyExistsException("Article with this title already exists");
            } else {
                articleCustomRepository.updateArticle(article);
            }
        } else {
            throw new ObjectNotFoundException("Updating non-existent article");
        }
    }

    public void addNewTopicByArticleTitle(
            final String articleTitle,
            final String topicName) {
        if (!articleRepository.existsByTitle(articleTitle)
                || !topicRepository.existsByName(topicName)) {
            throw new ObjectNotFoundException("This article or topic doesn't exist");
        }
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
            throw new ObjectNotFoundException("This article or topic doesn't exist");
        }
        articleCustomRepository.detachTopicFromArticle(
                articleCustomRepository
                        .findArticleByTitle(articleTitle)
                        .getId(),
                topicCustomRepository
                        .findTopicByName(topicName).getId());
    }

    public List<ArticleDTO> addMultipleArticles(final List<Article> articleList) {
        return articleList.stream()
                .map(this::saveArticle)
                .toList();
    }
}
