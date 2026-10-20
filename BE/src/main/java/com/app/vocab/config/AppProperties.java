package com.app.vocab.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private Rules rules = new Rules();
    private Crypto crypto = new Crypto();
    private Llm llm = new Llm();

    @Getter
    @Setter
    public static class Rules {
        private int maxWordsPerTopic = 12;
        private int maxPrevTopics = 3;
        private int randomPastWrongCount = 3;
        private int resolveWrongThreshold = 3;
    }

    @Getter
    @Setter
    public static class Crypto {
        private String secretKey;
    }

    @Getter
    @Setter
    public static class Llm {
        private String defaultProvider = "GROQ";
        private int timeoutSeconds = 15;
    }
}
