package com.app.vocab.lesson;

import com.app.vocab.topic.TopicRepository;
import com.app.vocab.word.WordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final TopicRepository topicRepository;
    private final WordRepository wordRepository;

    public LessonService(LessonRepository lessonRepository, TopicRepository topicRepository, WordRepository wordRepository) {
        this.lessonRepository = lessonRepository;
        this.topicRepository = topicRepository;
        this.wordRepository = wordRepository;
    }

    public List<LessonDtos.View> getAllLessons() {
        return lessonRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toView).toList();
    }

    public LessonDtos.View getLessonById(Long id) {
        return toView(findLesson(id));
    }

    public LessonDtos.View createLesson(LessonDtos.Create request) {
        return toView(lessonRepository.save(Lesson.builder().title(request.title().trim()).build()));
    }

    public LessonDtos.View updateLesson(Long id, LessonDtos.Create request) {
        Lesson lesson = findLesson(id);
        lesson.setTitle(request.title().trim());
        return toView(lessonRepository.save(lesson));
    }

    @Transactional
    public void deleteLesson(Long id) {
        findLesson(id);
        wordRepository.deleteByTopicLessonId(id);
        topicRepository.deleteByLessonId(id);
        lessonRepository.deleteById(id);
    }

    private Lesson findLesson(Long id) {
        return lessonRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lesson not found: " + id));
    }

    private LessonDtos.View toView(Lesson lesson) {
        return new LessonDtos.View(lesson.getId(), lesson.getTitle(), lesson.getCreatedAt(),
                (int) topicRepository.countByLessonId(lesson.getId()),
                (int) wordRepository.countByTopicLessonId(lesson.getId()));
    }
}
