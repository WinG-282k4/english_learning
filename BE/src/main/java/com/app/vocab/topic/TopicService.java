package com.app.vocab.topic;

import com.app.vocab.lesson.LessonRepository;
import com.app.vocab.word.WordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final LessonRepository lessonRepository;
    private final WordRepository wordRepository;

    public TopicService(TopicRepository topicRepository,
                        LessonRepository lessonRepository,
                        WordRepository wordRepository) {
        this.topicRepository = topicRepository;
        this.lessonRepository = lessonRepository;
        this.wordRepository = wordRepository;
    }

    public List<TopicDtos.View> getTopicsByLesson(Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) throw new EntityNotFoundException("Lesson not found: " + lessonId);
        return topicRepository.findByLessonIdOrderByOrderIndexAsc(lessonId).stream().map(this::toView).toList();
    }

    public TopicDtos.View getTopicById(Long id) {
        return toView(findTopic(id));
    }

    public TopicDtos.View createTopic(TopicDtos.Create request) {
        var lesson = lessonRepository.findById(request.lessonId()).orElseThrow(() -> new EntityNotFoundException("Lesson not found: " + request.lessonId()));
        int order = request.orderIndex() != null ? request.orderIndex() : topicRepository.findMaxOrderIndexByLessonId(request.lessonId()).orElse(-1) + 1;
        return toView(topicRepository.save(Topic.builder().lesson(lesson).title(request.title().trim()).orderIndex(order).build()));
    }

    public TopicDtos.View updateTopic(Long id, TopicDtos.Create request) {
        Topic topic = findTopic(id);
        var lesson = lessonRepository.findById(request.lessonId()).orElseThrow(() -> new EntityNotFoundException("Lesson not found: " + request.lessonId()));
        topic.setLesson(lesson);
        topic.setTitle(request.title().trim());
        if (request.orderIndex() != null) topic.setOrderIndex(request.orderIndex());
        return toView(topicRepository.save(topic));
    }

    @Transactional
    public void deleteTopic(Long id) {
        findTopic(id);
        wordRepository.deleteByTopicId(id);
        topicRepository.deleteById(id);
    }

    private Topic findTopic(Long id) { return topicRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Topic not found: " + id)); }
    private TopicDtos.View toView(Topic topic) { return new TopicDtos.View(topic.getId(), topic.getLesson().getId(), topic.getTitle(), topic.getOrderIndex(), (int) wordRepository.countByTopicId(topic.getId())); }
}
