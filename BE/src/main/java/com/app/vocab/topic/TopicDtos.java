package com.app.vocab.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class TopicDtos {
    private TopicDtos() {}

    public record View(
        Long id,
        Long lessonId,
        String title,
        Integer orderIndex,
        int wordCount
    ) {}

    public record Create(
        @NotNull(message = "Lesson ID is required")
        Long lessonId,

        @NotBlank(message = "Topic title cannot be blank")
        String title,

        Integer orderIndex
    ) {}
}
