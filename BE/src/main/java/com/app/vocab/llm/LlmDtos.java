package com.app.vocab.llm;

public final class LlmDtos {
    private LlmDtos() {}

    /**
     * Generic request object passed to any LLM call.
     */
    public record Request(
        String systemPrompt,
        String userMessage,
        boolean requireJsonOutput
    ) {}

    /**
     * Generic response returned from any LLM call.
     */
    public record Response(
        String rawContent,
        boolean success,
        String errorMessage
    ) {
        public static Response ok(String rawContent) {
            return new Response(rawContent, true, null);
        }

        public static Response error(String errorMessage) {
            return new Response(null, false, errorMessage);
        }
    }
}
