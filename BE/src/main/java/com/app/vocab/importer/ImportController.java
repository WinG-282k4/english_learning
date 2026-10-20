package com.app.vocab.importer;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/lessons/{id}/import")
    public ResponseEntity<Map<String, String>> uploadAndImport(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        String jobId = importService.startImportJob(id, file);
        return ResponseEntity.accepted().body(Map.of("jobId", jobId));
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<ImportDtos.JobStatus> getJobStatus(@PathVariable String id) {
        return ResponseEntity.ok(importService.getJobStatus(id));
    }

    @PostMapping("/jobs/{id}/commit")
    public ResponseEntity<Void> commitImport(
            @PathVariable String id,
            @Valid @RequestBody ImportDtos.CommitRequest request) {
        importService.commitImportJob(id, request);
        return ResponseEntity.ok().build();
    }
}
