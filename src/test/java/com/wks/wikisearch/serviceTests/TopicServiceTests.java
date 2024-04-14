package com.wks.wikisearch.serviceTests;

import com.wks.wikisearch.dto.TopicDTOWithArticles;
import com.wks.wikisearch.exception.ObjectAlreadyExistsException;
import com.wks.wikisearch.exception.ObjectNotFoundException;
import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.Topic;
import com.wks.wikisearch.repository.ArticleCustomRepository;
import com.wks.wikisearch.repository.ArticleRepository;
import com.wks.wikisearch.repository.TopicCustomRepository;
import com.wks.wikisearch.repository.TopicRepository;
import com.wks.wikisearch.service.TopicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TopicServiceTests {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private TopicCustomRepository topicCustomRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleCustomRepository articleCustomRepository;

    @InjectMocks
    private TopicService topicService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testFindAllTopics() {
        List<Topic> topics = new ArrayList<>();
        Topic topic1 = new Topic(1L, "Topic1");
        Topic topic2 = new Topic(2L, "Topic2");
        topics.add(topic1);
        topics.add(topic2);

        when(topicCustomRepository.findAllTopicsWithArticles()).thenReturn(topics);

        List<TopicDTOWithArticles> result = topicService.findAllTopics();

        assertEquals(topics.size(), result.size());
        // Add more assertions based on your specific requirements
    }

    @Test
    void testFindByName_ExistingTopic() {
        String topicName = "ExistingTopic";
        Topic topic = new Topic(1L, topicName);

        when(topicRepository.existsByName(topicName)).thenReturn(true);
        when(topicCustomRepository.findTopicByName(topicName)).thenReturn(topic);

        TopicDTOWithArticles result = topicService.findByName(topicName);

        assertNotNull(result);
        assertEquals(topicName, result.getName());
        // Add more assertions based on your specific requirements
    }

    @Test
    void testFindByName_NonExistingTopic() {
        String topicName = "NonExistingTopic";

        when(topicRepository.existsByName(topicName)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> {
            topicService.findByName(topicName);
        });
    }

    @Test
    void testSaveTopic_NewTopic() {
        String topicName = "NewTopic";
        Topic topic = new Topic();
        topic.setName(topicName);

        when(topicRepository.existsByName(topicName)).thenReturn(false);

        topicService.saveTopic(topic);

        verify(topicRepository, times(1)).save(topic);
    }

    @Test
    void testSaveTopic_ExistingTopic() {
        String topicName = "ExistingTopic";
        Topic topic = new Topic();
        topic.setName(topicName);

        when(topicRepository.existsByName(topicName)).thenReturn(true);

        assertThrows(ObjectAlreadyExistsException.class, () -> {
            topicService.saveTopic(topic);
        });
    }

    @Test
    void testAddNewArticleByTopicName_ExistingArticleAndTopic() {
        String topicName = "Topic1";
        String articleTitle = "Article1";

        when(articleRepository.existsByTitle(articleTitle)).thenReturn(true);
        when(topicRepository.existsByName(topicName)).thenReturn(true);
        when(topicCustomRepository.findTopicByName(topicName)).thenReturn(new Topic(1L, topicName));
        when(articleCustomRepository.findArticleByTitle(articleTitle))
                .thenReturn(new Article(1L, articleTitle,
                        "test.com", new HashSet<>()));

        assertDoesNotThrow(() -> {
            topicService.addNewArticleByTopicName(topicName, articleTitle);
        });

        verify(topicCustomRepository, times(1)).addArticleToTopic(anyLong(), anyLong());
    }

    @Test
    void testAddNewArticleByTopicName_NonExistingArticleOrTopic() {
        String topicName = "NonExistingTopic";
        String articleTitle = "NonExistingArticle";

        when(articleRepository.existsByTitle(articleTitle)).thenReturn(false);
        when(topicRepository.existsByName(topicName)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> {
            topicService.addNewArticleByTopicName(topicName, articleTitle);
        });
    }

    @Test
    void testDeleteTopic_ExistingTopic() {
        String topicName = "ExistingTopic";
        Topic topic = new Topic(1L, topicName);

        when(topicRepository.existsByName(topicName)).thenReturn(true);
        when(topicRepository.findByName(topicName))
                .thenReturn(Optional.of(topic));

        assertDoesNotThrow(() -> {
            topicService.deleteTopic(topicName);
        });

        verify(topicCustomRepository, times(1)).deleteTopic(anyLong());
    }

    @Test
    void testDeleteTopic_NonExistingTopic() {
        String topicName = "NonExistingTopic";

        when(topicRepository.existsByName(topicName)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> {
            topicService.deleteTopic(topicName);
        });
    }

    @Test
    void testDetachArticleByTopicName_ExistingArticleAndTopic() {
        String topicName = "Topic1";
        String articleTitle = "Article1";

        when(articleRepository.existsByTitle(articleTitle)).thenReturn(true);
        when(topicRepository.existsByName(topicName)).thenReturn(true);
        when(topicCustomRepository.findTopicByName(topicName)).thenReturn(new Topic(1L, topicName));
        when(articleCustomRepository.findArticleByTitle(articleTitle)).thenReturn(new Article(1L, articleTitle,
                "test.com", new HashSet<>()));

        assertDoesNotThrow(() -> {
            topicService.detachArticleByTopicName(topicName, articleTitle);
        });

        verify(topicCustomRepository, times(1)).detachArticleFromTopic(anyLong(), anyLong());
    }

    @Test
    void testDetachArticleByTopicName_NonExistingArticleOrTopic() {
        String topicName = "NonExistingTopic";
        String articleTitle = "NonExistingArticle";

        when(articleRepository.existsByTitle(articleTitle)).thenReturn(false);
        when(topicRepository.existsByName(topicName)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> {
            topicService.detachArticleByTopicName(topicName, articleTitle);
        });
    }

    @Test
    void testUpdateTopic_ExistingTopic() {
        String topicOldName = "ExistingTopic";
        Topic topic = new Topic(1L, "UpdatedTopic");

        when(topicRepository.existsByName(topicOldName)).thenReturn(true);
        when(topicRepository.existsByName("UpdatedTopic")).thenReturn(false);

        assertDoesNotThrow(() -> {
            topicService.updateTopic(topicOldName, topic);
        });

        verify(topicCustomRepository, times(1)).updateTopic(topic);
    }

    @Test
    void testUpdateTopic_NonExistingTopic() {
        String topicOldName = "NonExistingTopic";
        Topic topic = new Topic(1L,"UpdatedTopic");

        when(topicRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> {
            topicService.updateTopic(topicOldName, topic);
        });
    }

}

