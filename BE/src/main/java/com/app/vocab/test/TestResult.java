package com.app.vocab.test;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private TestSession session;

    @Column(name = "word_id")
    private Long wordId;

    @Column(name = "term_norm", nullable = false)
    private String termNorm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private WordSourceType source;

    @Column(name = "r1_correct")
    private Boolean r1Correct;

    @Lob
    @Column(name = "r1_answer")
    private String r1Answer;

    @Enumerated(EnumType.STRING)
    @Column(name = "r1_graded_by", length = 50)
    private GradedBy r1GradedBy;

    @Column(name = "r2_correct")
    private Boolean r2Correct;

    @Lob
    @Column(name = "r2_answer")
    private String r2Answer;

    @Enumerated(EnumType.STRING)
    @Column(name = "r2_graded_by", length = 50)
    private GradedBy r2GradedBy;
}
