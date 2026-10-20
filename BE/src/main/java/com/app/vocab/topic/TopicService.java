package com.app.vocab.topic;

import com.app.vocab.config.AppProperties;
import com.app.vocab.lesson.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final LessonRepository lessonRepository;
    private final AppProperties appProperties;

    public TopicService(TopicRepository topicRepository,
                        LessonRepository lessonRepository,
                        AppProperties appProperties) {
        this.topicRepository = topicRepository;
        this.lessonRepository = lessonRepository;
        this.appProperties = appProperties;
    }

    public List<TopicDtos.View> getTopicsByLesson(Long lessonId) {
        // TODO: implement get topics by lesson
        return null;
    }

    public TopicDtos.View getTopicById(Long id) {
        // TODO: implement get topic by id
        return null;
    }

    public TopicDtos.View createTopic(TopicDtos.Create request) {
        // TODO: implement create topic
        return null;
    }

    public TopicDtos.View updateTopic(Long id, TopicDtos.Create request) {
        // TODO: implement update topic
        return null;
    }

    public void deleteTopic(Long id) {
        // TODO: implement delete topic
    }
}
