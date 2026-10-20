package com.app.vocab.llm;

import org.springframework.stereotype.Component;

@Component("GEMINI")
public class GeminiProvider implements LlmProvider {

    @Override
    public LlmProviderType getProviderType() {
        return LlmProviderType.GEMINI;
    }

    @Override
    public LlmDtos.Response call(LlmDtos.Request request, String decryptedApiKey) {
        // TODO: call Gemini REST API via RestClient
        // - Use responseMimeType: application/json when request.requireJsonOutput() is true
        // - Apply timeout from AppProperties
        // - Return LlmDtos.Response.ok(rawJson) or LlmDtos.Response.error(message)
        return null;
    }
}
