package com.app.vocab.lesson;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public List<LessonDtos.View> getAllLessons() {
        // TODO: implement retrieve all lessons
        return null;
    }

    public LessonDtos.View getLessonById(Long id) {
        // TODO: implement retrieve single lesson
        return null;
    }

    public LessonDtos.View createLesson(LessonDtos.Create request) {
        // TODO: implement create lesson
        return null;
    }

    public LessonDtos.View updateLesson(Long id, LessonDtos.Create request) {
        // TODO: implement update lesson
        return null;
    }

    public void deleteLesson(Long id) {
        // TODO: implement delete lesson
    }
}
