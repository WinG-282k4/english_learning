import React, { useEffect, useState } from 'react';
import { LearningProgress } from '../components/LearningProgress';
import { Link } from 'react-router-dom';
import { LessonApi } from '../services/lessonApi';
import { Lesson } from '../types/vocab';

export const LessonListPage: React.FC = () => {
  const [lessons, setLessons] = useState<Lesson[]>([]);
  const [title, setTitle] = useState('');
  const [editing, setEditing] = useState<number | null>(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const load = async () => { try { setLessons(await LessonApi.getAll()); setError(''); } catch (e) { setError(message(e)); } };
  useEffect(() => { void load(); }, []);
  const save = async (e: React.FormEvent) => {
    e.preventDefault(); if (!title.trim()) return;
    setBusy(true); setError('');
    try { if (editing !== null) await LessonApi.update(editing, title.trim()); setTitle(''); setEditing(null); await load(); }
    catch (e) { setError(message(e)); } finally { setBusy(false); }
  };
  const remove = async (lesson: Lesson) => {
    if (!window.confirm(`Xóa bài học “${lesson.title}” cùng toàn bộ chủ đề và từ bên trong?`)) return;
    try { await LessonApi.delete(lesson.id); await load(); } catch (e) { setError(message(e)); }
  };
  return <div className="page-container">
    <header className="page-header"><div><p className="eyebrow">THƯ VIỆN CỦA BẠN</p><h1>Bài học</h1><p className="muted">Sắp xếp từ vựng theo bài học và chủ đề.</p></div><Link className="button primary" to="/lessons/new">+ Thêm bài học</Link></header>
    {editing !== null && <form className="editor-card inline-form" onSubmit={save}><label htmlFor="lesson-title">Đổi tên bài học</label><div className="form-row"><input id="lesson-title" value={title} onChange={e => setTitle(e.target.value)} maxLength={120} required/><button className="button primary" disabled={busy}>{busy ? 'Đang lưu…' : 'Lưu thay đổi'}</button><button type="button" className="button" onClick={() => {setEditing(null);setTitle('');}}>Hủy</button></div></form>}
    {error && <p className="notice error" role="alert">{error}</p>}
    {lessons.length === 0 ? <section className="empty-state"><h2>Chưa có bài học</h2><p>Tạo bài học đầu tiên để bắt đầu thêm chủ đề và từ vựng.</p></section> : <div className="card-grid">{lessons.map(lesson => <article className="lesson-card" key={lesson.id}><Link className="card-main" to={`/lessons/${lesson.id}`}><span className="card-icon">▤</span><h2>{lesson.title}</h2><p>{lesson.topicCount} chủ đề <span>·</span> {lesson.totalWords} từ</p><LearningProgress learned={lesson.learnedWords} total={lesson.totalWords} latestTestScore={lesson.latestTestScore} latestTestAt={lesson.latestTestAt} /></Link><div className="card-actions"><button className="button subtle" onClick={() => {setEditing(lesson.id);setTitle(lesson.title);window.scrollTo({top:0,behavior:'smooth'});}}>Sửa</button><button className="button danger" onClick={() => void remove(lesson)}>Xóa</button></div></article>)}</div>}
  </div>;
};

function message(error: unknown) { return error instanceof Error ? error.message : 'Đã xảy ra lỗi. Vui lòng thử lại.'; }
