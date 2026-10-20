package com.app.vocab.domain;

import com.app.vocab.stat.WordStat;

import java.time.Instant;

/**
 * Pure POJO domain class for updating WordStat progression.
 * Contains no Spring or JPA dependencies.
 */
public class StatUpdater {

    /**
     * Applies a single answer evaluation to a WordStat record.
     *
     * @param stat             The WordStat entity to update (or newly initialized)
     * @param isCorrect        Whether the answer for this step was correct
     * @param resolveThreshold Threshold of accumulated correct answers to resolve a wrong word (e.g. 3)
     * @param eventTime        Timestamp of the event
     */
    public void applyAnswer(WordStat stat, boolean isCorrect, int resolveThreshold, Instant eventTime) {
        // TODO: implement state transition rules:
        // - Wrong: wrong_count++, is_active_wrong = true, correct_since_wrong = 0, last_wrong_at = eventTime
        // - Correct and active wrong: correct_since_wrong++, if reaches threshold -> is_active_wrong = false, correct_count++
        // - Correct and not active wrong: correct_count++
    }
}
