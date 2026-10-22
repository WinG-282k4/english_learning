package com.app.vocab.stat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface WordStatRepository extends JpaRepository<WordStat, String> {

    List<WordStat> findByIsActiveWrongTrue();

    @Query("SELECT ws FROM WordStat ws WHERE ws.isActiveWrong = true ORDER BY RAND()")
    List<WordStat> findRandomActiveWrongWords(Pageable pageable);

    @Query("SELECT ws FROM WordStat ws WHERE ws.isActiveWrong = true AND ws.termNorm NOT IN :excludedTerms ORDER BY RAND()")
    List<WordStat> findRandomActiveWrongWords(@Param("excludedTerms") Collection<String> excludedTerms, Pageable pageable);

    @Query("SELECT ws FROM WordStat ws WHERE ws.wrongCount > 0 ORDER BY ws.wrongCount DESC, ws.lastWrongAt DESC")
    List<WordStat> findTopWrongWords(Pageable pageable);
}
