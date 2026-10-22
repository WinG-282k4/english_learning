package com.app.vocab.ai;

import com.app.vocab.config.AppProperties;
import com.app.vocab.llm.LlmDtos;
import com.app.vocab.llm.LlmProvider;
import com.app.vocab.llm.LlmProviderType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GradeService {

    private final Map<String, LlmProvider> llmProviders;
    private final KeyService keyService;
    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public GradeService(Map<String, LlmProvider> llmProviders, KeyService keyService,
                        AppProperties appProperties, ObjectMapper objectMapper) {
        this.llmProviders = llmProviders;
        this.keyService = keyService;
        this.appProperties = appProperties;
        this.objectMapper = objectMapper;
    }

    public AiDtos.GradeResponse gradeWithAi(AiDtos.GradeRequest request) {
        try {
            LlmProviderType providerType = LlmProviderType.valueOf(appProperties.getLlm().getDefaultProvider().toUpperCase());
            String apiKey = keyService.getDecryptedKey(providerType);
            if (apiKey == null || apiKey.isBlank()) {
                return unavailable("Chưa cấu hình API key cho AI; câu trả lời được đánh dấu sai.");
            }
            LlmProvider provider = llmProviders.get(providerType.name());
            if (provider == null) return unavailable("Nhà cung cấp AI hiện không khả dụng.");
            String prompt = "Evaluate an English-to-Vietnamese vocabulary answer. Accept correct synonyms. "
                + "Return only JSON with verdict (correct, typo, or wrong) and an optional note.";
            String userMessage = objectMapper.writeValueAsString(Map.of(
                "english", request.english(), "correct_meanings", request.correctMeanings(), "user_answer", request.userAnswer()));
            LlmDtos.Response response = provider.call(new LlmDtos.Request(prompt, userMessage, true), apiKey);
            if (response == null || !response.success() || response.rawContent() == null) {
                return unavailable("AI không phản hồi; câu trả lời được chấm theo các nghĩa đã cho.");
            }
            String rawContent = response.rawContent().trim().replaceAll("^```(?:json)?\\s*|\\s*```$", "");
            JsonNode json = objectMapper.readTree(rawContent);
            AiGradeVerdict verdict = AiGradeVerdict.valueOf(json.path("verdict").asText().toUpperCase());
            String note = json.path("note").asText(null);
            return new AiDtos.GradeResponse(verdict, note);
        } catch (Exception ignored) {
            return unavailable("AI hiện không khả dụng; câu trả lời được chấm theo các nghĩa đã cho.");
        }
    }

    private AiDtos.GradeResponse unavailable(String note) {
        return new AiDtos.GradeResponse(AiGradeVerdict.WRONG, note);
    }
}
