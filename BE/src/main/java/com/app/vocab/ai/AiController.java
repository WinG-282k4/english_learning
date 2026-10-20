package com.app.vocab.ai;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AiController {

    private final KeyService keyService;
    private final GradeService gradeService;

    public AiController(KeyService keyService, GradeService gradeService) {
        this.keyService = keyService;
        this.gradeService = gradeService;
    }

    // --- Key Management ---
    @GetMapping("/keys")
    public ResponseEntity<List<AiDtos.KeyView>> getAllKeys() {
        return ResponseEntity.ok(keyService.getAllKeys());
    }

    @PostMapping("/keys")
    public ResponseEntity<AiDtos.KeyView> saveKey(@Valid @RequestBody AiDtos.SaveKeyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(keyService.saveKey(request));
    }

    @PutMapping("/keys/{id}")
    public ResponseEntity<AiDtos.KeyView> updateKey(@PathVariable Long id, @Valid @RequestBody AiDtos.SaveKeyRequest request) {
        return ResponseEntity.ok(keyService.updateKey(id, request));
    }

    @DeleteMapping("/keys/{id}")
    public ResponseEntity<Void> deleteKey(@PathVariable Long id) {
        keyService.deleteKey(id);
        return ResponseEntity.noContent().build();
    }

    // --- AI Grading ---
    @PostMapping("/grade/ai")
    public ResponseEntity<AiDtos.GradeResponse> gradeWithAi(@Valid @RequestBody AiDtos.GradeRequest request) {
        return ResponseEntity.ok(gradeService.gradeWithAi(request));
    }
}
