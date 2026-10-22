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
        if (!isCorrect) {
            stat.setWrongCount(stat.getWrongCount() + 1);
            stat.setActiveWrong(true);
            stat.setCorrectSinceWrong(0);
            stat.setLastWrongAt(eventTime);
            return;
        }

        stat.setCorrectCount(stat.getCorrectCount() + 1);
        if (stat.isActiveWrong()) {
            int correctSinceWrong = stat.getCorrectSinceWrong() + 1;
            stat.setCorrectSinceWrong(correctSinceWrong);
            if (correctSinceWrong >= Math.max(1, resolveThreshold)) {
                stat.setActiveWrong(false);
                stat.setCorrectSinceWrong(0);
            }
        }
    }
}
