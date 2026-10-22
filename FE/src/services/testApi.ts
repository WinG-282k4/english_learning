import { ApiClient } from './apiClient';
import { TestPoolResponse, TestSubmitRequest, TestSubmitResponse, TestSummaryResponse } from '../types/test';

export class TestApi {
  static async getTopicTestPool(topicId: number): Promise<TestPoolResponse> {
    return ApiClient.get<TestPoolResponse>(`/topics/${topicId}/test-pool`);
  }

  static async getLessonTestPool(lessonId: number): Promise<TestPoolResponse> {
    return ApiClient.get<TestPoolResponse>(`/lessons/${lessonId}/test-pool`);
  }

  static async submitTest(request: TestSubmitRequest): Promise<TestSubmitResponse> {
    return ApiClient.post<TestSubmitResponse>('/tests/submit', request);
  }

  static async getSummary(sessionId: number): Promise<TestSummaryResponse> {
    return ApiClient.get<TestSummaryResponse>(`/tests/${sessionId}`);
  }
}
