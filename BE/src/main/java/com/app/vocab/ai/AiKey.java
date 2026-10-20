package com.app.vocab.ai;

import com.app.vocab.llm.LlmProviderType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ai_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LlmProviderType provider;

    @Column(name = "encrypted_key", nullable = false, length = 1000)
    private String encryptedKey;

    @Builder.Default
    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;
}
