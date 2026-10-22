import React, { useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { LessonApi } from '../services/lessonApi';
import { TopicApi } from '../services/topicApi';
import { Lesson, Topic, Word } from '../types/vocab';
import { parseLessonJson } from '../utils/lessonJson';
import { WordApi } from '../services/wordApi';

export const LessonDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>(); const lessonId = Number(id);
  const [lesson, setLesson] = useState<Lesson | null>(null); const [topics, setTopics] = useState<Topic[]>([]);
  const [words, setWords] = useState<Word[]>([]);
  const [title, setTitle] = useState(''); const [editing, setEditing] = useState<number | null>(null); const [busy, setBusy] = useState(false); const [importBusy, setImportBusy] = useState(false); const [error, setError] = useState('');
  const load = useCallback(async () => { try { const [l, t, w] = await Promise.all([LessonApi.getById(lessonId), TopicApi.getByLesson(lessonId), WordApi.getByLesson(lessonId)]); setLesson(l); setTopics(t); setWords(w); setError(''); } catch (e) { setError(message(e)); } }, [lessonId]);
  useEffect(() => { void load(); }, [load]);
  const save = async (e: React.FormEvent) => { e.preventDefault(); if (!title.trim()) return; setBusy(true); setError(''); try { if (editing === null) await TopicApi.create(lessonId, title.trim()); else { const topic = topics.find(t => t.id === editing); if (topic) await TopicApi.update(editing, lessonId, title.trim(), topic.orderIndex); } setTitle(''); setEditing(null); await load(); } catch (e) { setError(message(e)); } finally { setBusy(false); } };
  const remove = async (topic: Topic) => { if (!window.confirm(`Xóa chủ đề “${topic.title}” cùng ${topic.wordCount} từ bên trong?`)) return; try { await TopicApi.delete(topic.id); await load(); } catch (e) { setError(message(e)); } };
  const importJson = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]; event.target.value = ''; if (!file) return;
    setImportBusy(true); setError('');
    try {
      if (file.size > 2 * 1024 * 1024) throw new Error('File JSON không được vượt quá 2 MB.');
      const parsed = parseLessonJson(await file.text());
      await LessonApi.appendJson(lessonId, { topics: parsed.topics });
      await load();
    } catch (cause) { setError(message(cause)); }
    finally { setImportBusy(false); }
  };
  return <div className="page-container">
    <Link className="back-link" to="/">← Tất cả bài học</Link>
    <header className="page-header"><div><p className="eyebrow">BÀI HỌC</p><h1>{lesson?.title ?? `Bài học #${id}`}</h1><p className="muted">{topics.length} chủ đề · {lesson?.totalWords ?? topics.reduce((sum, t) => sum + t.wordCount, 0)} từ</p></div><div className="header-actions"><Link className="button" to={`/study/lesson/${id}`}>Học tất cả</Link><Link className="button primary" to={`/test/lesson/${id}`}>Kiểm tra</Link></div></header>
    <label className="button import-link" htmlFor="lesson-json-upload">{importBusy ? 'Đang nhập JSON…' : '↑ Nhập chủ đề từ JSON'}<input id="lesson-json-upload" type="file" accept=".json,application/json" onChange={event => void importJson(event)} disabled={importBusy} hidden /></label>
    <form className="editor-card inline-form" onSubmit={save}><label htmlFor="topic-title">{editing === null ? 'Thêm chủ đề' : 'Đổi tên chủ đề'}</label><div className="form-row"><input id="topic-title" value={title} onChange={e => setTitle(e.target.value)} placeholder="Ví dụ: Du lịch" maxLength={120} required/><button className="button primary" disabled={busy}>{busy ? 'Đang lưu…' : editing === null ? 'Thêm chủ đề' : 'Lưu thay đổi'}</button>{editing !== null && <button type="button" className="button" onClick={() => {setEditing(null);setTitle('');}}>Hủy</button>}</div></form>
    {error && <p className="notice error" role="alert">{error}</p>}
    <section className="lesson-detail-section"><h2 className="lesson-section-title">Chủ đề</h2>
      {!topics.length ? <section className="empty-state"><h2>Chưa có chủ đề</h2><p>Thêm chủ đề hoặc nhập chủ đề từ file JSON.</p></section> : <div className="topic-list">{topics.map((topic, index) => <article className="topic-row" key={topic.id}><Link className="topic-info" to={`/topics/${topic.id}`}><span className="topic-number">{String(index + 1).padStart(2, '0')}</span><span><strong>{topic.title}</strong><small>{topic.wordCount} từ</small></span></Link><div className="card-actions"><Link className="button subtle" to={`/study/topic/${topic.id}`}>Học</Link><Link className="button subtle" to={`/test/topic/${topic.id}`}>Kiểm tra</Link><button className="button subtle" onClick={() => {setEditing(topic.id);setTitle(topic.title);}}>Sửa</button><button className="button danger" onClick={() => void remove(topic)}>Xóa</button></div></article>)}</div>}
    </section>
    <section className="lesson-detail-section"><h2 className="lesson-section-title">Từ vựng trong bài ({words.length})</h2>
      {!words.length ? <p className="muted">Bài học chưa có từ vựng.</p> : <div className="word-list">{words.map((word, index) => <article className="word-row" key={word.id}><span className="word-number">{String(index + 1).padStart(2, '0')}</span><div className="word-content"><small className="word-topic-label">{topics.find(topic => topic.id === word.topicId)?.title ?? 'Chủ đề'}</small><div className="word-title"><strong>{word.term}</strong>{word.pos && <span className="word-tag">{word.pos}</span>}{word.ipa && <span className="ipa">{word.ipa}</span>}</div><p>{word.meaningVi}</p>{word.example && <small>{word.example}</small>}</div><Link className="button subtle" to={`/topics/${word.topicId}`}>Mở topic</Link></article>)}</div>}
    </section>
  </div>;
};
function message(error: unknown) { return error instanceof Error ? error.message : 'Đã xảy ra lỗi. Vui lòng thử lại.'; }
