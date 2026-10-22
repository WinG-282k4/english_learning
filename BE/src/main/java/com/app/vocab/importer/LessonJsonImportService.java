package com.app.vocab.importer;

import com.app.vocab.lesson.Lesson;
import com.app.vocab.lesson.LessonDtos;
import com.app.vocab.lesson.LessonRepository;
import com.app.vocab.topic.Topic;
import com.app.vocab.topic.TopicRepository;
import com.app.vocab.word.Word;
import com.app.vocab.word.WordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Locale;

@Service
public class LessonJsonImportService {
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

    private final ObjectMapper objectMapper;
    private final LessonRepository lessonRepository;
    private final TopicRepository topicRepository;
    private final WordRepository wordRepository;

    public LessonJsonImportService(ObjectMapper objectMapper, LessonRepository lessonRepository,
                                   TopicRepository topicRepository, WordRepository wordRepository) {
        this.objectMapper = objectMapper;
        this.lessonRepository = lessonRepository;
        this.topicRepository = topicRepository;
        this.wordRepository = wordRepository;
    }

    @Transactional
    public LessonDtos.View createNewLesson(LessonJsonImportDtos.LessonFile data) {
        validate(data, true);
        return saveNewLesson(data);
    }

    private LessonDtos.View saveNewLesson(LessonJsonImportDtos.LessonFile data) {
        Lesson lesson = lessonRepository.save(Lesson.builder().title(data.title().trim()).build());
        return appendContent(lesson, data);
    }

    @Transactional
    public LessonDtos.View importIntoLesson(Long lessonId, LessonJsonImportDtos.LessonFile data) {
        validate(data, false);
        Lesson lesson = lessonRepository.findById(lessonId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bài học."));
        return appendContent(lesson, data);
    }

    private LessonDtos.View appendContent(Lesson lesson, LessonJsonImportDtos.LessonFile data) {
        int topicOrder = topicRepository.findMaxOrderIndexByLessonId(lesson.getId()).orElse(-1) + 1;
        for (var topicData : data.topics()) {
            Topic topic = topicRepository.save(Topic.builder().lesson(lesson).title(topicData.title().trim()).orderIndex(topicOrder++).build());
            int wordOrder = 0;
            for (var wordData : topicData.words()) {
                String term = wordData.term().trim();
                wordRepository.save(Word.builder().topic(topic).term(term).termNorm(term.toLowerCase(Locale.ROOT))
                    .pos(clean(wordData.pos())).ipa(clean(wordData.ipa())).meaningVi(wordData.meaningVi().trim())
                    .example(clean(wordData.example())).orderIndex(wordOrder++).build());
            }
        }
        return new LessonDtos.View(lesson.getId(), lesson.getTitle(), lesson.getCreatedAt(), (int) topicRepository.countByLessonId(lesson.getId()),
            (int) wordRepository.countByTopicLessonId(lesson.getId()));
    }

    private void validate(LessonJsonImportDtos.LessonFile data, boolean requireTitle) {
        if (data == null || (requireTitle && (data.title() == null || data.title().isBlank())) || data.topics() == null || data.topics().isEmpty()) {
            throw badRequest(requireTitle ? "JSON cần có tên bài học và ít nhất một topic." : "JSON cần có ít nhất một topic.");
        }
        try {
            if (objectMapper.writeValueAsBytes(data).length > MAX_FILE_SIZE) throw badRequest("Dữ liệu JSON không được vượt quá 2 MB.");
        } catch (IOException e) {
            throw badRequest("Không thể kiểm tra dữ liệu JSON.");
        }
        for (var topic : data.topics()) {
            if (topic == null || topic.title() == null || topic.title().isBlank() || topic.words() == null || topic.words().isEmpty()) {
                throw badRequest("Mỗi topic cần có tên và ít nhất một từ.");
            }
            for (var word : topic.words()) {
                if (word == null || word.term() == null || word.term().isBlank() || word.meaningVi() == null || word.meaningVi().isBlank()) {
                    throw badRequest("Mỗi từ cần có term và meaningVi.");
                }
            }
        }
    }

    private String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private ResponseStatusException badRequest(String message) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, message); }
}
