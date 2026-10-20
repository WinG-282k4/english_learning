import { ApiClient } from './apiClient';
import { AiGradeVerdict } from '../types/test';

export interface AiGradeResponse {
  verdict: AiGradeVerdict;
  note?: string;
}

export class GradeApi {
  static async gradeWithAi(english: string, correctMeanings: string[], userAnswer: string): Promise<AiGradeResponse> {
    return ApiClient.post<AiGradeResponse>('/grade/ai', {
      english,
      correct_meanings: correctMeanings,
      user_answer: userAnswer,
    });
  }
}
