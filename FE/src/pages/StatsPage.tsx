import React, { useEffect, useMemo, useState } from 'react';
import { StatApi } from '../services/statApi';
import { WordStat } from '../types/vocab';

type StatusFilter = 'ACTIVE' | 'RESOLVED' | 'ALL';

export const StatsPage: React.FC = () => {
  const [stats, setStats] = useState<WordStat[]>([]);
  const [filter, setFilter] = useState<StatusFilter>('ACTIVE');
  const [search, setSearch] = useState('');
  const [limit, setLimit] = useState(100);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = async (maxItems = limit) => {
    setLoading(true);
    setError('');
    try { setStats(await StatApi.getFrequentlyWrongWords(maxItems)); }
    catch (cause) { setError(cause instanceof Error ? cause.message : 'Không tải được thống kê.'); }
    finally { setLoading(false); }
  };
  useEffect(() => { void load(limit); }, [limit]);

  const normalizedSearch = normalize(search);
  const visibleStats = useMemo(() => stats.filter(stat => {
    const matchesStatus = filter === 'ALL' || (filter === 'ACTIVE' ? stat.isActiveWrong : !stat.isActiveWrong);
    return matchesStatus && (!normalizedSearch || normalize(stat.termNorm).includes(normalizedSearch));
  }), [stats, filter, normalizedSearch]);
  const activeCount = stats.filter(stat => stat.isActiveWrong).length;
  const resolvedCount = stats.length - activeCount;
  const totalWrong = stats.reduce((sum, stat) => sum + stat.wrongCount, 0);

  return <div className="page-container stats-page">
    <header className="page-header"><div><p className="eyebrow">TIẾN ĐỘ HỌC</p><h1>Thống kê từ sai</h1><p className="muted">Theo dõi từ cần ôn và tiến độ trả lời đúng.</p></div><button className="button" onClick={() => void load()} disabled={loading}>{loading ? 'Đang tải…' : '↻ Làm mới'}</button></header>

    <section className="stats-overview" aria-label="Tổng quan thống kê">
      <article className="stats-overview-card"><span>Đang cần ôn</span><strong>{loading ? '—' : activeCount}</strong></article>
      <article className="stats-overview-card"><span>Đã cải thiện</span><strong>{loading ? '—' : resolvedCount}</strong></article>
      <article className="stats-overview-card"><span>Tổng lượt trả lời sai</span><strong>{loading ? '—' : totalWrong}</strong></article>
    </section>

    <section className="stats-toolbar" aria-label="Lọc từ sai">
      <div className="stats-filters" role="tablist" aria-label="Trạng thái từ">
        <button role="tab" aria-selected={filter === 'ACTIVE'} className={filter === 'ACTIVE' ? 'active' : ''} onClick={() => setFilter('ACTIVE')}>Cần ôn ({activeCount})</button>
        <button role="tab" aria-selected={filter === 'RESOLVED'} className={filter === 'RESOLVED' ? 'active' : ''} onClick={() => setFilter('RESOLVED')}>Đã cải thiện ({resolvedCount})</button>
        <button role="tab" aria-selected={filter === 'ALL'} className={filter === 'ALL' ? 'active' : ''} onClick={() => setFilter('ALL')}>Tất cả ({stats.length})</button>
      </div>
      <label className="stats-search"><span aria-hidden="true">⌕</span><input value={search} onChange={event => setSearch(event.target.value)} placeholder="Tìm từ..." aria-label="Tìm từ trong thống kê" />{search && <button type="button" onClick={() => setSearch('')} aria-label="Xóa tìm kiếm">×</button>}</label>
      <label className="stats-limit">Hiển thị<select value={limit} onChange={event => setLimit(Number(event.target.value))}><option value={50}>50</option><option value={100}>100</option><option value={200}>200</option></select></label>
    </section>

    {error && <p className="notice error" role="alert">{error}</p>}
    {loading ? <section className="empty-state"><p>Đang tải thống kê từ sai…</p></section> : visibleStats.length === 0 ? <section className="empty-state"><h2>{stats.length === 0 ? 'Chưa có dữ liệu thống kê' : 'Không tìm thấy từ phù hợp'}</h2><p>{stats.length === 0 ? 'Thống kê sẽ xuất hiện sau khi hoàn thành bài kiểm tra.' : 'Thử đổi trạng thái hoặc từ khóa tìm kiếm.'}</p></section> : <>
      <p className="stats-result-count">Hiển thị {visibleStats.length} / {stats.length} từ đã có lượt sai{stats.length === limit ? ` · Đang tải tối đa ${limit} từ` : ''}</p>
      <div className="stats-table-wrap"><table className="stats-table"><thead><tr><th>Từ</th><th>Lượt sai</th><th>Lượt đúng</th><th>Tiến độ gỡ khỏi danh sách sai</th><th>Lần sai gần nhất</th><th>Trạng thái</th></tr></thead><tbody>
        {visibleStats.map(stat => <tr key={stat.termNorm}>
          <td className="stats-term">{stat.termNorm}</td>
          <td>{stat.wrongCount}</td>
          <td>{stat.correctCount}</td>
          <td>{stat.isActiveWrong ? <div className="stats-streak"><span>{stat.correctSinceWrong} / 3 câu đúng liên tiếp</span><div><i style={{ width: `${Math.min(stat.correctSinceWrong / 3 * 100, 100)}%` }} /></div></div> : <span className="muted">Đã đạt ngưỡng</span>}</td>
          <td>{stat.lastWrongAt ? new Date(stat.lastWrongAt).toLocaleString() : '—'}</td>
          <td><span className={`stats-status ${stat.isActiveWrong ? 'needs-review' : 'resolved'}`}>{stat.isActiveWrong ? 'Cần ôn' : 'Đã cải thiện'}</span></td>
        </tr>)}
      </tbody></table></div>
    </>}
  </div>;
};

function normalize(value: string): string {
  return value.toLocaleLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/đ/g, 'd').trim();
}
