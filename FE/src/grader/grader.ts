import { GradingContext, GradeResult, GradingHandler } from './types';

/**
 * Base abstract class for grading chain handlers.
 */
export abstract class AbstractGradingStep implements GradingHandler {
  protected nextHandler: GradingHandler | null = null;

  setNext(handler: GradingHandler): GradingHandler {
    this.nextHandler = handler;
    return handler;
  }

  abstract handle(context: GradingContext): Promise<GradeResult | null>;
}

export class ExactGradingStep extends AbstractGradingStep {
  async handle(context: GradingContext): Promise<GradeResult | null> {
    // TODO: implement exact match check after normalization
    return this.nextHandler ? this.nextHandler.handle(context) : null;
  }
}

export class AccentGradingStep extends AbstractGradingStep {
  async handle(context: GradingContext): Promise<GradeResult | null> {
    // TODO: implement accent removal comparison (Round 1 only)
    return this.nextHandler ? this.nextHandler.handle(context) : null;
  }
}

export class FuzzyGradingStep extends AbstractGradingStep {
  async handle(context: GradingContext): Promise<GradeResult | null> {
    // TODO: implement Levenshtein <= 1 check (Round 2 only)
    return this.nextHandler ? this.nextHandler.handle(context) : null;
  }
}

export class AiGradingStep extends AbstractGradingStep {
  async handle(context: GradingContext): Promise<GradeResult | null> {
    // TODO: call backend POST /grade/ai when local check fails and AI grading is enabled
    return this.nextHandler ? this.nextHandler.handle(context) : null;
  }
}

/**
 * Orchestrator for the grading pipeline.
 */
export class AnswerGrader {
  private chain: GradingHandler;

  constructor(chain: GradingHandler) {
    this.chain = chain;
  }

  static createDefaultPipeline(): AnswerGrader {
    const exact = new ExactGradingStep();
    const accent = new AccentGradingStep();
    const fuzzy = new FuzzyGradingStep();
    const ai = new AiGradingStep();

    exact.setNext(accent).setNext(fuzzy).setNext(ai);
    return new AnswerGrader(exact);
  }

  async grade(context: GradingContext): Promise<GradeResult> {
    // TODO: run through chain and return result or default WRONG
    return {
      correct: false,
      gradedBy: 'EXACT',
    };
  }
}
