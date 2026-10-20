package com.app.vocab.topic;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public ResponseEntity<List<TopicDtos.View>> getTopicsByLesson(@RequestParam Long lessonId) {
        return ResponseEntity.ok(topicService.getTopicsByLesson(lessonId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TopicDtos.View> getTopicById(@PathVariable Long id) {
        return ResponseEntity.ok(topicService.getTopicById(id));
    }

    @PostMapping
    public ResponseEntity<TopicDtos.View> createTopic(@Valid @RequestBody TopicDtos.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(topicService.createTopic(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TopicDtos.View> updateTopic(@PathVariable Long id, @Valid @RequestBody TopicDtos.Create request) {
        return ResponseEntity.ok(topicService.updateTopic(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }
}
