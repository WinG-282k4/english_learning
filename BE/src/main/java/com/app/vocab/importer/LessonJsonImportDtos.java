package com.app.vocab.importer;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.util.List;

public final class LessonJsonImportDtos {
    private LessonJsonImportDtos() {}

    public record LessonFile(
        @JsonAlias({"lesson", "name"}) String title,
        List<TopicFile> topics
    ) {}

    public record TopicFile(
        String title,
        List<WordFile> words
    ) {}

    public record WordFile(
        String term,
        String pos,
        String ipa,
        String meaningVi,
        String example
    ) {}
}
