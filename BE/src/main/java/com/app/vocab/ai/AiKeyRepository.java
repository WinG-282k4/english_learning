package com.app.vocab.ai;

import com.app.vocab.llm.LlmProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AiKeyRepository extends JpaRepository<AiKey, Long> {
    Optional<AiKey> findByProvider(LlmProviderType provider);
    Optional<AiKey> findByIsDefaultTrue();
}
