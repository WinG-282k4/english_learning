package com.app.vocab.domain;

import com.app.vocab.test.ScopeType;
import java.util.List;

public class TopicSource implements WordSource {

    private final Long topicId;
    private final List<DomainPoolWord> words;

    public TopicSource(Long topicId, List<DomainPoolWord> words) {
        this.topicId = topicId;
        this.words = words;
    }

    @Override
    public ScopeType getScopeType() {
        return ScopeType.TOPIC;
    }

    @Override
    public Long getScopeId() {
        return topicId;
    }

    @Override
    public List<DomainPoolWord> getWords() {
        return words;
    }
}
