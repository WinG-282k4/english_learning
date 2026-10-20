package com.app.vocab.stat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatController {

    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @GetMapping("/wrong-words")
    public ResponseEntity<List<StatDtos.WrongWordView>> getFrequentlyWrongWords(
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(statService.getFrequentlyWrongWords(limit));
    }
}
