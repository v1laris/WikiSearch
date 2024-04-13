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
import java.util.Optional;

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

    @Mock
    private Conversion conversion;

    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllArticles() {
        List<Article> articles = new ArrayList<>();
        // добавьте статьи в список articles

        when(articleCustomRepository.findAllArticlesWithTopics()).thenReturn(articles);

        List<ArticleDTOWithTopics> result = articleService.findAllArticles();

        assertEquals(articles.size(), result.size());
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

/*    @Test
    void testUpdateArticle_ExistingArticle() {
        String oldTitle = "Old Title";
        String newTitle = "New Title";
        Article existingArticle = new Article();
        existingArticle.setTitle(oldTitle);
        existingArticle.setId(1L);
        Article updatedArticle = new Article();
        updatedArticle.setId(1L);
        updatedArticle.setTitle(newTitle);

        when(articleRepository.findById(1L)).thenReturn(Optional.of(existingArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(updatedArticle);

        articleService.updateArticle(existingArticle, newTitle);
        //assertThrows(ObjectAlreadyExistsException.class, () -> articleService.updateArticle(existingArticle, oldTitle));

        verify(articleCustomRepository, times(1)).updateArticle(existingArticle);
    }*/

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

    /*@Test
    void testAddNewTopicByArticleTitle_ExistingArticleAndTopic() {
        String articleTitle = "Existing Article";
        String topicName = "Existing Topic";
        long articleId = 1L;
        long topicId = 1L;

        when(articleRepository.existsByTitle(articleTitle)).thenReturn(true);
        when(topicRepository.existsByName(topicName)).thenReturn(true);
        when(articleCustomRepository.findArticleByTitle(articleTitle))
                .thenReturn(articleCustomRepository.findArticleByTitle(articleTitle));
        when(topicCustomRepository.findTopicByName(topicName))
                .thenReturn(topicCustomRepository.findTopicByName(topicName));

        articleService.addNewTopicByArticleTitle(articleTitle, topicName);

        verify(articleCustomRepository, times(1)).addTopicToArticle(articleId, topicId);
    }*/

   /* @Test
    void testAddNewTopicByArticleTitle_ExistingArticleAndTopic() {
        Article article = new Article();
        article.setTitle("Existing Article");
        article.setId(1L);
        Topic topic = new Topic();
        topic.setName("Existing Topic");
        topic.setId(1L);
        when(articleRepository.save(article)).thenReturn(article);
        when(topicRepository.save(topic)).thenReturn(topic);
        
        // Устанавливаем заглушки для репозиториев
        String articleTitle = "Existing Article";
        String topicName = "Existing Topic";

        // Устанавливаем условия, при которых методы existsByTitle будут возвращать true
        when(articleRepository.existsByTitle(articleTitle)).thenReturn(true);
        when(topicRepository.existsByName(topicName)).thenReturn(true);

        // Вызываем метод, который будем тестировать
        articleService.addNewTopicByArticleTitle(articleTitle, topicName);

        // Проверяем, что метод addTopicToArticle был вызван один раз с корректными параметрами
        verify(articleCustomRepository, times(1)).addTopicToArticle(anyLong(), anyLong());
    }

    @Test
    void testDetachTopicByArticleName_ExistingArticleAndTopic() {
        // Устанавливаем заглушки для репозиториев
        String articleTitle = "Existing Article";
        String topicName = "Existing Topic";

        // Устанавливаем условия, при которых методы existsByTitle будут возвращать true
        when(articleRepository.existsByTitle(articleTitle)).thenReturn(true);
        when(topicRepository.existsByName(topicName)).thenReturn(true);

        // Вызываем метод, который будем тестировать
        articleService.detachTopicByArticleName(articleTitle, topicName);

        // Проверяем, что метод detachTopicFromArticle был вызван один раз с корректными параметрами
        verify(articleCustomRepository, times(1)).detachTopicFromArticle(anyLong(), anyLong());
    }*/

    /*@Test
    public void testFindAllArticles() {
        // Create test data
        List<Article> articles = new ArrayList<>();
        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitle("Article 1");
        articles.add(article1);

        // Stubbing method calls
        when(articleCustomRepository.findAllArticlesWithTopics()).thenReturn(articles);
        when(Conversion.convertArticleToDTOWithTopics(any(Article.class)))
                .thenAnswer(invocation -> {
                    Article article = invocation.getArgument(0);
                    ArticleDTOWithTopics articleDTOWithTopics = new ArticleDTOWithTopics();
                    articleDTOWithTopics.setId(article.getId());
                    articleDTOWithTopics.setTitle(article.getTitle());
                    return articleDTOWithTopics;
                });

        // Call the method under test
        List<ArticleDTOWithTopics> result = articleService.findAllArticles();

        // Assertions
        assertEquals(1, result.size());
        assertEquals(article1.getId(), result.get(0).getId());
        assertEquals(article1.getTitle(), result.get(0).getTitle());

        // Verify method invocations
        verify(articleCustomRepository).findAllArticlesWithTopics();
        verify(conversion, times(articles.size())).convertArticleToDTOWithTopics(any(Article.class));
    }*/
}


