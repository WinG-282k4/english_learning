package com.app.vocab.test;

import com.app.vocab.config.AppProperties;
import com.app.vocab.domain.PoolBuilder;
import com.app.vocab.domain.StatUpdater;
import com.app.vocab.stat.WordStatRepository;
import com.app.vocab.topic.TopicRepository;
import com.app.vocab.word.WordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestService {

    private final WordRepository wordRepository;
    private final TopicRepository topicRepository;
    private final WordStatRepository wordStatRepository;
    private final TestSessionRepository testSessionRepository;
    private final TestResultRepository testResultRepository;
    private final AppProperties appProperties;
    private final PoolBuilder poolBuilder = new PoolBuilder();
    private final StatUpdater statUpdater = new StatUpdater();

    public TestService(WordRepository wordRepository,
                       TopicRepository topicRepository,
                       WordStatRepository wordStatRepository,
                       TestSessionRepository testSessionRepository,
                       TestResultRepository testResultRepository,
                       AppProperties appProperties) {
        this.wordRepository = wordRepository;
        this.topicRepository = topicRepository;
        this.wordStatRepository = wordStatRepository;
        this.testSessionRepository = testSessionRepository;
        this.testResultRepository = testResultRepository;
        this.appProperties = appProperties;
    }

    public TestDtos.PoolResponse getTopicTestPool(Long topicId) {
        // TODO: assemble TopicSource, fetch 3 prev topics wrong words and random past wrong words, call PoolBuilder
        return null;
    }

    public TestDtos.PoolResponse getLessonTestPool(Long lessonId) {
        // TODO: assemble LessonSource, fetch random past wrong words, call PoolBuilder
        return null;
    }

    @Transactional
    public TestDtos.SubmitResponse submitTest(TestDtos.SubmitRequest request) {
        // TODO: sequential replay of r1 and r2 for each word, update WordStat via StatUpdater, save TestSession & TestResults
        return null;
    }
}
