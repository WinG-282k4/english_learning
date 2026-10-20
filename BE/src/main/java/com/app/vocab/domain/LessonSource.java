package com.app.vocab.domain;

import com.app.vocab.test.ScopeType;
import java.util.List;

public class LessonSource implements WordSource {

    private final Long lessonId;
    private final List<DomainPoolWord> words;

    public LessonSource(Long lessonId, List<DomainPoolWord> words) {
        this.lessonId = lessonId;
        this.words = words;
    }

    @Override
    public ScopeType getScopeType() {
        return ScopeType.LESSON;
    }

    @Override
    public Long getScopeId() {
        return lessonId;
    }

    @Override
    public List<DomainPoolWord> getWords() {
        return words;
    }
}
