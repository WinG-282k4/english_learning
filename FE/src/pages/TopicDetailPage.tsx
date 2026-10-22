import React, { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { TopicApi } from '../services/topicApi';
import { WordApi } from '../services/wordApi';
import { Topic, Word } from '../types/vocab';

type WordForm = { term: string; pos: string; ipa: string; meaningVi: string; example: string };
const blank = (): WordForm => ({ term: '', pos: '', ipa: '', meaningVi: '', example: '' });

export const TopicDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const topicId = Number(id);
  const navigate = useNavigate();
  const [topic, setTopic] = useState<Topic | null>(null);
  const [words, setWords] = useState<Word[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState<Word[] | null>(null);
  const [searchBusy, setSearchBusy] = useState(false);
  const [editing, setEditing] = useState<number | null>(null);
  const [editForm, setEditForm] = useState<WordForm>(blank());
  const [addFormOpen, setAddFormOpen] = useState(false);
  const [newWords, setNewWords] = useState<WordForm[]>([]);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    try {
      const [loadedTopic, loadedWords] = await Promise.all([TopicApi.getById(topicId), WordApi.getByTopic(topicId)]);
      setTopic(loadedTopic);
      setWords(loadedWords);
      setError('');
    } catch (cause) { setError(message(cause)); }
  }, [topicId]);
  useEffect(() => { void load(); }, [load]);
  const query = searchTerm.trim();
  useEffect(() => {
    if (!query) { setSearchResults(null); setSearchBusy(false); return; }
    let active = true;
    setSearchBusy(true);
    const timer = window.setTimeout(() => {
      void WordApi.searchByTopic(topicId, query)
        .then(result => { if (active) { setSearchResults(result); setError(''); } })
        .catch(cause => { if (active) setError(message(cause)); })
        .finally(() => { if (active) setSearchBusy(false); });
    }, 300);
    return () => { active = false; window.clearTimeout(timer); };
  }, [topicId, query]);
  const displayedWords = query ? searchResults ?? [] : words;

  const startAdding = () => {
    setEditing(null);
    setAddFormOpen(true);
    setNewWords([blank()]);
    setError('');
  };
  const saveNewWords = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!newWords.length || newWords.some(word => !word.term.trim() || !word.meaningVi.trim())) {
      setError('Mỗi từ cần có từ tiếng Anh và nghĩa tiếng Việt.');
      return;
    }
    setBusy(true);
    setError('');
    try {
      await WordApi.createMany(topicId, newWords.map(word => ({
        term: word.term.trim(), pos: word.pos.trim(), ipa: word.ipa.trim(),
        meaningVi: word.meaningVi.trim(), example: word.example.trim(),
      })));
      setNewWords([]);
      setAddFormOpen(false);
      await load();
    } catch (cause) { setError(message(cause)); }
    finally { setBusy(false); }
  };
  const saveEdit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (editing === null || !editForm.term.trim() || !editForm.meaningVi.trim()) return;
    setBusy(true);
    setError('');
    const existing = words.find(word => word.id === editing);
    try {
      const saved = await WordApi.update(editing, { topicId, ...editForm, orderIndex: existing?.orderIndex });
      setEditing(null);
      setEditForm(blank());
      if (saved.topicId !== topicId) { navigate(`/topics/${saved.topicId}`, { replace: true }); return; }
      await load();
    } catch (cause) { setError(message(cause)); }
    finally { setBusy(false); }
  };
  const edit = (word: Word) => {
    setAddFormOpen(false);
    setEditing(word.id);
    setEditForm({ term: word.term, pos: word.pos ?? '', ipa: word.ipa ?? '', meaningVi: word.meaningVi, example: word.example ?? '' });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };
  const remove = async (word: Word) => {
    if (!window.confirm(`Xóa từ “${word.term}”?`)) return;
    try { await WordApi.delete(word.id); await load(); }
    catch (cause) { setError(message(cause)); }
  };
  const updateNewWord = (index: number, changes: Partial<WordForm>) => {
    setNewWords(current => current.map((word, itemIndex) => itemIndex === index ? { ...word, ...changes } : word));
  };

  return <div className="page-container">
    <Link className="back-link" to={topic ? `/lessons/${topic.lessonId}` : '/'}>← {topic ? 'Quay lại bài học' : 'Tất cả bài học'}</Link>
    <header className="page-header">
      <div><p className="eyebrow">CHỦ ĐỀ</p><h1>{topic?.title ?? `Chủ đề #${id}`}</h1><p className="muted">{words.length} tu vung</p></div>
      <div className="header-actions"><Link className="button" to={`/study/topic/${id}`}>Học</Link><Link className="button" to={`/test/topic/${id}`}>Kiểm tra</Link></div>
    </header>

    {editing !== null && <form className="editor-card" onSubmit={saveEdit}>
      <div className="editor-heading"><div><h2>Sửa từ vựng</h2><p className="muted">Cập nhật thông tin từ này.</p></div><button type="button" className="button" onClick={() => { setEditing(null); setEditForm(blank()); }}>Hủy</button></div>
      <div className="word-form-grid">
        <label>Từ tiếng Anh *<input value={editForm.term} onChange={event => setEditForm({ ...editForm, term: event.target.value })} maxLength={150} required /></label>
        <label>Từ loại<input value={editForm.pos} onChange={event => setEditForm({ ...editForm, pos: event.target.value })} maxLength={50} /></label>
        <label>Phiên âm IPA<input value={editForm.ipa} onChange={event => setEditForm({ ...editForm, ipa: event.target.value })} maxLength={100} /></label>
        <label>Nghĩa tiếng Việt *<input value={editForm.meaningVi} onChange={event => setEditForm({ ...editForm, meaningVi: event.target.value })} required /></label>
        <label className="wide-field">Ví dụ<input value={editForm.example} onChange={event => setEditForm({ ...editForm, example: event.target.value })} /></label>
      </div>
      <div className="form-footer"><span className="muted">Đang sửa từ đã có</span><button className="button primary" disabled={busy}>{busy ? 'Đang lưu…' : 'Lưu từ'}</button></div>
    </form>}



    {error && <p className="notice error" role="alert">{error}</p>}
    <div className="lesson-search topic-search"><span aria-hidden="true">⌕</span><input aria-label="Tìm từ trong chủ đề" value={searchTerm} onChange={event => setSearchTerm(event.target.value)} placeholder="Tìm từ, nghĩa hoặc ví dụ trong chủ đề..." />{searchTerm && <button type="button" onClick={() => setSearchTerm('')} aria-label="Xóa nội dung tìm kiếm">×</button>}</div>
    {!words.length && !query ? <section className="empty-state"><h2>Chủ đề chưa có từ</h2><p>Nhấn “Thêm từ” để mở form nhập một hoặc nhiều từ.</p></section> : searchBusy ? <p className="muted">Đang tìm kiếm…</p> : !displayedWords.length ? <p className="muted">Không tìm thấy từ phù hợp.</p> : <div className="word-list">{displayedWords.map((word, index) => <article className="word-row" key={word.id}><span className="word-number">{String(index + 1).padStart(2, '0')}</span><div className="word-content"><div className="word-title"><strong>{word.term}</strong>{word.pos && <span className="word-tag">{word.pos}</span>}{word.ipa && <span className="ipa">/{word.ipa.replace(/^\/(.*)\/$/, '$1')}/</span>}</div><p>{word.meaningVi}</p>{word.example && <small>{word.example}</small>}</div><div className="card-actions"><button className="button subtle" onClick={() => edit(word)}>Sửa</button><button className="button danger" onClick={() => void remove(word)}>Xóa</button></div></article>)}</div>}

    <div className="topic-add-actions"><button className="button primary" onClick={startAdding} disabled={busy}>+ Thêm từ</button></div>
    {addFormOpen && <form className="editor-card" onSubmit={saveNewWords}>
      <div className="editor-heading"><div><h2>Thêm nhiều từ</h2><p className="muted">Nhap cac tu can them roi luu cung luc.</p></div><button type="button" className="button" onClick={() => { setAddFormOpen(false); setNewWords([]); }}>Đóng</button></div>
      {newWords.map((word, index) => <section className="batch-word-card" key={index}>
        <div className="editor-heading"><strong>Từ {index + 1}</strong><button type="button" className="button subtle" onClick={() => setNewWords(current => current.filter((_, itemIndex) => itemIndex !== index))}>Xóa</button></div>
        <div className="word-form-grid">
          <label>Từ tiếng Anh *<input value={word.term} onChange={event => updateNewWord(index, { term: event.target.value })} maxLength={150} required /></label>
          <label>Từ loại<input value={word.pos} onChange={event => updateNewWord(index, { pos: event.target.value })} maxLength={50} /></label>
          <label>Phiên âm IPA<input value={word.ipa} onChange={event => updateNewWord(index, { ipa: event.target.value })} maxLength={100} /></label>
          <label>Nghĩa tiếng Việt *<input value={word.meaningVi} onChange={event => updateNewWord(index, { meaningVi: event.target.value })} required /></label>
          <label className="wide-field">Ví dụ<input value={word.example} onChange={event => updateNewWord(index, { example: event.target.value })} /></label>
        </div>
      </section>)}
      <div className="form-footer"><button type="button" className="button" onClick={() => setNewWords(current => [...current, blank()])} >+ Thêm dòng từ</button><button type="submit" className="button primary" disabled={busy || newWords.length === 0}>{busy ? 'Đang lưu…' : `Lưu ${newWords.length} từ`}</button></div>
    </form>}
  </div>;
};

function message(error: unknown) { return error instanceof Error ? error.message : 'Đã xảy ra lỗi. Vui lòng thử lại.'; }
