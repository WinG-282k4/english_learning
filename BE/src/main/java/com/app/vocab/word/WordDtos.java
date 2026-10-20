package com.app.vocab.word;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class WordDtos {
    private WordDtos() {}

    public record View(
        Long id,
        Long topicId,
        String term,
        String termNorm,
        String pos,
        String ipa,
        String meaningVi,
        String example,
        Integer orderIndex
    ) {}

    public record Create(
        @NotNull(message = "Topic ID is required")
        Long topicId,

        @NotBlank(message = "Term is required")
        String term,

        String pos,
        String ipa,

        @NotBlank(message = "Vietnamese meaning is required")
        String meaningVi,

        String example,
        Integer orderIndex
    ) {}
}
