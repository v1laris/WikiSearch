package com.wks.wikisearch.service;

import com.wks.wikisearch.dto.ArticleDTOWithTopics;
import com.wks.wikisearch.dto.TopicDTO;
import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.Topic;
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

    public List<ArticleDTOWithTopics> findAllArticles(){
        List<Article> articles = articleCustomRepository.findAllArticlesWithTopics();
        List<ArticleDTOWithTopics> articleDTOsWithTopics = new ArrayList<>();
        for (Article article : articles) {
            ArticleDTOWithTopics articleDTOWithTopics = Convertation.convertArticleToDTOWithTopics(article);
            articleDTOsWithTopics.add(articleDTOWithTopics);
        }
        return articleDTOsWithTopics;
    }

    public ArticleDTOWithTopics findByTitle(String title){
        return Convertation.convertArticleToDTOWithTopics(articleCustomRepository.findArticleByTitle(title));
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
        Article article = articleCustomRepository.findArticleByTitle(title);
        articleCustomRepository.deleteArticle(article.getId());
    }

    public void updateArticle(Article article) {
        articleCustomRepository.updateArticle(article);
    }

    public void addNewTopicByArticleTitle(String articleTitle, String topicName) {
        if(!articleRepository.existsByTitle(articleTitle) || !topicRepository.existsByName(topicName)){
            return;
        }
        articleCustomRepository.addTopicToArticle(articleCustomRepository.findArticleByTitle(articleTitle).getId(),
                topicCustomRepository.findTopicByName(topicName).getId());
    }

    public void detachTopicByArticleName(String articleTitle, String topicName){
        articleCustomRepository.detachTopicFromArticle(articleCustomRepository.findArticleByTitle(articleTitle).getId(),
                topicCustomRepository.findTopicByName(topicName).getId());
    }
}
