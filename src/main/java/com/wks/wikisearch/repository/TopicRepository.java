package com.wks.wikisearch.repository;

import com.wks.wikisearch.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByName(String name);
}