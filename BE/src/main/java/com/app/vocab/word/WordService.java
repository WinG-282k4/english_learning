package com.app.vocab.word;

import com.app.vocab.lesson.LessonRepository;
import com.app.vocab.topic.Topic;
import com.app.vocab.topic.TopicRepository;
import com.app.vocab.topic.TopicDtos;
import com.app.vocab.test.ScopeType;
import com.app.vocab.test.TestSessionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WordService {

    private final WordRepository wordRepository;
    private final TopicRepository topicRepository;
    private final LessonRepository lessonRepository;
    private final TestSessionRepository testSessionRepository;

    public WordService(WordRepository wordRepository,
                       TopicRepository topicRepository,
                       LessonRepository lessonRepository,
                       TestSessionRepository testSessionRepository) {
        this.wordRepository = wordRepository;
        this.topicRepository = topicRepository;
        this.lessonRepository = lessonRepository;
        this.testSessionRepository = testSessionRepository;
    }

    public List<WordDtos.View> getWordsByTopic(Long topicId) {
        if (!topicRepository.existsById(topicId)) throw new EntityNotFoundException("Topic not found: " + topicId);
        return wordRepository.findByTopicIdOrderByOrderIndexAsc(topicId).stream().map(this::toView).toList();
    }

    @Transactional(readOnly = true)
    public List<WordDtos.View> searchTopic(Long topicId, String query) {
        if (!topicRepository.existsById(topicId)) throw new EntityNotFoundException("Topic not found: " + topicId);
        String normalizedQuery = normalizeSearch(query);
        if (normalizedQuery.isBlank()) return List.of();
        return wordRepository.findByTopicIdOrderByOrderIndexAsc(topicId).stream()
            .filter(word -> matchesWord(word, normalizedQuery))
            .map(this::toView)
            .toList();
    }

    public List<WordDtos.View> getWordsByLesson(Long lessonId) {
        return wordRepository.findAllByLessonId(lessonId).stream().map(this::toView).toList();
    }

    @Transactional(readOnly = true)
    public WordDtos.LessonSearchResult searchLesson(Long lessonId, String query) {
        if (!lessonRepository.existsById(lessonId)) throw new EntityNotFoundException("Lesson not found: " + lessonId);
        String normalizedQuery = normalizeSearch(query);
        if (normalizedQuery.isBlank()) return new WordDtos.LessonSearchResult(List.of(), List.of());

        List<Topic> lessonTopics = topicRepository.findByLessonIdOrderByOrderIndexAsc(lessonId);
        List<Word> lessonWords = wordRepository.findAllByLessonId(lessonId);
        Set<Long> matchingTopicIds = lessonTopics.stream()
            .filter(topic -> normalizeSearch(topic.getTitle()).contains(normalizedQuery))
            .map(Topic::getId)
            .collect(Collectors.toSet());
        List<Word> matchingWords = lessonWords.stream()
            .filter(word -> matchingTopicIds.contains(word.getTopic().getId()) || matchesWord(word, normalizedQuery))
            .toList();
        Set<Long> resultTopicIds = matchingWords.stream().map(word -> word.getTopic().getId()).collect(Collectors.toSet());
        resultTopicIds.addAll(matchingTopicIds);
        List<TopicDtos.View> matchingTopics = lessonTopics.stream()
            .filter(topic -> resultTopicIds.contains(topic.getId()))
            .map(topic -> {
                var latestTest = testSessionRepository.findFirstByScopeAndScopeIdOrderByCreatedAtDesc(ScopeType.TOPIC, topic.getId());
                return new TopicDtos.View(topic.getId(), topic.getLesson().getId(), topic.getTitle(), topic.getOrderIndex(),
                    (int) wordRepository.countByTopicId(topic.getId()), (int) wordRepository.countByTopicIdAndLearnedTrue(topic.getId()),
                    latestTest.map(session -> session.getScore()).orElse(null),
                    latestTest.map(session -> session.getCreatedAt()).orElse(null));
            })
            .toList();
        return new WordDtos.LessonSearchResult(matchingTopics, matchingWords.stream().map(this::toView).toList());
    }

    private boolean matchesWord(Word word, String query) {
        return java.util.stream.Stream.of(word.getTerm(), word.getTermNorm(), word.getPos(), word.getIpa(), word.getMeaningVi(), word.getExample())
            .filter(value -> value != null).anyMatch(value -> normalizeSearch(value).contains(query));
    }

    private String normalizeSearch(String value) {
        if (value == null) return "";
        return Normalizer.normalize(value.toLowerCase(Locale.ROOT).replace('đ', 'd'), Normalizer.Form.NFD)
            .replaceAll("\\p{M}+", "").trim();
    }

    @Transactional
    public WordDtos.View createWord(WordDtos.Create request) {
        Topic topic = topicRepository.findById(request.topicId()).orElseThrow(() -> new EntityNotFoundException("Topic not found: " + request.topicId()));
        int order = request.orderIndex() != null ? request.orderIndex() : wordRepository.countByTopicId(topic.getId()) > 0 ? wordRepository.findByTopicIdOrderByOrderIndexAsc(topic.getId()).getLast().getOrderIndex() + 1 : 0;
        Word word = Word.builder().topic(topic).term(request.term().trim()).termNorm(normalize(request.term())).pos(clean(request.pos())).ipa(clean(request.ipa())).meaningVi(request.meaningVi().trim()).example(clean(request.example())).orderIndex(order).build();
        return toView(wordRepository.save(word));
    }

    @Transactional
    public List<WordDtos.View> createWords(WordDtos.BatchCreate request) {
        Topic topic = topicRepository.findById(request.topicId())
            .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Topic not found: " + request.topicId()));
        int order = wordRepository.findMaxOrderIndexByTopicId(topic.getId()).orElse(-1) + 1;
        int[] nextOrder = {order};
        List<Word> newWords = request.words().stream().map(item -> {
            String term = item.term().trim();
            return Word.builder().topic(topic).term(term).termNorm(normalize(term)).pos(clean(item.pos()))
                .ipa(clean(item.ipa())).meaningVi(item.meaningVi().trim()).example(clean(item.example()))
                .orderIndex(nextOrder[0]++).build();
        }).toList();
        return wordRepository.saveAll(newWords).stream().map(this::toView).toList();
    }

    public WordDtos.View updateWord(Long id, WordDtos.Create request) {
        Word word = wordRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Word not found: " + id));
        Topic topic = topicRepository.findById(request.topicId()).orElseThrow(() -> new EntityNotFoundException("Topic not found: " + request.topicId()));
        word.setTopic(topic); word.setTerm(request.term().trim()); word.setTermNorm(normalize(request.term())); word.setPos(clean(request.pos())); word.setIpa(clean(request.ipa())); word.setMeaningVi(request.meaningVi().trim()); word.setExample(clean(request.example()));
        if (request.orderIndex() != null) word.setOrderIndex(request.orderIndex());
        return toView(wordRepository.save(word));
    }

    @Transactional
    public WordDtos.View markLearned(Long id) {
        Word word = wordRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Word not found: " + id));
        word.setLearned(true);
        return toView(wordRepository.save(word));
    }

    public void deleteWord(Long id) {
        if (!wordRepository.existsById(id)) throw new EntityNotFoundException("Word not found: " + id);
        wordRepository.deleteById(id);
    }

    private String normalize(String value) { return value.trim().toLowerCase(java.util.Locale.ROOT); }
    private String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private WordDtos.View toView(Word word) { return new WordDtos.View(word.getId(), word.getTopic().getId(), word.getTerm(), word.getTermNorm(), word.getPos(), word.getIpa(), word.getMeaningVi(), word.getExample(), word.getOrderIndex(), Boolean.TRUE.equals(word.getLearned())); }
}
