import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Flashcard } from '../components/Flashcard';
import { LessonApi } from '../services/lessonApi';
import { TopicApi } from '../services/topicApi';
import { WordApi } from '../services/wordApi';
import { Lesson, Topic, Word } from '../types/vocab';

export const StudyFlashcardPage: React.FC = () => {
  const { type, id } = useParams<{ type: string; id: string }>();
  const scopeId = Number(id);
  const [title, setTitle] = useState('');
  const [words, setWords] = useState<Word[]>([]);
  const [index, setIndex] = useState(0);
  const [flipped, setFlipped] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError('');
    setIndex(0);
    setFlipped(false);
    const load = async () => {
      try {
        if (!Number.isInteger(scopeId) || scopeId < 1 || (type !== 'lesson' && type !== 'topic')) {
          throw new Error('Đường dẫn học không hợp lệ.');
        }
        if (type === 'lesson') {
          const [lesson, lessonWords] = await Promise.all([LessonApi.getById(scopeId), WordApi.getByLesson(scopeId)]);
          if (active) { setTitle((lesson as Lesson).title); setWords(lessonWords); }
        } else {
          const [topic, topicWords] = await Promise.all([TopicApi.getById(scopeId), WordApi.getByTopic(scopeId)]);
          if (active) { setTitle((topic as Topic).title); setWords(topicWords); }
        }
      } catch (cause) {
        if (active) setError(cause instanceof Error ? cause.message : 'Không thể tải từ vựng.');
      } finally { if (active) setLoading(false); }
    };
    void load();
    return () => { active = false; };
  }, [scopeId, type]);

  const word = words[index];
  const backHref = type === 'topic' ? `/topics/${id}` : `/lessons/${id}`;
  const move = (nextIndex: number) => {
    setIndex(nextIndex);
    setFlipped(false);
  };
  const flipCard = () => {
    if (!flipped && word && !word.learned) {
      void WordApi.markLearned(word.id)
        .then(updated => setWords(current => current.map(item => item.id === updated.id ? updated : item)))
        .catch(cause => setError(cause instanceof Error ? cause.message : 'Could not save learning progress.'));
    }
    setFlipped(value => !value);
  };

  return <div className="page-container study-page">
    <Link className="back-link" to={backHref}>← {type === 'topic' ? 'Quay lại chủ đề' : 'Quay lại bài học'}</Link>
    <header className="page-header study-header">
      <div><p className="eyebrow">CHẾ ĐỘ HỌC · {type === 'topic' ? 'CHỦ ĐỀ' : 'BÀI HỌC'}</p><h1>{title || (type === 'topic' ? `Chủ đề #${id}` : `Bài học #${id}`)}</h1><p className="muted">{loading ? 'Đang tải từ vựng…' : `${words.length} từ vựng`}</p></div>
      {!loading && words.length > 0 && <div className="study-progress-label">{index + 1}<span> / {words.length}</span></div>}
    </header>

    {error && <p className="notice error" role="alert">{error}</p>}
    {loading ? <section className="empty-state"><p>Đang tải thẻ học…</p></section> : !error && !words.length ? <section className="empty-state"><h2>Chưa có từ để học</h2><p>Thêm từ vào {type === 'topic' ? 'chủ đề' : 'bài học'} rồi quay lại đây.</p><Link className="button primary" to={backHref}>Quay lại</Link></section> : word && <>
      <div className="study-progress"><span style={{ width: `${((index + 1) / words.length) * 100}%` }} /></div>
      <p className="study-hint">Nhấn vào thẻ để xem nghĩa. Dùng nút phát âm để nghe cách đọc.</p>
      <Flashcard word={word} isFlipped={flipped} onFlip={flipCard} />
      <nav className="study-controls" aria-label="Điều khiển thẻ học">
        <button className="button" onClick={() => move(index - 1)} disabled={index === 0}>← Trước</button>
        <button className="button study-flip-button" onClick={flipCard}>{flipped ? 'Xem từ' : 'Lật thẻ'}</button>
        {index === words.length - 1 ? <Link className="button primary" to={backHref}>Hoàn thành</Link> : <button className="button primary" onClick={() => move(index + 1)}>Tiếp →</button>}
      </nav>
      {index === words.length - 1 && <p className="study-finished">Bạn đã đến thẻ cuối cùng. Có thể xem lại các thẻ bằng nút “Trước”.</p>}
    </>}
  </div>;
};
