package com.app.vocab.domain;

import com.app.vocab.test.ScopeType;
import java.util.List;

public interface WordSource {
    ScopeType getScopeType();
    Long getScopeId();
    List<DomainPoolWord> getWords();
}
