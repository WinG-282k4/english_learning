package com.app.vocab.word;

import com.app.vocab.topic.TopicDtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

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
        Integer orderIndex,
        boolean learned
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

    public record BatchWord(
        @NotBlank(message = "Term is required") String term,
        String pos,
        String ipa,
        @NotBlank(message = "Vietnamese meaning is required") String meaningVi,
        String example
    ) {}

    public record BatchCreate(
        @NotNull(message = "Topic ID is required") Long topicId,
        @NotEmpty(message = "At least one word is required") List<@Valid BatchWord> words
    ) {}

    public record LessonSearchResult(List<TopicDtos.View> topics, List<View> words) {}
}
