import { GradedBy } from '../types/test';

export interface GradingContext {
  round: 1 | 2; // 1: EN -> VI, 2: VI -> EN
  targetEnglish: string;
  correctMeanings: string[]; // parsed from meaningVi split by ; or ,
  userAnswer: string;
  enableFuzzy?: boolean;
  enableAi?: boolean;
}

export interface GradeResult {
  correct: boolean;
  gradedBy: GradedBy;
  note?: string;
  matchedMeaning?: string;
}

export interface GradingHandler {
  setNext(handler: GradingHandler): GradingHandler;
  handle(context: GradingContext): Promise<GradeResult | null>;
}
