package com.app.vocab.ai;

import com.app.vocab.llm.LlmProviderType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public final class AiDtos {
    private AiDtos() {}

    public record KeyView(
        Long id,
        LlmProviderType provider,
        String maskedKey,
        boolean isDefault
    ) {}

    public record SaveKeyRequest(
        @NotNull(message = "Provider is required")
        LlmProviderType provider,

        @NotBlank(message = "API key cannot be blank")
        String rawApiKey,

        boolean isDefault
    ) {}

    public record GradeRequest(
        @NotBlank(message = "English term is required")
        String english,

        @JsonProperty("correct_meanings")
        @NotEmpty(message = "Correct meanings cannot be empty")
        List<String> correctMeanings,

        @JsonProperty("user_answer")
        @NotBlank(message = "User answer is required")
        String userAnswer
    ) {}

    public record GradeResponse(
        AiGradeVerdict verdict,
        String note
    ) {}

    public record GenerateVocabRequest(
        @NotBlank(message = "Term cannot be blank")
        String term
    ) {}

    public record ExtractedItem(
        @NotBlank String term,
        String pos,
        String ipa,
        @JsonProperty("meaning_vi") @NotBlank String meaningVi,
        String example
    ) {}
}
