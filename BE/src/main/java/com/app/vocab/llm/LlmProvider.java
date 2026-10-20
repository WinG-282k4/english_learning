package com.app.vocab.llm;

/**
 * Strategy interface for LLM providers.
 * Injected by Spring as Map<String, LlmProvider> keyed by bean name (GEMINI, GROQ).
 */
public interface LlmProvider {

    LlmProviderType getProviderType();

    LlmDtos.Response call(LlmDtos.Request request, String decryptedApiKey);
}
