package com.app.vocab.domain;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Pure POJO domain class for building test pools.
 * Contains no Spring or JPA dependencies.
 */
public class PoolBuilder {

    /**
     * Builds a deduplicated test pool for a given word source (Topic or Lesson).
     *
     * @param source                 The base word source (TopicSource or LessonSource)
     * @param prevTopicsWrongWords   Wrong words from up to 3 preceding topics (ignored for LessonSource)
     * @param randomPastWrongWords   Random active wrong words from past sessions
     * @param maxRandomPastCount     Maximum random past wrong words to include (e.g. 3)
     * @return Ordered and deduplicated list of test pool words
     */
    public List<DomainPoolWord> buildPool(
            WordSource source,
            List<DomainPoolWord> prevTopicsWrongWords,
            List<DomainPoolWord> randomPastWrongWords,
            int maxRandomPastCount) {
        Map<String, DomainPoolWord> pool = new LinkedHashMap<>();
        add(pool, source.getWords());
        if (source instanceof TopicSource) add(pool, prevTopicsWrongWords);
        if (randomPastWrongWords != null && maxRandomPastCount > 0) {
            int added = 0;
            for (DomainPoolWord word : randomPastWrongWords) {
                String key = normalize(word.termNorm());
                if (!key.isBlank() && !pool.containsKey(key)) {
                    pool.put(key, word);
                    if (++added >= maxRandomPastCount) break;
                }
            }
        }
        return List.copyOf(pool.values());
    }

    private void add(Map<String, DomainPoolWord> pool, List<DomainPoolWord> words) {
        if (words == null) return;
        for (DomainPoolWord word : words) {
            String key = normalize(word.termNorm());
            if (!key.isBlank()) pool.putIfAbsent(key, word);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
