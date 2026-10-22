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
        @NotNull(message = "Round correctness is required")
        Boolean correct,
        @NotNull(message = "Grading method is required")
        GradedBy gradedBy,
        String answer
    ) {}

    public record SubmitResultItem(
        @NotNull(message = "Word ID is required")
        Long wordId,
        @NotNull(message = "Term is required")
        String term,
        @NotNull(message = "Word source is required")
        WordSourceType source,
        @NotNull @Valid
        RoundResult r1,
        @NotNull @Valid
        RoundResult r2
    ) {}

    public record Scope(
        @NotNull(message = "Scope type is required")
        ScopeType type,
        @NotNull(message = "Scope ID is required")
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

    public record SummaryResultItem(
        Long wordId,
        String term,
        WordSourceType source,
        RoundResult r1,
        RoundResult r2
    ) {}

    public record SummaryResponse(
        Long sessionId,
        ScopeType scope,
        Long scopeId,
        int score,
        int totalWords,
        int correctWords,
        int wrongWords,
        Instant createdAt,
        List<SummaryResultItem> results
    ) {}
}
