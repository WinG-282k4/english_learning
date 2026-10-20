package com.app.vocab.llm;

import org.springframework.stereotype.Component;

@Component("GROQ")
public class GroqProvider implements LlmProvider {

    @Override
    public LlmProviderType getProviderType() {
        return LlmProviderType.GROQ;
    }

    @Override
    public LlmDtos.Response call(LlmDtos.Request request, String decryptedApiKey) {
        // TODO: call Groq REST API via RestClient
        // - Use response_format: json_object when request.requireJsonOutput() is true
        // - Apply timeout from AppProperties
        // - Return LlmDtos.Response.ok(rawJson) or LlmDtos.Response.error(message)
        return null;
    }
}
