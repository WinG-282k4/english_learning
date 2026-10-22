package com.app.vocab.lesson;

import jakarta.validation.Valid;
import com.app.vocab.importer.LessonJsonImportService;
import com.app.vocab.importer.LessonJsonImportDtos;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final LessonJsonImportService jsonImportService;

    public LessonController(LessonService lessonService, LessonJsonImportService jsonImportService) {
        this.lessonService = lessonService;
        this.jsonImportService = jsonImportService;
    }

    @GetMapping
    public ResponseEntity<List<LessonDtos.View>> getAllLessons() {
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonDtos.View> getLessonById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }

    @PostMapping
    public ResponseEntity<LessonDtos.View> createLesson(@Valid @RequestBody LessonDtos.Create request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.createLesson(request));
    }

    @PostMapping("/with-content")
    public ResponseEntity<LessonDtos.View> createWithContent(@RequestBody LessonJsonImportDtos.LessonFile request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(jsonImportService.createNewLesson(request));
    }

    @PostMapping("/{id}/import-json")
    public ResponseEntity<LessonDtos.View> appendJson(@PathVariable Long id, @RequestBody LessonJsonImportDtos.LessonFile request) {
        return ResponseEntity.ok(jsonImportService.importIntoLesson(id, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessonDtos.View> updateLesson(@PathVariable Long id, @Valid @RequestBody LessonDtos.Create request) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
}
