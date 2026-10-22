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
        return wordStatRepository.findTopWrongWords(PageRequest.of(0, limit))
                .stream()
                .map(ws -> new StatDtos.WrongWordView(
                        ws.getTermNorm(),
                        ws.getWrongCount(),
                        ws.getCorrectCount(),
                        ws.getCorrectSinceWrong(),
                        ws.isActiveWrong(),
                        ws.getLastWrongAt()
                ))
                .toList();
    }
}

