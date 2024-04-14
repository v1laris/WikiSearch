package com.wks.wikisearch.serviceTests;

import com.wks.wikisearch.dto.ArticleDTOWithTopics;
import com.wks.wikisearch.exception.ObjectAlreadyExistsException;
import com.wks.wikisearch.exception.ObjectNotFoundException;
import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.repository.ArticleCustomRepository;
import com.wks.wikisearch.repository.ArticleRepository;
import com.wks.wikisearch.repository.TopicCustomRepository;
import com.wks.wikisearch.repository.TopicRepository;
import com.wks.wikisearch.service.ArticleService;
import com.wks.wikisearch.service.Conversion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArticleServiceTests {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private ArticleCustomRepository articleCustomRepository;

    @Mock
    private TopicCustomRepository topicCustomRepository;


    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByTitle_ExistingArticle() {
        String title = "Test Article";
        Article article = new Article();
        // установите свойства статьи article

        when(articleRepository.existsByTitle(title)).thenReturn(true);
        when(articleCustomRepository.findArticleByTitle(title)).thenReturn(article);

        ArticleDTOWithTopics result = articleService.findByTitle(title);

        assertNotNull(result);
        // проверьте, что результат соответствует ожиданиям
    }

    @Test
    void testFindByTitle_NonExistingArticle() {
        String title = "Non-existing Article";

        when(articleRepository.existsByTitle(title)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> articleService.findByTitle(title));
    }

    @Test
    void testSaveArticle_NewArticle() {
        Article article = new Article();
        article.setTitle("New Article");

        when(articleRepository.existsByTitle(article.getTitle())).thenReturn(false);

        articleService.saveArticle(article);

        verify(articleRepository, times(1)).save(article);
    }

    @Test
    void testSaveArticle_ExistingArticle() {
        Article existingArticle = new Article();
        existingArticle.setTitle("Existing Article");

        when(articleRepository.existsByTitle(existingArticle.getTitle())).thenReturn(true);

        assertThrows(ObjectAlreadyExistsException.class, () -> articleService.saveArticle(existingArticle));
    }

    @Test
    void testDeleteArticle_ExistingArticle() {
        String title = "Existing Article";
        long articleId = 1L;
        Article article = new Article();
        article.setId(articleId);

        when(articleRepository.existsByTitle(title)).thenReturn(true);
        when(articleCustomRepository.findArticleByTitle(title)).thenReturn(article);

        articleService.deleteArticle(title);

        verify(articleCustomRepository, times(1)).deleteArticle(articleId);
    }

    @Test
    void testDeleteArticle_NonExistingArticle() {
        String title = "Non-existing Article";

        when(articleRepository.existsByTitle(title)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> articleService.deleteArticle(title));
    }

    @Test
    void testUpdateArticle_NewTitleAlreadyExists() {
        String oldTitle = "Old Title";
        String newTitle = "Existing Title";
        Article article = new Article();
        article.setTitle(oldTitle);

        when(articleRepository.existsByTitle(oldTitle)).thenReturn(true);
        when(articleRepository.existsByTitle(newTitle)).thenReturn(true);

        assertThrows(ObjectAlreadyExistsException.class, () -> articleService.updateArticle(article, oldTitle));
    }

    @Test
    void testUpdateArticle_NonExistingArticle() {
        String oldTitle = "Non-existing Title";
        Article article = new Article();
        article.setTitle(oldTitle);

        when(articleRepository.existsByTitle(oldTitle)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> articleService.updateArticle(article, oldTitle));
    }

    @Test
    void testUpdateArticle_ExistingArticle() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("testTitle");
        when(articleRepository.existsByTitle("testTitle")).thenReturn(true);
        article.setTitle("testTitle2");
        articleService.updateArticle(article, "testTitle");
        verify(articleCustomRepository,
                times(1)).updateArticle(article);
    }

    @Test
    void testAddNewTopicByArticleTitle_ExistingArticleAndTopic() {
        Article article = new Article();
        Topic topic = new Topic();
        article.setTitle("Existing Article");
        topic.setName("Existing Topic");
        String articleTitle = "Existing Article";
        String topicName = "Existing Topic";
        article.setId(1L);
        topic.setId(1L);
        articleRepository.save(article);
        topicRepository.save(topic);

        when(articleRepository.existsByTitle(articleTitle)).thenReturn(true);
        when(topicRepository.existsByName(topicName)).thenReturn(true);
        when(articleCustomRepository.findArticleByTitle(articleTitle))
                .thenReturn(article);
        when(topicCustomRepository.findTopicByName(topicName))
                .thenReturn(topic);

        articleService.addNewTopicByArticleTitle(articleTitle, topicName);

        verify(articleCustomRepository, times(1))
                .addTopicToArticle(article.getId(), topic.getId());
    }

    @Test
    void testDetachTopicByArticleName_ExistingArticleAndTopic() {
        // Устанавливаем заглушки для репозиториев
        String articleTitle = "Existing Article";
        String topicName = "Existing Topic";
        Article article = new Article();
        article.setId(1L);
        article.setTitle(articleTitle);
        Topic topic = new Topic();
        topic.setId(1L);
        topic.setName(topicName);

        // Устанавливаем условия, при которых методы existsByTitle будут возвращать true
        when(articleRepository.existsByTitle(articleTitle)).thenReturn(true);
        when(topicRepository.existsByName(topicName)).thenReturn(true);
        when(articleCustomRepository.findArticleByTitle(articleTitle)).thenReturn(article);
        when(topicCustomRepository.findTopicByName(topicName)).thenReturn(topic);
        // Вызываем метод, который будем тестировать
        articleService.detachTopicByArticleName(articleTitle, topicName);

        // Проверяем, что метод detachTopicFromArticle был вызван один раз с корректными параметрами
        verify(articleCustomRepository, times(1)).detachTopicFromArticle(anyLong(), anyLong());
    }

    @Test
    void testFindAllArticles() {
        // Create test data
        List<Article> articles = new ArrayList<>();
        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitle("Article 1");
        articles.add(article1);

        // Stubbing method calls
        when(articleCustomRepository.findAllArticlesWithTopics()).thenReturn(articles);

        // Call the method under test
        List<ArticleDTOWithTopics> result = articleService.findAllArticles();

        // Assertions
        assertEquals(1, result.size());
        assertEquals(article1.getId(), result.get(0).getId());
        assertEquals(article1.getTitle(), result.get(0).getTitle());
    }

}
