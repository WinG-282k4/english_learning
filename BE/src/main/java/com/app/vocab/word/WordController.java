package com.app.vocab.word;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/words")
public class WordController {

    private final WordService wordService;

    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping
    public ResponseEntity<List<WordDtos.View>> getWordsByTopic(@RequestParam Long topicId) {
        return ResponseEntity.ok(wordService.getWordsByTopic(topicId));
    }

    @GetMapping("/by-lesson/{lessonId}")
    public ResponseEntity<List<WordDtos.View>> getWordsByLesson(@PathVariable Long lessonId) {
        return ResponseEntity.ok(wordService.getWordsByLesson(lessonId));
    }

    @GetMapping("/search")
    public ResponseEntity<WordDtos.LessonSearchResult> searchLesson(
            @RequestParam Long lessonId,
            @RequestParam String q) {
        return ResponseEntity.ok(wordService.searchLesson(lessonId, q));
    }

    @GetMapping("/search/by-topic/{topicId}")
    public ResponseEntity<List<WordDtos.View>> searchTopic(
            @PathVariable Long topicId,
            @RequestParam String q) {
        return ResponseEntity.ok(wordService.searchTopic(topicId, q));
    }

    @PostMapping
    public ResponseEntity<WordDtos.View> createWord(@Valid @RequestBody WordDtos.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wordService.createWord(request));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<WordDtos.View>> createWords(@Valid @RequestBody WordDtos.BatchCreate request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wordService.createWords(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WordDtos.View> updateWord(@PathVariable Long id, @Valid @RequestBody WordDtos.Create request) {
        return ResponseEntity.ok(wordService.updateWord(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        wordService.deleteWord(id);
        return ResponseEntity.noContent().build();
    }
}
