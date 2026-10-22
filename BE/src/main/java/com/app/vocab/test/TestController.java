package com.app.vocab.test;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/topics/{id}/test-pool")
    public ResponseEntity<TestDtos.PoolResponse> getTopicTestPool(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getTopicTestPool(id));
    }

    @GetMapping("/lessons/{id}/test-pool")
    public ResponseEntity<TestDtos.PoolResponse> getLessonTestPool(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getLessonTestPool(id));
    }

    @PostMapping("/tests/submit")
    public ResponseEntity<TestDtos.SubmitResponse> submitTest(@Valid @RequestBody TestDtos.SubmitRequest request) {
        return ResponseEntity.ok(testService.submitTest(request));
    }

    @GetMapping("/tests/{id}")
    public ResponseEntity<TestDtos.SummaryResponse> getTestSummary(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getTestSummary(id));
    }
}
