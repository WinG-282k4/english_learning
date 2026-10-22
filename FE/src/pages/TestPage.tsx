import React, { FormEvent, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { AnswerGrader } from '../grader/grader';
import { TextNormalizer } from '../grader/normalizer';
import { GradeResult } from '../grader/types';
import { LessonApi } from '../services/lessonApi';
import { TestApi } from '../services/testApi';
import { TopicApi } from '../services/topicApi';
import { PoolWord, RoundResult, ScopeType, TestPoolResponse, TestResultSubmitItem } from '../types/test';

type SavedAnswers = Record<string, { r1?: RoundResult; r2?: RoundResult }>;

export const TestPage: React.FC = () => {
  const { type, id } = useParams<{ type: string; id: string }>();
  const navigate = useNavigate();
  const scopeId = Number(id);
  const [pool, setPool] = useState<TestPoolResponse | null>(null);
  const [title, setTitle] = useState('');
  const [round, setRound] = useState<1 | 2>(1);
  const [index, setIndex] = useState(0);
  const [answer, setAnswer] = useState('');
  const [feedback, setFeedback] = useState<GradeResult | null>(null);
  const [answers, setAnswers] = useState<SavedAnswers>({});
  const [loading, setLoading] = useState(true);
  const [grading, setGrading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    const load = async () => {
      try {
        if (!Number.isInteger(scopeId) || scopeId < 1 || (type !== 'lesson' && type !== 'topic')) throw new Error('Đường dẫn bài kiểm tra không hợp lệ.');
        const loadedPool = type === 'lesson' ? await TestApi.getLessonTestPool(scopeId) : await TestApi.getTopicTestPool(scopeId);
        const scopeTitle = type === 'lesson' ? (await LessonApi.getById(scopeId)).title : (await TopicApi.getById(scopeId)).title;
        if (active) { setPool({ ...loadedPool, words: shuffleWords(loadedPool.words) }); setTitle(scopeTitle); }
      } catch (cause) { if (active) setError(errorMessage(cause)); }
      finally { if (active) setLoading(false); }
    };
    void load();
    return () => { active = false; };
  }, [scopeId, type]);

  const currentWord = pool?.words[index];
  const scopeType: ScopeType = type === 'lesson' ? 'LESSON' : 'TOPIC';
  const backHref = type === 'lesson' ? `/lessons/${id}` : `/topics/${id}`;

  const submitAnswer = async (event: FormEvent) => {
    event.preventDefault();
    if (!currentWord || !answer.trim() || grading) return;
    setGrading(true);
    setError('');
    const grader = AnswerGrader.createDefaultPipeline();
    try {
      const result = await grader.grade({
        round,
        targetEnglish: currentWord.term,
        correctMeanings: TextNormalizer.parseMeanings(currentWord.meaningVi),
        userAnswer: answer,
        enableFuzzy: round === 2,
        enableAi: round === 1,
      });
      setFeedback(result);
      setAnswers(previous => ({
        ...previous,
        [currentWord.termNorm]: { ...previous[currentWord.termNorm], [`r${round}`]: { correct: result.correct, gradedBy: result.gradedBy, answer: answer.trim(), note: result.note } },
      }));
    } catch (cause) { setError(errorMessage(cause)); }
    finally { setGrading(false); }
  };

  const markCorrect = () => {
    if (!currentWord || !feedback) return;
    const override: GradeResult = { correct: true, gradedBy: 'USER_OVERRIDE', note: 'Đã ghi nhận theo xác nhận của bạn.' };
    setFeedback(override);
    setAnswers(previous => ({
      ...previous,
      [currentWord.termNorm]: { ...previous[currentWord.termNorm], [`r${round}`]: { correct: true, gradedBy: 'USER_OVERRIDE', answer: answer.trim(), note: override.note } },
    }));
  };

  const continueTest = async () => {
    if (!pool || saving) return;
    if (index < pool.words.length - 1) {
      setIndex(index + 1); setAnswer(''); setFeedback(null); setError('');
      return;
    }
    if (round === 1) {
      setRound(2); setIndex(0); setAnswer(''); setFeedback(null); setError('');
      return;
    }
    const results: TestResultSubmitItem[] = pool.words.map(word => {
      const wordAnswers = answers[word.termNorm];
      if (!wordAnswers?.r1 || !wordAnswers.r2) throw new Error(`Thiếu kết quả cho từ: ${word.term}`);
      return { wordId: word.wordId, term: word.term, source: word.source, r1: wordAnswers.r1, r2: wordAnswers.r2 };
    });
    setSaving(true);
    try {
      const summary = await TestApi.submitTest({ scope: { type: scopeType, id: scopeId }, results });
      navigate(`/test/summary/${summary.sessionId}`);
    } catch (cause) { setError(errorMessage(cause)); }
    finally { setSaving(false); }
  };

  const prompt = currentWord && round === 1 ? currentWord.term : currentWord?.meaningVi;
  const expected = currentWord && round === 1 ? currentWord.meaningVi : currentWord?.term;

  return <div className="page-container test-page">
    <Link className="back-link" to={backHref}>← Quay lại {type === 'lesson' ? 'bài học' : 'chủ đề'}</Link>
    <header className="page-header"><div><p className="eyebrow">CHẾ ĐỘ KIỂM TRA · LƯỢT {round}</p><h1>{title || 'Bài kiểm tra'}</h1><p className="muted">{round === 1 ? 'Anh → Việt' : 'Việt → Anh'}</p></div>
      {pool && pool.words.length > 0 && <div className="test-counter">Từ {index + 1} / {pool.words.length}</div>}
    </header>
    {error && <p className="notice error" role="alert">{error}</p>}
    {loading ? <section className="empty-state"><p>Đang tạo bộ từ kiểm tra…</p></section> : !error && pool?.words.length === 0 ? <section className="empty-state"><h2>Chưa có từ để kiểm tra</h2><p>Thêm từ vào {type === 'lesson' ? 'bài học' : 'chủ đề'} trước khi bắt đầu.</p><Link className="button primary" to={backHref}>Quay lại</Link></section> : currentWord && <>
      <div className="test-progress"><span style={{ width: `${((index + 1) / pool!.words.length) * 100}%` }} /></div>
      <section className="test-question-card">
        <p className="test-instruction">{round === 1 ? 'Dịch từ tiếng Anh sang tiếng Việt' : 'Viết từ tiếng Anh tương ứng'}</p>
        <h2>{prompt}</h2>
        {round === 1 && <>{currentWord.ipa && <p className="ipa">/{currentWord.ipa.replace(/^\/(.*)\/$/, '$1')}/</p>}{currentWord.pos && <span className="word-tag">{currentWord.pos}</span>}</>}
        <small>Nguồn từ: {sourceLabel(currentWord.source)}</small>
      </section>
      {!feedback ? <form className="test-answer-form" onSubmit={event => void submitAnswer(event)}>
        <label htmlFor="test-answer">Câu trả lời</label>
        <input id="test-answer" autoFocus value={answer} onChange={event => setAnswer(event.target.value)} placeholder={round === 1 ? 'Nhập nghĩa tiếng Việt…' : 'Nhập từ tiếng Anh…'} disabled={grading} />
        <button className="button primary" disabled={grading || !answer.trim()}>{grading ? 'Đang chấm…' : 'Trả lời'}</button>
      </form> : <section className={`test-feedback ${feedback.correct ? 'correct' : 'incorrect'}`} aria-live="polite">
        <h2>{feedback.correct ? 'Chính xác!' : 'Chưa chính xác'}</h2>
        <p>Câu trả lời của bạn: <strong>{answer}</strong></p>
        {!feedback.correct && <p>Đáp án: <strong>{expected}</strong></p>}
        {feedback.note && <p className="muted">{feedback.note}</p>}
        <p className="test-grader-label">Chấm bởi: {graderLabel(feedback.gradedBy)}</p>
        {!feedback.correct && <button className="button" type="button" onClick={markCorrect}>Tôi đúng</button>}
        <button className="button primary" type="button" onClick={() => void continueTest()} disabled={saving}>{saving ? 'Đang lưu kết quả…' : nextLabel(index, pool!.words.length, round)}</button>
      </section>}
    </>}
  </div>;
};

