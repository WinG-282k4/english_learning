export type ScopeType = 'TOPIC' | 'LESSON';

export type WordSourceType = 'CURRENT' | 'PREV_TOPIC' | 'RANDOM_PAST';

export type GradedBy = 'EXACT' | 'ACCENT' | 'FUZZY' | 'AI' | 'USER_OVERRIDE';

export type AiGradeVerdict = 'correct' | 'typo' | 'wrong';

export interface PoolWord {
  wordId?: number;
  term: string;
  termNorm: string;
  pos?: string;
  ipa?: string;
  meaningVi: string;
  example?: string;
  source: WordSourceType;
}

export interface TestPoolResponse {
  scope: ScopeType;
  scopeId: number;
  totalWords: number;
  words: PoolWord[];
}

export interface RoundResult {
  correct: boolean;
  gradedBy: GradedBy;
  answer: string;
  note?: string;
}

export interface TestResultSubmitItem {
  wordId?: number;
  term: string;
  source: WordSourceType;
  r1: RoundResult;
  r2: RoundResult;
}

export interface TestSubmitRequest {
  scope: {
    type: ScopeType;
    id: number;
  };
  results: TestResultSubmitItem[];
}

export interface TestSubmitResponse {
  sessionId: number;
  score: number;
  totalWords: number;
  correctWords: number;
  wrongWords: number;
  createdAt: string;
}

export interface TestSummaryResponse extends TestSubmitResponse {
  scope: ScopeType;
  scopeId: number;
  results: TestResultSubmitItem[];
}
