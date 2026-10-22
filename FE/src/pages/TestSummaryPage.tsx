import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { TestApi } from '../services/testApi';
import { TestSummaryResponse } from '../types/test';

export const TestSummaryPage: React.FC = () => {
  const { sessionId } = useParams<{ sessionId: string }>();
  const [summary, setSummary] = useState<TestSummaryResponse | null>(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    let active = true;
    void TestApi.getSummary(Number(sessionId))
      .then(result => { if (active) setSummary(result); })
      .catch(cause => { if (active) setError(cause instanceof Error ? cause.message : 'Không tải được kết quả.'); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [sessionId]);

  const wrongResults = summary?.results.filter(result => !result.r1.correct || !result.r2.correct) ?? [];
  const backHref = summary ? summary.scope === 'LESSON' ? `/lessons/${summary.scopeId}` : `/topics/${summary.scopeId}` : '/';
  return <div className="page-container test-summary-page">
    <Link className="back-link" to={backHref}>← Quay lại</Link>
    <header className="page-header"><div><p className="eyebrow">KẾT QUẢ KIỂM TRA</p><h1>Tổng kết</h1><p className="muted">{summary ? new Date(summary.createdAt).toLocaleString() : 'Xem lại kết quả lượt kiểm tra vừa rồi.'}</p></div></header>
    {loading && <section className="empty-state"><p>Đang tải kết quả…</p></section>}
    {error && <p className="notice error" role="alert">{error}</p>}
    {summary && <>
      <section className="summary-score-card"><div className="summary-score">{summary.score}<span>%</span></div><div><h2>{summary.correctWords} / {summary.totalWords} từ đúng cả hai lượt</h2><p>{summary.wrongWords} từ cần ôn lại</p></div></section>
      {wrongResults.length > 0 ? <section className="lesson-detail-section"><h2 className="lesson-section-title">Từ cần ôn ({wrongResults.length})</h2><div className="word-list">{wrongResults.map(result => <article className="word-row summary-word-row" key={result.wordId ?? result.term}><div className="word-content"><strong>{result.term}</strong><p>Lượt 1: {result.r1.correct ? 'Đúng' : 'Sai'}{result.r1.answer ? ` · Bạn trả lời: ${result.r1.answer}` : ''}</p><p>Lượt 2: {result.r2.correct ? 'Đúng' : 'Sai'}{result.r2.answer ? ` · Bạn trả lời: ${result.r2.answer}` : ''}</p></div><span className="word-tag">{sourceLabel(result.source)}</span></article>)}</div></section> : <section className="empty-state"><h2>Hoàn thành tốt!</h2><p>Tất cả từ đều đúng cả hai lượt.</p></section>}
      <div className="summary-actions"><Link className="button" to={backHref}>Về bài học</Link><Link className="button primary" to={`/test/${summary.scope === 'LESSON' ? 'lesson' : 'topic'}/${summary.scopeId}`}>Làm lại</Link></div>
    </>}
  </div>;
};

function sourceLabel(source: string): string { return source === 'CURRENT' ? 'Hiện tại' : source === 'PREV_TOPIC' ? 'Topic trước' : 'Ôn từ sai'; }
