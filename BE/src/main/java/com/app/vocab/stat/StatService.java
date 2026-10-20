package com.app.vocab.stat;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatService {

    private final WordStatRepository wordStatRepository;

    public StatService(WordStatRepository wordStatRepository) {
        this.wordStatRepository = wordStatRepository;
    }

    public List<StatDtos.WrongWordView> getFrequentlyWrongWords(int limit) {
        // TODO: query word_stats where wrongCount > 0 ordered by wrongCount desc
        return null;
    }
}
