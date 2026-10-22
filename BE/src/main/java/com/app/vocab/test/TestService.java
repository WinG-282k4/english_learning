package com.app.vocab.test;

import com.app.vocab.config.AppProperties;
import com.app.vocab.domain.DomainPoolWord;
import com.app.vocab.domain.LessonSource;
import com.app.vocab.domain.PoolBuilder;
import com.app.vocab.domain.StatUpdater;
import com.app.vocab.domain.TopicSource;
import com.app.vocab.lesson.LessonRepository;
import com.app.vocab.stat.WordStat;
import com.app.vocab.stat.WordStatRepository;
import com.app.vocab.topic.Topic;
import com.app.vocab.topic.TopicRepository;
import com.app.vocab.word.Word;
import com.app.vocab.word.WordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TestService {

    private final WordRepository wordRepository;
    private final TopicRepository topicRepository;
    private final LessonRepository lessonRepository;
    private final WordStatRepository wordStatRepository;
    private final TestSessionRepository testSessionRepository;
    private final TestResultRepository testResultRepository;
    private final AppProperties appProperties;
    private final PoolBuilder poolBuilder = new PoolBuilder();
    private final StatUpdater statUpdater = new StatUpdater();

    public TestService(WordRepository wordRepository,
                       TopicRepository topicRepository,
                       LessonRepository lessonRepository,
                       WordStatRepository wordStatRepository,
                       TestSessionRepository testSessionRepository,
                       TestResultRepository testResultRepository,
                       AppProperties appProperties) {
        this.wordRepository = wordRepository;
        this.topicRepository = topicRepository;
        this.lessonRepository = lessonRepository;
        this.wordStatRepository = wordStatRepository;
        this.testSessionRepository = testSessionRepository;
        this.testResultRepository = testResultRepository;
        this.appProperties = appProperties;
    }

    @Transactional(readOnly = true)
    public TestDtos.PoolResponse getTopicTestPool(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
            .orElseThrow(() -> new EntityNotFoundException("Topic not found: " + topicId));
        List<Word> currentWords = wordRepository.findByTopicIdOrderByOrderIndexAsc(topicId);
        int maxPreviousTopics = Math.max(0, appProperties.getRules().getMaxPrevTopics());
        List<Topic> previousTopics = maxPreviousTopics == 0 ? List.of() : topicRepository.findPreviousTopics(
            topic.getLesson().getId(), topic.getOrderIndex(), PageRequest.of(0, maxPreviousTopics));

        Set<String> currentTerms = currentWords.stream().map(Word::getTermNorm).collect(Collectors.toSet());
        Set<String> activeWrongTerms = wordStatRepository.findByIsActiveWrongTrue().stream()
            .map(WordStat::getTermNorm).collect(Collectors.toSet());
        List<DomainPoolWord> previousWrongWords = previousTopics.isEmpty() ? List.of() : wordRepository
            .findAllByTopicIdIn(previousTopics.stream().map(Topic::getId).toList()).stream()
            .filter(word -> activeWrongTerms.contains(word.getTermNorm()))
            .map(word -> toDomainWord(word, WordSourceType.PREV_TOPIC))
            .toList();

        Set<String> excludedTerms = new HashSet<>(currentTerms);
        previousWrongWords.stream().map(DomainPoolWord::termNorm).forEach(excludedTerms::add);
        List<DomainPoolWord> randomPastWords = randomPastWords(excludedTerms);
        return toPoolResponse(new TopicSource(topicId, currentWords.stream()
            .map(word -> toDomainWord(word, WordSourceType.CURRENT)).toList()), previousWrongWords, randomPastWords);
    }

    @Transactional(readOnly = true)
    public TestDtos.PoolResponse getLessonTestPool(Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) throw new EntityNotFoundException("Lesson not found: " + lessonId);
        List<Word> lessonWords = wordRepository.findAllByLessonId(lessonId);
        Set<String> lessonTerms = lessonWords.stream().map(Word::getTermNorm).collect(Collectors.toSet());
        List<DomainPoolWord> randomPastWords = randomPastWords(lessonTerms);
        return toPoolResponse(new LessonSource(lessonId, lessonWords.stream()
            .map(word -> toDomainWord(word, WordSourceType.CURRENT)).toList()), List.of(), randomPastWords);
    }

    @Transactional
    public TestDtos.SubmitResponse submitTest(TestDtos.SubmitRequest request) {
        ScopeType scopeType = request.scope().type();
        Long scopeId = request.scope().id();
        Set<Long> allowedPreviousTopicIds = validateScope(scopeType, scopeId);
        Set<String> submittedTerms = new LinkedHashSet<>();
        for (TestDtos.SubmitResultItem result : request.results()) {
            if (result.r1() == null || result.r2() == null || result.r1().correct() == null || result.r2().correct() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Both test rounds must be submitted for every word.");
            }
            Word word = wordRepository.findById(result.wordId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "A test word no longer exists."));
            if (!word.getTermNorm().equals(normalizeTerm(result.term())) || !submittedTerms.add(word.getTermNorm())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Test words are invalid or duplicated.");
            }
            validateSource(scopeType, scopeId, allowedPreviousTopicIds, result.source(), word);
        }
        Set<String> requiredTerms = requiredPoolTerms(scopeType, scopeId, allowedPreviousTopicIds);
        if (!submittedTerms.containsAll(requiredTerms)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The submission is missing required words from the selected scope.");
        }
        long randomPastCount = request.results().stream().filter(result -> result.source() == WordSourceType.RANDOM_PAST).count();
        if (randomPastCount > appProperties.getRules().getRandomPastWrongCount()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The submission contains too many review words.");
        }

        int correctAnswers = 0;
        int correctWords = 0;
        for (int i = 0; i < request.results().size(); i++) {
            TestDtos.SubmitResultItem result = request.results().get(i);
            if (result.r1().correct()) correctAnswers++;
            if (result.r2().correct()) correctAnswers++;
            if (result.r1().correct() && result.r2().correct()) correctWords++;
        }
        int totalWords = request.results().size();
        int score = totalWords == 0 ? 0 : Math.round(correctAnswers * 100f / (totalWords * 2));
        TestSession session = testSessionRepository.save(TestSession.builder()
            .scope(scopeType).scopeId(scopeId).score(score).build());
        List<TestResult> savedResults = new ArrayList<>(totalWords);
        Map<String, WordStat> stats = request.results().stream().collect(Collectors.toMap(
            result -> normalizeTerm(result.term()),
            result -> wordStatRepository.findById(normalizeTerm(result.term()))
                .orElseGet(() -> WordStat.builder().termNorm(normalizeTerm(result.term())).build()),
            (first, ignored) -> first));
        Instant eventTime = Instant.now();
        for (TestDtos.SubmitResultItem result : request.results()) {
            WordStat stat = stats.get(normalizeTerm(result.term()));
            statUpdater.applyAnswer(stat, result.r1().correct(), appProperties.getRules().getResolveWrongThreshold(), eventTime);
            statUpdater.applyAnswer(stat, result.r2().correct(), appProperties.getRules().getResolveWrongThreshold(), eventTime);
            savedResults.add(TestResult.builder()
                .session(session)
                .wordId(result.wordId())
                .termNorm(normalizeTerm(result.term()))
                .source(result.source())
                .r1Correct(result.r1().correct()).r1Answer(result.r1().answer()).r1GradedBy(result.r1().gradedBy())
                .r2Correct(result.r2().correct()).r2Answer(result.r2().answer()).r2GradedBy(result.r2().gradedBy())
                .build());
        }
        wordStatRepository.saveAll(stats.values());
        testResultRepository.saveAll(savedResults);
        return new TestDtos.SubmitResponse(session.getId(), score, totalWords, correctWords,
            totalWords - correctWords, session.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public TestDtos.SummaryResponse getTestSummary(Long sessionId) {
        TestSession session = testSessionRepository.findById(sessionId)
            .orElseThrow(() -> new EntityNotFoundException("Test session not found: " + sessionId));
        List<TestDtos.SummaryResultItem> results = testResultRepository.findBySessionId(sessionId).stream()
            .map(result -> new TestDtos.SummaryResultItem(result.getWordId(),
                wordRepository.findById(result.getWordId()).map(Word::getTerm).orElse(result.getTermNorm()),
                result.getSource(),
                new TestDtos.RoundResult(result.getR1Correct(), result.getR1GradedBy(), result.getR1Answer()),
                new TestDtos.RoundResult(result.getR2Correct(), result.getR2GradedBy(), result.getR2Answer())))
            .toList();
        int correctWords = (int) results.stream().filter(result -> result.r1().correct() && result.r2().correct()).count();
        return new TestDtos.SummaryResponse(session.getId(), session.getScope(), session.getScopeId(), session.getScore(),
            results.size(), correctWords, results.size() - correctWords, session.getCreatedAt(), results);
    }

    private Set<Long> validateScope(ScopeType scopeType, Long scopeId) {
        if (scopeType == ScopeType.LESSON) {
            if (!lessonRepository.existsById(scopeId)) throw new EntityNotFoundException("Lesson not found: " + scopeId);
            return Set.of();
        }
        Topic topic = topicRepository.findById(scopeId)
            .orElseThrow(() -> new EntityNotFoundException("Topic not found: " + scopeId));
        int maxPreviousTopics = Math.max(0, appProperties.getRules().getMaxPrevTopics());
        if (maxPreviousTopics == 0) return Set.of();
        return topicRepository.findPreviousTopics(topic.getLesson().getId(), topic.getOrderIndex(), PageRequest.of(0, maxPreviousTopics))
            .stream().map(Topic::getId).collect(Collectors.toSet());
    }

    private Set<String> requiredPoolTerms(ScopeType scopeType, Long scopeId, Set<Long> previousTopicIds) {
        Set<String> required = new HashSet<>();
        if (scopeType == ScopeType.LESSON) {
            wordRepository.findAllByLessonId(scopeId).stream().map(Word::getTermNorm).forEach(required::add);
            return required;
        }
        wordRepository.findByTopicIdOrderByOrderIndexAsc(scopeId).stream().map(Word::getTermNorm).forEach(required::add);
        if (!previousTopicIds.isEmpty()) {
            Set<String> activeWrongTerms = wordStatRepository.findByIsActiveWrongTrue().stream()
                .map(WordStat::getTermNorm).collect(Collectors.toSet());
            wordRepository.findAllByTopicIdIn(new ArrayList<>(previousTopicIds)).stream()
                .map(Word::getTermNorm).filter(activeWrongTerms::contains).forEach(required::add);
        }
        return required;
    }

    private void validateSource(ScopeType scopeType, Long scopeId, Set<Long> previousTopicIds,
                                WordSourceType source, Word word) {
        boolean valid = switch (source) {
            case CURRENT -> scopeType == ScopeType.LESSON
                ? word.getTopic().getLesson().getId().equals(scopeId)
                : word.getTopic().getId().equals(scopeId);
            case PREV_TOPIC -> scopeType == ScopeType.TOPIC && previousTopicIds.contains(word.getTopic().getId())
                && wordStatRepository.findById(word.getTermNorm()).map(WordStat::isActiveWrong).orElse(false);
            case RANDOM_PAST -> (scopeType == ScopeType.LESSON
                ? !word.getTopic().getLesson().getId().equals(scopeId)
                : !word.getTopic().getId().equals(scopeId) && !previousTopicIds.contains(word.getTopic().getId()))
                && wordStatRepository.findById(word.getTermNorm()).map(WordStat::isActiveWrong).orElse(false);
        };
        if (!valid) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A test word is outside the selected test pool.");
    }

    private List<DomainPoolWord> randomPastWords(Set<String> excludedTerms) {
        int limit = Math.max(0, appProperties.getRules().getRandomPastWrongCount());
        if (limit == 0) return List.of();
        Set<String> excluded = excludedTerms.isEmpty() ? Set.of("") : excludedTerms;
        List<WordStat> candidates = wordStatRepository.findRandomActiveWrongWords(excluded,
            PageRequest.of(0, Math.max(limit * 4, limit)));
        List<DomainPoolWord> randomWords = new ArrayList<>();
        Set<String> seen = new HashSet<>(excludedTerms);
        for (WordStat stat : candidates) {
            if (!seen.add(stat.getTermNorm())) continue;
            wordRepository.findFirstByTermNormOrderByIdAsc(stat.getTermNorm())
                .ifPresent(word -> randomWords.add(toDomainWord(word, WordSourceType.RANDOM_PAST)));
            if (randomWords.size() == limit) break;
        }
        return randomWords;
    }

    private TestDtos.PoolResponse toPoolResponse(com.app.vocab.domain.WordSource source,
                                                  List<DomainPoolWord> previousWrongWords,
                                                  List<DomainPoolWord> randomPastWords) {
        List<DomainPoolWord> pool = poolBuilder.buildPool(source, previousWrongWords, randomPastWords,
            appProperties.getRules().getRandomPastWrongCount());
        List<TestDtos.PoolWord> words = pool.stream().map(word -> new TestDtos.PoolWord(word.wordId(), word.term(),
            word.termNorm(), word.pos(), word.ipa(), word.meaningVi(), word.example(), word.source())).toList();
        return new TestDtos.PoolResponse(source.getScopeType(), source.getScopeId(), words.size(), words);
    }

    private DomainPoolWord toDomainWord(Word word, WordSourceType source) {
        return new DomainPoolWord(word.getId(), word.getTerm(), word.getTermNorm(), word.getPos(), word.getIpa(),
            word.getMeaningVi(), word.getExample(), source);
    }

    private String normalizeTerm(String term) {
        return term == null ? "" : term.trim().toLowerCase(Locale.ROOT);
    }
}