function sourceLabel(source: PoolWord['source']): string {
  return source === 'CURRENT' ? 'Chủ đề/bài hiện tại' : source === 'PREV_TOPIC' ? 'Chủ đề liền trước' : 'Ôn lại từ sai';
}
function graderLabel(gradedBy: GradeResult['gradedBy']): string {
  return ({ EXACT: 'khớp chính xác', ACCENT: 'bỏ qua dấu tiếng Việt', FUZZY: 'chấp nhận lỗi chính tả nhỏ', AI: 'AI', USER_OVERRIDE: 'xác nhận của người học' })[gradedBy];
}
function nextLabel(index: number, total: number, round: 1 | 2): string {
  if (index < total - 1) return 'Câu tiếp theo →';
  return round === 1 ? 'Bắt đầu lượt 2 →' : 'Xem kết quả';
}
function shuffleWords<T>(words: T[]): T[] {
  const shuffled = [...words];
  for (let index = shuffled.length - 1; index > 0; index--) {
    const swapIndex = Math.floor(Math.random() * (index + 1));
    [shuffled[index], shuffled[swapIndex]] = [shuffled[swapIndex], shuffled[index]];
  }
  return shuffled;
}
function errorMessage(cause: unknown): string { return cause instanceof Error ? cause.message : 'Đã xảy ra lỗi. Vui lòng thử lại.'; }
