package com.app.vocab.word;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByTopicIdOrderByOrderIndexAsc(Long topicId);

    Optional<Word> findFirstByTermNormOrderByIdAsc(String termNorm);

    long countByTopicId(Long topicId);

    @Query("SELECT MAX(w.orderIndex) FROM Word w WHERE w.topic.id = :topicId")
    Optional<Integer> findMaxOrderIndexByTopicId(@Param("topicId") Long topicId);

    long countByTopicLessonId(Long lessonId);

    void deleteByTopicId(Long topicId);

    void deleteByTopicLessonId(Long lessonId);

    @Query("SELECT w FROM Word w WHERE w.topic.lesson.id = :lessonId ORDER BY w.topic.orderIndex ASC, w.orderIndex ASC")
    List<Word> findAllByLessonId(@Param("lessonId") Long lessonId);

    @Query("SELECT w FROM Word w WHERE w.topic.id IN :topicIds ORDER BY w.topic.orderIndex ASC, w.orderIndex ASC")
    List<Word> findAllByTopicIdIn(@Param("topicIds") List<Long> topicIds);
}
