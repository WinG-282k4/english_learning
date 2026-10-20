package com.app.vocab.domain;

import java.util.List;

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
        // TODO: implement deduplication by term_norm and pool assembly logic
        return null;
    }
}
