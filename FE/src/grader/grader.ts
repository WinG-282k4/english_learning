import { GradeApi } from '../services/gradeApi';
import { TextNormalizer } from './normalizer';
import { LevenshteinCalculator } from './levenshtein';
import { GradingContext, GradeResult, GradingHandler } from './types';

export abstract class AbstractGradingStep implements GradingHandler {
  protected nextHandler: GradingHandler | null = null;
  setNext(handler: GradingHandler): GradingHandler { this.nextHandler = handler; return handler; }
  abstract handle(context: GradingContext): Promise<GradeResult | null>;
}

export class ExactGradingStep extends AbstractGradingStep {
  async handle(context: GradingContext): Promise<GradeResult | null> {
    const answer = TextNormalizer.normalize(context.userAnswer);
    const expected = context.round === 1 ? context.correctMeanings : [TextNormalizer.normalize(context.targetEnglish)];
    if (expected.includes(answer)) return { correct: true, gradedBy: 'EXACT' };
    return this.nextHandler ? this.nextHandler.handle(context) : null;
  }
}

export class AccentGradingStep extends AbstractGradingStep {
  async handle(context: GradingContext): Promise<GradeResult | null> {
    if (context.round === 1) {
      const answer = TextNormalizer.normalize(TextNormalizer.removeAccents(context.userAnswer));
      if (context.correctMeanings.some(meaning => TextNormalizer.normalize(TextNormalizer.removeAccents(meaning)) === answer)) {
        return { correct: true, gradedBy: 'ACCENT', note: 'Đáp án đúng, không yêu cầu dấu tiếng Việt.' };
      }
    }
    return this.nextHandler ? this.nextHandler.handle(context) : null;
  }
}

export class FuzzyGradingStep extends AbstractGradingStep {
  async handle(context: GradingContext): Promise<GradeResult | null> {
    if (context.round === 2 && context.enableFuzzy !== false &&
        LevenshteinCalculator.isMatch(TextNormalizer.normalize(context.targetEnglish), TextNormalizer.normalize(context.userAnswer))) {
      return { correct: true, gradedBy: 'FUZZY', note: 'Được chấp nhận với một lỗi chính tả nhỏ.' };
    }
    return this.nextHandler ? this.nextHandler.handle(context) : null;
  }
}

export class AiGradingStep extends AbstractGradingStep {
  async handle(context: GradingContext): Promise<GradeResult | null> {
    if (context.round !== 1 || !context.enableAi || !context.userAnswer.trim()) {
      return this.nextHandler ? this.nextHandler.handle(context) : null;
    }
    try {
      const result = await GradeApi.gradeWithAi(context.targetEnglish, context.correctMeanings, context.userAnswer);
      return { correct: result.verdict === 'correct' || result.verdict === 'typo', gradedBy: 'AI', note: result.note };
    } catch {
      return { correct: false, gradedBy: 'EXACT', note: 'Không kết nối được AI; đáp án được chấm theo nghĩa đã cho.' };
    }
  }
}

export class AnswerGrader {
  private constructor(private readonly chain: GradingHandler) {}
  static createDefaultPipeline(): AnswerGrader {
    const exact = new ExactGradingStep();
    const accent = new AccentGradingStep();
    const fuzzy = new FuzzyGradingStep();
    const ai = new AiGradingStep();
    exact.setNext(accent).setNext(fuzzy).setNext(ai);
    return new AnswerGrader(exact);
  }
  async grade(context: GradingContext): Promise<GradeResult> {
    return await this.chain.handle(context) ?? { correct: false, gradedBy: 'EXACT' };
  }
}
