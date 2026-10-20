import { ApiClient } from './apiClient';
import { WordStat } from '../types/vocab';

export class StatApi {
  static async getFrequentlyWrongWords(limit: number = 50): Promise<WordStat[]> {
    return ApiClient.get<WordStat[]>(`/stats/wrong-words?limit=${limit}`);
  }
}
