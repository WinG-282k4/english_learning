package com.app.vocab.importer;

import com.app.vocab.ai.KeyService;
import com.app.vocab.lesson.LessonRepository;
import com.app.vocab.llm.LlmProvider;
import com.app.vocab.topic.TopicRepository;
import com.app.vocab.word.WordRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ImportService {

    private final LessonRepository lessonRepository;
    private final TopicRepository topicRepository;
    private final WordRepository wordRepository;
    private final Map<String, LlmProvider> llmProviders;
    private final KeyService keyService;
    private final Map<String, ImportDtos.JobStatus> jobCache = new ConcurrentHashMap<>();

    public ImportService(LessonRepository lessonRepository,
                         TopicRepository topicRepository,
                         WordRepository wordRepository,
                         Map<String, LlmProvider> llmProviders,
                         KeyService keyService) {
        this.lessonRepository = lessonRepository;
        this.topicRepository = topicRepository;
        this.wordRepository = wordRepository;
        this.llmProviders = llmProviders;
        this.keyService = keyService;
    }

    public String startImportJob(Long lessonId, MultipartFile file) {
        // TODO: generate jobId, initialize status, trigger async execution, return jobId
        return null;
    }

    @Async("importTaskExecutor")
    public void processImportPipeline(String jobId, Long lessonId, byte[] fileBytes) {
        // TODO: Pipeline:
        // 1. Apache POI (XWPF) extract text from .docx bytes
        // 2. Chunk text to fit context window
        // 3. Build LlmRequest with extract_vocab.txt prompt
        // 4. llmProvider.call() -> parse JSON via Jackson -> List<AiDtos.ExtractedItem>
        // 5. Bean Validation, retry on JSON error, timeout + size limit
        // 6. Dedupe by termNorm
        // 7. Call LLM to suggest topic names, split into groups of max 12
        // 8. Update jobCache with preview state
    }

    public ImportDtos.JobStatus getJobStatus(String jobId) {
        // TODO: retrieve job progress and preview data from jobCache
        return null;
    }

    public void commitImportJob(String jobId, ImportDtos.CommitRequest request) {
        // TODO: save validated and user-reviewed topics and words into database
    }
}
