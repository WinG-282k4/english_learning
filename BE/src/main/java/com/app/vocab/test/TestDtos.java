package com.app.vocab.test;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public final class TestDtos {
    private TestDtos() {}

    public record PoolWord(
        Long wordId,
        String term,
        String termNorm,
        String pos,
        String ipa,
        String meaningVi,
        String example,
        WordSourceType source
    ) {}

    public record PoolResponse(
        ScopeType scope,
        Long scopeId,
        int totalWords,
        List<PoolWord> words
    ) {}

    public record RoundResult(
        Boolean correct,
        GradedBy gradedBy,
        String answer
    ) {}

    public record SubmitResultItem(
        Long wordId,
        String term,
        WordSourceType source,
        RoundResult r1,
        RoundResult r2
    ) {}

    public record Scope(
        ScopeType type,
        Long id
    ) {}

    public record SubmitRequest(
        @NotNull(message = "Scope is required")
        @Valid
        Scope scope,

        @NotEmpty(message = "Results cannot be empty")
        @Valid
        List<SubmitResultItem> results
    ) {}

    public record SubmitResponse(
        Long sessionId,
        int score,
        int totalWords,
        int correctWords,
        int wrongWords,
        Instant createdAt
    ) {}
}
