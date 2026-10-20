import { ApiClient } from './apiClient';
import { Lesson } from '../types/vocab';

export class LessonApi {
  static async getAll(): Promise<Lesson[]> {
    return ApiClient.get<Lesson[]>('/lessons');
  }

  static async getById(id: number): Promise<Lesson> {
    return ApiClient.get<Lesson>(`/lessons/${id}`);
  }

  static async create(title: string): Promise<Lesson> {
    return ApiClient.post<Lesson>('/lessons', { title });
  }

  static async update(id: number, title: string): Promise<Lesson> {
    return ApiClient.put<Lesson>(`/lessons/${id}`, { title });
  }

  static async delete(id: number): Promise<void> {
    return ApiClient.delete(`/lessons/${id}`);
  }
}
