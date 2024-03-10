package com.wks.wikisearch.servise;

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
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final TopicRepository topicRepository;
    private final ArticleCustomRepository articleCustomRepository;
    private final TopicCustomRepository topicCustomRepository;

    public List<Article> findAllArticles(){
        return articleCustomRepository.findAllArticlesWithTopics();
    }

    public Article findByTitle(String title){
        return articleCustomRepository.findArticleByTitle(title);
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
        articleCustomRepository.addTopicToArticle(articleCustomRepository.findArticleByTitle(articleTitle).getId(),
                topicCustomRepository.findTopicByName(topicName).getId());
    }

    public void detachTopicByArticleName(String articleTitle, String topicName){
        articleCustomRepository.detachTopicFromArticle(articleCustomRepository.findArticleByTitle(articleTitle).getId(),
                topicCustomRepository.findTopicByName(topicName).getId());
    }
}
