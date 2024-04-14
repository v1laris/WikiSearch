package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.Topic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.util.*;

@Repository
@Transactional
public class TopicCustomRepository {
    private final JdbcTemplate jdbcTemplate;

    public TopicCustomRepository(final JdbcTemplate dbTemplate) {
        this.jdbcTemplate = dbTemplate;
    }

    public List<Topic> findAllTopicsWithArticles() {
        String sql = "SELECT t.id, t.name, a.id AS article_id, a.title "
                + "AS article_title, a.url AS article_url "
                + "FROM topic t "
                + "LEFT JOIN article_topic at ON t.id = at.topic_id "
                + "LEFT JOIN article a ON at.article_id = a.id";

        Map<Long, Topic> topicMap = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
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

    public Topic findTopicByName(final String name) {
        String sql = "SELECT t.*, a.id AS article_id, a.title AS article_title, a.url AS article_url "
                + "FROM topic t "
                + "LEFT JOIN article_topic at ON t.id = at.topic_id "
                + "LEFT JOIN article a ON at.article_id = a.id "
                + "WHERE t.name = ?";

        List<Topic> topics = jdbcTemplate.query(sql,
                new Object[]{name}, new int[]{Types.VARCHAR}, (rs, rowNum) -> {
                    Topic t = new Topic();
                    t.setId(rs.getLong("id"));
                    t.setName(rs.getString("name"));
                    return t;
                });

        if (topics.isEmpty()) {
            return null;
        }

        Topic topic = topics.get(0);

        Set<Article> articles = new HashSet<>();
        jdbcTemplate.query("SELECT a.id AS article_id, a.title AS article_title, a.url AS article_url "
                        + "FROM article a "
                        + "LEFT JOIN article_topic at ON a.id = at.article_id "
                        + "WHERE at.topic_id = ?",
                new Object[]{topic.getId()}, new int[]{Types.BIGINT}, (rs, rowNum) -> {
                    Article article = new Article();
                    article.setId(rs.getLong("article_id"));
                    article.setTitle(rs.getString("article_title"));
                    article.setUrl(rs.getString("article_url"));
                    articles.add(article);
                    return null;
                });

        topic.setArticles(articles);

        return topic;
    }




    public void deleteTopic(final Long topicId) {
        String deleteArticleTopicSql = "DELETE FROM article_topic "
                + "WHERE topic_id = ?";
        jdbcTemplate.update(deleteArticleTopicSql, topicId);

        String deleteTopicSql = "DELETE FROM topic WHERE id = ?";
        jdbcTemplate.update(deleteTopicSql, topicId);
    }

    public void addArticleToTopic(final Long topicId, final Long articleId) {
        String sql = "INSERT INTO article_topic "
                + "(topic_id, article_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, topicId, articleId);
    }

    public void detachArticleFromTopic(final Long topicId,
                                       final Long articleId) {
        String sql = "DELETE FROM article_topic "
                + "WHERE topic_id = ? AND article_id = ?";
        jdbcTemplate.update(sql, topicId, articleId);
    }

    public void updateTopic(final Topic topic) {
        String sql = "UPDATE topic SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, topic.getName(), topic.getId());
    }
}
