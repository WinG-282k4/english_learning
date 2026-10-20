package com.app.vocab.word;

import com.app.vocab.topic.Topic;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "words", indexes = {
    @Index(name = "idx_words_term_norm", columnList = "term_norm"),
    @Index(name = "idx_words_topic_order", columnList = "topic_id, order_index")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false)
    private String term;

    @Column(name = "term_norm", nullable = false)
    private String termNorm;

    @Column(length = 50)
    private String pos;

    private String ipa;

    @Lob
    @Column(name = "meaning_vi", nullable = false)
    private String meaningVi;

    @Lob
    private String example;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
}
