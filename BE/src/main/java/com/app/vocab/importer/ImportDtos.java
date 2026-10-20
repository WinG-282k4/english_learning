package com.app.vocab.importer;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public final class ImportDtos {
    private ImportDtos() {}

    public record PreviewWord(
        String term,
        String termNorm,
        String pos,
        String ipa,
        String meaningVi,
        String example
    ) {}

    public record PreviewTopic(
        String title,
        Integer orderIndex,
        List<PreviewWord> words
    ) {}

    public record JobStatus(
        String jobId,
        Long lessonId,
        String status,
        int progressPercentage,
        String message,
        List<PreviewTopic> previewTopics
    ) {}

    public record CommitRequest(
        @NotEmpty(message = "Topics cannot be empty")
        List<PreviewTopic> topics
    ) {}
}
