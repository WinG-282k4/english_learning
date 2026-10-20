package com.app.vocab.lesson;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public final class LessonDtos {
    private LessonDtos() {}

    public record View(
        Long id,
        String title,
        Instant createdAt,
        int topicCount,
        int totalWords
    ) {}

    public record Create(
        @NotBlank(message = "Lesson title cannot be blank")
        String title
    ) {}
}
