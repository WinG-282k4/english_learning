package com.app.vocab.domain;

import com.app.vocab.test.WordSourceType;

public record DomainPoolWord(
    Long wordId,
    String term,
    String termNorm,
    String pos,
    String ipa,
    String meaningVi,
    String example,
    WordSourceType source
) {}
