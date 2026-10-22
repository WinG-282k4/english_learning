import { ApiClient } from './apiClient';
import { Topic, Word } from '../types/vocab';

export interface LessonWordSearchResult { topics: Topic[]; words: Word[] }

export class WordApi {
  static async createMany(topicId: number, words: Array<Pick<Word, 'term' | 'meaningVi' | 'pos' | 'ipa' | 'example'>>): Promise<Word[]> {
    return ApiClient.post<Word[]>('/words/batch', { topicId, words });
  }

  static async markLearned(wordId: number): Promise<Word> {
    return ApiClient.patch<Word>(`/words/${wordId}/learned`);
  }

  static async getByTopic(topicId: number): Promise<Word[]> {
    return ApiClient.get<Word[]>(`/words?topicId=${topicId}`);
  }

  static async getByLesson(lessonId: number): Promise<Word[]> {
    return ApiClient.get<Word[]>(`/words/by-lesson/${lessonId}`);
  }

  static async searchByLesson(lessonId: number, query: string): Promise<LessonWordSearchResult> {
    return ApiClient.get<LessonWordSearchResult>(`/words/search?lessonId=${lessonId}&q=${encodeURIComponent(query)}`);
  }

  static async searchByTopic(topicId: number, query: string): Promise<Word[]> {
    return ApiClient.get<Word[]>(`/words/search/by-topic/${topicId}?q=${encodeURIComponent(query)}`);
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
