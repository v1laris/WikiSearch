package com.wks.wikisearch.servise.impl;

import com.wks.wikisearch.dto.ArticleDTO;
import com.wks.wikisearch.dto.TopicDTO;
import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    public List<Article> findAllArticles() {
        return articleRepository.findAll();
    }

    public Article findByTitle(String title){
        return articleRepository.findByTitle(title);
    }

    public void saveArticle(Article article) {
        boolean exists = articleRepository.existsByTitle(article.getTitle());
        if(exists) {
            throw new IllegalStateException("Article exists.");
        }
        articleRepository.save(article);
    }

    @Transactional
    public void deleteArticle(String title) {
        boolean exists = articleRepository.existsByTitle(title);
        if(!exists) {
            throw new IllegalStateException(
                    "Article with title " + title + " can not be deleted, because id does not exist");
        }
        articleRepository.deleteByTitle(title);
    }

    public void updateArticle(Article article) {
        Article Article = articleRepository.findById(article.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Article with id " + article.getId() + "can not be updated, because it does not exist"));

    }

    private ArticleDTO convertToDTO(Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(article.getId());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setContent(article.getContent());
        articleDTO.setTopics(article.getTopics().stream().map(this::convertToDTO).collect(Collectors.toList()));
        return articleDTO;
    }

    private TopicDTO convertToDTO(Topic topic) {
        TopicDTO topicDTO = new TopicDTO();
        topicDTO.setId(topic.getId());
        topicDTO.setName(topic.getName());
        return topicDTO;
    }
    public List<ArticleDTO> getAllArticlesWithTopics() {
        List<Article> articles = articleRepository.findAll();
        return articles.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
