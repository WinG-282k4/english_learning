import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { LessonApi } from '../services/lessonApi';
import { LessonImportPayload, LessonImportTopic, LessonImportWord } from '../types/vocab';
import { parseLessonJson } from '../utils/lessonJson';

const emptyWord = (): LessonImportWord => ({ term: '', pos: '', ipa: '', meaningVi: '', example: '' });
const emptyTopic = (): LessonImportTopic => ({ title: '', words: [emptyWord()] });

export const CreateLessonPage: React.FC = () => {
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [topics, setTopics] = useState<LessonImportTopic[]>([emptyTopic()]);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  const updateTopic = (topicIndex: number, changes: Partial<LessonImportTopic>) => {
    setTopics(current => current.map((topic, index) => index === topicIndex ? { ...topic, ...changes } : topic));
  };
  const updateWord = (topicIndex: number, wordIndex: number, changes: Partial<LessonImportWord>) => {
    setTopics(current => current.map((topic, index) => index !== topicIndex ? topic : {
      ...topic,
      words: topic.words.map((word, itemIndex) => itemIndex === wordIndex ? { ...word, ...changes } : word),
    }));
  };
  const importJson = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    event.target.value = '';
    if (!file) return;
    setError('');
    if (file.size > 2 * 1024 * 1024) { setError('File JSON không được vượt quá 2 MB.'); return; }
    try {
      const parsed = parseLessonJson(await file.text());
      setTitle(parsed.title ?? '');
      setTopics(parsed.topics);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Không đọc được file JSON.');
    }
  };

  const save = async (event: React.FormEvent) => {
    event.preventDefault();
    setError('');
    if (!title.trim()) { setError('Vui lòng nhập tên bài học.'); return; }
    if (topics.some(topic => !topic.title.trim())) { setError('Vui lòng nhập tên cho tất cả topic.'); return; }
    if (topics.some(topic => topic.words.length === 0 || topic.words.length > 12)) { setError('Mỗi topic cần có từ 1 đến 12 từ.'); return; }
    if (topics.some(topic => topic.words.some(word => !word.term.trim() || !word.meaningVi.trim()))) { setError('Mỗi từ cần có từ tiếng Anh và nghĩa tiếng Việt.'); return; }
    setBusy(true);
    try {
      const lesson = await LessonApi.createWithContent({ title: title.trim(), topics: topics.map(topic => ({
        title: topic.title.trim(),
        words: topic.words.map(word => ({ ...word, term: word.term.trim(), meaningVi: word.meaningVi.trim() })),
      })) });
      navigate(`/lessons/${lesson.id}`);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'Không tạo được bài học.');
    } finally { setBusy(false); }
  };

  return <div className="page-container">
    <Link className="back-link" to="/">← Tất cả bài học</Link>
    <header className="page-header"><div><p className="eyebrow">TẠO NỘI DUNG</p><h1>Thêm bài học</h1><p className="muted">Nhập nội dung bên dưới hoặc nạp file JSON để điền form.</p></div><label className="button" htmlFor="lesson-json-file">{busy ? 'Đang lưu…' : 'Nạp từ JSON'}<input id="lesson-json-file" type="file" accept=".json,application/json" onChange={event => void importJson(event)} disabled={busy} hidden /></label></header>
    {error && <p className="notice error" role="alert">{error}</p>}
    <form onSubmit={save}>
      <section className="editor-card inline-form"><label htmlFor="new-lesson-title">Tên bài học</label><input id="new-lesson-title" value={title} onChange={event => setTitle(event.target.value)} maxLength={120} placeholder="Ví dụ: Tiếng Anh cơ bản" required /></section>
      {topics.map((topic, topicIndex) => <section className="editor-card create-topic-card" key={topicIndex}>
        <div className="editor-heading"><div><h2>Topic {topicIndex + 1}</h2><p className="muted">Thêm từ vựng trực tiếp vào topic này.</p></div><button type="button" className="button danger" onClick={() => setTopics(current => current.filter((_, index) => index !== topicIndex))} disabled={topics.length === 1}>Xóa topic</button></div>
        <label className="create-field">Tên topic<input value={topic.title} onChange={event => updateTopic(topicIndex, { title: event.target.value })} maxLength={120} placeholder="Ví dụ: Động vật" required /></label>
        {topic.words.map((word, wordIndex) => <div className="create-word-card" key={wordIndex}>
          <div className="editor-heading"><strong>Từ {wordIndex + 1}</strong><button type="button" className="button subtle" onClick={() => updateTopic(topicIndex, { words: topic.words.filter((_, index) => index !== wordIndex) })} disabled={topic.words.length === 1}>Xóa từ</button></div>
          <div className="word-form-grid">
            <label>Từ tiếng Anh *<input value={word.term} onChange={event => updateWord(topicIndex, wordIndex, { term: event.target.value })} placeholder="cat" required /></label>
            <label>Từ loại<input value={word.pos ?? ''} onChange={event => updateWord(topicIndex, wordIndex, { pos: event.target.value })} placeholder="noun" /></label>
            <label>Phiên âm IPA<input value={word.ipa ?? ''} onChange={event => updateWord(topicIndex, wordIndex, { ipa: event.target.value })} placeholder="/kæt/" /></label>
            <label>Nghĩa tiếng Việt *<input value={word.meaningVi} onChange={event => updateWord(topicIndex, wordIndex, { meaningVi: event.target.value })} placeholder="con mèo" required /></label>
            <label className="wide-field">Câu ví dụ<input value={word.example ?? ''} onChange={event => updateWord(topicIndex, wordIndex, { example: event.target.value })} placeholder="The cat is sleeping." /></label>
          </div>
        </div>)}
        <button type="button" className="button" onClick={() => updateTopic(topicIndex, { words: [...topic.words, emptyWord()] })} disabled={topic.words.length >= 12}>+ Thêm từ ({topic.words.length}/12)</button>
      </section>)}
      <div className="create-actions"><button type="button" className="button" onClick={() => setTopics(current => [...current, emptyTopic()])}>+ Thêm topic</button><button type="submit" className="button primary" disabled={busy}>{busy ? 'Đang tạo…' : 'Tạo bài học'}</button></div>
    </form>
  </div>;
};
