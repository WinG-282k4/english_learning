package com.app.vocab.stat;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "word_stats", indexes = {
    @Index(name = "idx_word_stats_active_wrong", columnList = "is_active_wrong")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordStat {

    @Id
    @Column(name = "term_norm", nullable = false)
    private String termNorm;

    @Builder.Default
    @Column(name = "wrong_count", nullable = false)
    private int wrongCount = 0;

    @Builder.Default
    @Column(name = "correct_count", nullable = false)
    private int correctCount = 0;

    @Builder.Default
    @Column(name = "correct_since_wrong", nullable = false)
    private int correctSinceWrong = 0;

    @Builder.Default
    @Column(name = "is_active_wrong", nullable = false)
    private boolean isActiveWrong = false;

    @Column(name = "last_wrong_at")
    private Instant lastWrongAt;
}
