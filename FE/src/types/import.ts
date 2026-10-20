export interface PreviewWord {
  term: string;
  termNorm: string;
  pos?: string;
  ipa?: string;
  meaningVi: string;
  example?: string;
}

export interface PreviewTopic {
  title: string;
  orderIndex: number;
  words: PreviewWord[];
}

export interface ImportJob {
  jobId: string;
  lessonId: number;
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';
  progressPercentage: number;
  message?: string;
  previewTopics: PreviewTopic[];
}
