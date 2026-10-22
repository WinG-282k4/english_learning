package com.app.vocab.test;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestSessionRepository extends JpaRepository<TestSession, Long> {
    List<TestSession> findByScopeAndScopeIdOrderByCreatedAtDesc(ScopeType scope, Long scopeId);
    Optional<TestSession> findFirstByScopeAndScopeIdOrderByCreatedAtDesc(ScopeType scope, Long scopeId);
    List<TestSession> findAllByOrderByCreatedAtDesc();
}
