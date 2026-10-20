export interface Lesson {
  id: number;
  title: string;
  createdAt: string;
  topicCount: number;
  totalWords: number;
}

export interface Topic {
  id: number;
  lessonId: number;
  title: string;
  orderIndex: number;
  wordCount: number;
  words?: Word[];
}

export interface Word {
  id: number;
  topicId: number;
  term: string;
  termNorm: string;
  pos?: string;
  ipa?: string;
  meaningVi: string;
  example?: string;
  orderIndex: number;
}

export interface WordStat {
  termNorm: string;
  wrongCount: number;
  correctCount: number;
  correctSinceWrong: number;
  isActiveWrong: boolean;
  lastWrongAt?: string;
}
