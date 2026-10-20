import { ApiClient } from './apiClient';
import { AiKey } from '../types/setting';

export class KeyApi {
  static async getAll(): Promise<AiKey[]> {
    return ApiClient.get<AiKey[]>('/keys');
  }

  static async save(provider: string, rawApiKey: string, isDefault: boolean): Promise<AiKey> {
    return ApiClient.post<AiKey>('/keys', { provider, rawApiKey, isDefault });
  }

  static async update(id: number, provider: string, rawApiKey: string, isDefault: boolean): Promise<AiKey> {
    return ApiClient.put<AiKey>(`/keys/${id}`, { provider, rawApiKey, isDefault });
  }

  static async delete(id: number): Promise<void> {
    return ApiClient.delete(`/keys/${id}`);
  }
}
