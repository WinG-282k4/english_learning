package com.app.vocab.word;

import com.app.vocab.config.AppProperties;
import com.app.vocab.topic.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WordService {

    private final WordRepository wordRepository;
    private final TopicRepository topicRepository;
    private final AppProperties appProperties;

    public WordService(WordRepository wordRepository,
                       TopicRepository topicRepository,
                       AppProperties appProperties) {
        this.wordRepository = wordRepository;
        this.topicRepository = topicRepository;
        this.appProperties = appProperties;
    }

    public List<WordDtos.View> getWordsByTopic(Long topicId) {
        // TODO: implement get words by topic
        return null;
    }

    public List<WordDtos.View> getWordsByLesson(Long lessonId) {
        // TODO: implement get all words by lesson (composite query)
        return null;
    }

    public WordDtos.View createWord(WordDtos.Create request) {
        // TODO: implement create word; if topic has reached 12 words, auto create next topic
        return null;
    }

    public WordDtos.View updateWord(Long id, WordDtos.Create request) {
        // TODO: implement update word
        return null;
    }

    public void deleteWord(Long id) {
        // TODO: implement delete word
    }
}
