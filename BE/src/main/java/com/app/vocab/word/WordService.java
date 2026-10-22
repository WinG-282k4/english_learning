package com.app.vocab.word;

import com.app.vocab.config.AppProperties;
import com.app.vocab.topic.Topic;
import com.app.vocab.topic.TopicRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordService {

    private final WordRepository wordRepository;
    private final TopicRepository topicRepository;
    private final AppProperties appProperties;

    public WordService(WordRepository wordRepository,
                       TopicRepository topicRepository,
                       AppProperties appProperties) {
        this.wordRepository = wordRepository;
        this.topicRepository = topicRepository;
        this.appProperties = appProperties;
    }

    public List<WordDtos.View> getWordsByTopic(Long topicId) {
        if (!topicRepository.existsById(topicId)) throw new EntityNotFoundException("Topic not found: " + topicId);
        return wordRepository.findByTopicIdOrderByOrderIndexAsc(topicId).stream().map(this::toView).toList();
    }

    public List<WordDtos.View> getWordsByLesson(Long lessonId) {
        return wordRepository.findAllByLessonId(lessonId).stream().map(this::toView).toList();
    }

    @Transactional
    public WordDtos.View createWord(WordDtos.Create request) {
        Topic topic = topicRepository.findById(request.topicId()).orElseThrow(() -> new EntityNotFoundException("Topic not found: " + request.topicId()));
        if (wordRepository.countByTopicId(topic.getId()) >= appProperties.getRules().getMaxWordsPerTopic()) {
            int order = topicRepository.findMaxOrderIndexByLessonId(topic.getLesson().getId()).orElse(-1) + 1;
            topic = topicRepository.save(Topic.builder().lesson(topic.getLesson()).title(topic.getTitle() + " (tiếp)").orderIndex(order).build());
        }
        int order = request.orderIndex() != null ? request.orderIndex() : wordRepository.countByTopicId(topic.getId()) > 0 ? wordRepository.findByTopicIdOrderByOrderIndexAsc(topic.getId()).getLast().getOrderIndex() + 1 : 0;
        Word word = Word.builder().topic(topic).term(request.term().trim()).termNorm(normalize(request.term())).pos(clean(request.pos())).ipa(clean(request.ipa())).meaningVi(request.meaningVi().trim()).example(clean(request.example())).orderIndex(order).build();
        return toView(wordRepository.save(word));
    }

    public WordDtos.View updateWord(Long id, WordDtos.Create request) {
        Word word = wordRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Word not found: " + id));
        Topic topic = topicRepository.findById(request.topicId()).orElseThrow(() -> new EntityNotFoundException("Topic not found: " + request.topicId()));
        if (!word.getTopic().getId().equals(topic.getId()) && wordRepository.countByTopicId(topic.getId()) >= appProperties.getRules().getMaxWordsPerTopic()) throw new IllegalArgumentException("Topic is full");
        word.setTopic(topic); word.setTerm(request.term().trim()); word.setTermNorm(normalize(request.term())); word.setPos(clean(request.pos())); word.setIpa(clean(request.ipa())); word.setMeaningVi(request.meaningVi().trim()); word.setExample(clean(request.example()));
        if (request.orderIndex() != null) word.setOrderIndex(request.orderIndex());
        return toView(wordRepository.save(word));
    }

    public void deleteWord(Long id) {
        if (!wordRepository.existsById(id)) throw new EntityNotFoundException("Word not found: " + id);
        wordRepository.deleteById(id);
    }

    private String normalize(String value) { return value.trim().toLowerCase(java.util.Locale.ROOT); }
    private String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private WordDtos.View toView(Word word) { return new WordDtos.View(word.getId(), word.getTopic().getId(), word.getTerm(), word.getTermNorm(), word.getPos(), word.getIpa(), word.getMeaningVi(), word.getExample(), word.getOrderIndex()); }
}
