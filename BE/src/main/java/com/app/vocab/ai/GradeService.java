package com.app.vocab.ai;

import com.app.vocab.llm.LlmProvider;
import com.app.vocab.llm.LlmProviderType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GradeService {

    private final Map<String, LlmProvider> llmProviders;
    private final KeyService keyService;

    public GradeService(Map<String, LlmProvider> llmProviders, KeyService keyService) {
        this.llmProviders = llmProviders;
        this.keyService = keyService;
    }

    public AiDtos.GradeResponse gradeWithAi(AiDtos.GradeRequest request) {
        // TODO:
        // 1. Determine active provider (default or grading-specific)
        // 2. keyService.getDecryptedKey(provider)
        // 3. Build LlmRequest with grade_answer.txt prompt
        // 4. llmProvider.call(request, decryptedKey)
        // 5. Parse JSON response -> AiDtos.GradeResponse
        // 6. On timeout/error: return GradeResponse(WRONG, "AI không khả dụng")
        return null;
    }
}
