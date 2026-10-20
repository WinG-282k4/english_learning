import { ApiClient } from './apiClient';
import { Topic, Word } from '../types/vocab';

export class TopicApi {
  static async getByLesson(lessonId: number): Promise<Topic[]> {
    return ApiClient.get<Topic[]>(`/topics?lessonId=${lessonId}`);
  }

  static async getById(id: number): Promise<Topic> {
    return ApiClient.get<Topic>(`/topics/${id}`);
  }

  static async create(lessonId: number, title: string, orderIndex?: number): Promise<Topic> {
    return ApiClient.post<Topic>('/topics', { lessonId, title, orderIndex });
  }

  static async update(id: number, lessonId: number, title: string, orderIndex?: number): Promise<Topic> {
    return ApiClient.put<Topic>(`/topics/${id}`, { lessonId, title, orderIndex });
  }

  static async delete(id: number): Promise<void> {
    return ApiClient.delete(`/topics/${id}`);
  }

  static async generateWordWithAi(topicId: number, term: string): Promise<Word> {
    return ApiClient.post<Word>(`/topics/${topicId}/ai-generate`, { term });
  }
}
