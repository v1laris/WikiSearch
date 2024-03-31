package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.Article;
import com.wks.wikisearch.model.Topic;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Repository
@Transactional
public class ArticleCustomRepository {
    private final JdbcTemplate jdbcTemplate;

    public ArticleCustomRepository(final JdbcTemplate dbTemplate) {
        this.jdbcTemplate = dbTemplate;
    }

    public List<Article> findAllArticlesWithTopics() {
        String sql = "SELECT a.id, a.title, a.url, t.id "
                + "AS topic_id, t.name AS topic_name "
                + "FROM article a "
                + "LEFT JOIN article_topic at ON a.id = at.article_id "
                + "LEFT JOIN topic t ON at.topic_id = t.id";

        Map<Long, Article> articleMap = new HashMap<>();
        jdbcTemplate.query(sql, (rs) -> {
            Long articleId = rs.getLong("id");
            Article article = articleMap.get(articleId);
            if (article == null) {
                article = new Article();
                article.setId(articleId);
                article.setTitle(rs.getString("title"));
                article.setUrl(rs.getString("url"));
                article.setTopics(new HashSet<>());
                articleMap.put(articleId, article);
            }

            Long topicId = rs.getLong("topic_id");
            if (topicId != 0) {
                Topic topic = new Topic();
                topic.setId(topicId);
                topic.setName(rs.getString("topic_name"));
                article.getTopics().add(topic);
            }
        });

        return new ArrayList<>(articleMap.values());
    }

    public Article findArticleByTitle(final String title) {
        String sql = "SELECT a.*, t.id AS topic_id, t.name AS topic_name "
                + "FROM article a "
                + "LEFT JOIN article_topic at ON a.id = at.article_id "
                + "LEFT JOIN topic t ON at.topic_id = t.id "
                + "WHERE a.title = ?";

        List<Article> articles = jdbcTemplate.query(sql,
                new Object[]{title}, (rs, rowNum) -> {
            Article a = new Article();
            a.setId(rs.getLong("id"));
            a.setTitle(rs.getString("title"));
            a.setUrl(rs.getString("url"));
            return a;
        });

        if (articles.isEmpty()) {
            return null;
        }

        Article article = articles.get(0);

        Set<Topic> topics = new HashSet<>();
        jdbcTemplate.query("SELECT t.* "
                + "FROM topic t "
                + "LEFT JOIN article_topic at ON t.id = at.topic_id "
                + "WHERE at.article_id = ?",
                new Object[]{article.getId()}, (rs, rowNum) -> {
            Topic topic = new Topic();
            topic.setId(rs.getLong("id"));
            topic.setName(rs.getString("name"));
            topics.add(topic);
            return null;
        });

        article.setTopics(topics);

        return article;
    }

    public void deleteArticle(final Long id) {
        String deleteArticleTopicSql = "DELETE FROM article_topic"
                + " WHERE article_id = ?";
        jdbcTemplate.update(deleteArticleTopicSql, id);

        String deleteArticleSql = "DELETE FROM article WHERE id = ?";
        jdbcTemplate.update(deleteArticleSql, id);
    }

    public void addTopicToArticle(final Long articleId,
                                  final Long topicId) {
        String sql = "INSERT INTO article_topic "
                + "(article_id, topic_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, articleId, topicId);
    }

    public void detachTopicFromArticle(final Long articleId,
                                       final Long topicId) {
        String sql = "DELETE FROM article_topic "
                + "WHERE article_id = ? "
                + "AND topic_id = ?";
        jdbcTemplate.update(sql, articleId, topicId);
    }

    public void updateArticle(final Article article) {
        String sql = "UPDATE article SET title = ?, url = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                article.getTitle(),
                article.getUrl(),
                article.getId());
    }
}
