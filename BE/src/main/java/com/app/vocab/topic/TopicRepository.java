package com.app.vocab.topic;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByLessonIdOrderByOrderIndexAsc(Long lessonId);

    long countByLessonId(Long lessonId);

    void deleteByLessonId(Long lessonId);

    @Query("SELECT t FROM Topic t WHERE t.lesson.id = :lessonId AND t.orderIndex < :orderIndex ORDER BY t.orderIndex DESC")
    List<Topic> findPreviousTopics(@Param("lessonId") Long lessonId, @Param("orderIndex") Integer orderIndex, Pageable pageable);

    @Query("SELECT MAX(t.orderIndex) FROM Topic t WHERE t.lesson.id = :lessonId")
    Optional<Integer> findMaxOrderIndexByLessonId(@Param("lessonId") Long lessonId);
}
