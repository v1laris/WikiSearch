package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.Topic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TopicCustomRepository {
    private final JdbcTemplate jdbcTemplate;

    public TopicCustomRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Topic> findAllTopicsWithArticles() {
        String sql = "SELECT t.id, t.name, a.id AS article_id, a.title AS article_title, a.url AS article_url " +
                "FROM topic t " +
                "LEFT JOIN article_topic at ON t.id = at.topic_id " +
                "LEFT JOIN article a ON at.article_id = a.id";

        Map<Long, Topic> topicMap = new HashMap<>();
        jdbcTemplate.query(sql, (rs) -> {
            Long topicId = rs.getLong("id");
            Topic topic = topicMap.get(topicId);
            if (topic == null) {
                topic = new Topic();
                topic.setId(topicId);
                topic.setName(rs.getString("name"));
                topic.setArticles(new HashSet<>());
                topicMap.put(topicId, topic);
            }

            Long articleId = rs.getLong("article_id");
            if (articleId != 0) {
                Article article = new Article();
                article.setId(articleId);
                article.setTitle(rs.getString("article_title"));
                article.setUrl(rs.getString("article_url"));
                topic.getArticles().add(article);
            }
        });

        return new ArrayList<>(topicMap.values());
    }

    public Topic findTopicByName(String name) {
        String sql = "SELECT t.*, a.id AS article_id, a.title AS article_title, a.url AS article_url " +
                "FROM topic t " +
                "LEFT JOIN article_topic at ON t.id = at.topic_id " +
                "LEFT JOIN article a ON at.article_id = a.id " +
                "WHERE t.name = ?";

        List<Topic> topics = jdbcTemplate.query(sql, new Object[]{name}, (rs, rowNum) -> {
            Topic a = new Topic();
            a.setId(rs.getLong("id"));
            a.setName(rs.getString("name"));
            return a;
        });

        if (topics.isEmpty()) {
            return null;
        }

        Topic topic = topics.get(0);

        Set<Article> articles = new HashSet<>();

        jdbcTemplate.query("SELECT t.* " +
                "FROM article t " +
                "LEFT JOIN article_topic at ON t.id = at.article_id " +
                "WHERE at.topic_id = ?", new Object[]{topic.getId()}, (rs, rowNum) -> {
            Article article = new Article();
            article.setId(rs.getLong("id"));
            article.setTitle(rs.getString("title"));
            articles.add(article);
            return null;
        });

        topic.setArticles(articles);

        return topic;
    }

    public void deleteTopic(Long topicId) {
        String deleteArticleTopicSql = "DELETE FROM article_topic WHERE topic_id = ?";
        jdbcTemplate.update(deleteArticleTopicSql, topicId);

        String deleteTopicSql = "DELETE FROM topic WHERE id = ?";
        jdbcTemplate.update(deleteTopicSql, topicId);
    }

    public void addArticleToTopic(Long topicId, Long articleId) {
        String sql = "INSERT INTO article_topic (topic_id, article_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, topicId, articleId);
    }

    public void detachArticleFromTopic(Long topicId, Long articleId) {
        String sql = "DELETE FROM article_topic WHERE topic_id = ? AND article_id = ?";
        jdbcTemplate.update(sql, topicId, articleId);
    }
}
