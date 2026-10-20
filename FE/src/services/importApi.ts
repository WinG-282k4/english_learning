import { ApiClient } from './apiClient';
import { ImportJob, PreviewTopic } from '../types/import';

export class ImportApi {
  static async uploadFile(lessonId: number, file: File): Promise<{ jobId: string }> {
    const formData = new FormData();
    formData.append('file', file);
    const res = await fetch(`/api/lessons/${lessonId}/import`, {
      method: 'POST',
      body: formData,
    });
    if (!res.ok) throw new Error(`Upload failed with status: ${res.status}`);
    return res.json();
  }

  static async getJobStatus(jobId: string): Promise<ImportJob> {
    return ApiClient.get<ImportJob>(`/jobs/${jobId}`);
  }

  static async commitJob(jobId: string, topics: PreviewTopic[]): Promise<void> {
    await ApiClient.post<void>(`/jobs/${jobId}/commit`, { topics });
  }
}
