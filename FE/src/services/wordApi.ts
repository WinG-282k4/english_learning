import { ApiClient } from './apiClient';
import { Word } from '../types/vocab';

export class WordApi {
  static async createMany(topicId: number, words: Array<Pick<Word, 'term' | 'meaningVi' | 'pos' | 'ipa' | 'example'>>): Promise<Word[]> {
    return ApiClient.post<Word[]>('/words/batch', { topicId, words });
  }

  static async getByTopic(topicId: number): Promise<Word[]> {
    return ApiClient.get<Word[]>(`/words?topicId=${topicId}`);
  }

  static async getByLesson(lessonId: number): Promise<Word[]> {
    return ApiClient.get<Word[]>(`/words/by-lesson/${lessonId}`);
  }

  static async create(word: Partial<Word>): Promise<Word> {
    return ApiClient.post<Word>('/words', word);
  }

  static async update(id: number, word: Partial<Word>): Promise<Word> {
    return ApiClient.put<Word>(`/words/${id}`, word);
  }

  static async delete(id: number): Promise<void> {
    return ApiClient.delete(`/words/${id}`);
  }
}
