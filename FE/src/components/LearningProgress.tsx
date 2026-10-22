import React from 'react';

interface LearningProgressProps {
  learned: number;
  total: number;
  latestTestScore?: number | null;
  latestTestAt?: string | null;
}

export const LearningProgress: React.FC<LearningProgressProps> = ({ learned, total, latestTestScore, latestTestAt }) => {
  const percent = total ? Math.round((learned / total) * 100) : 0;
  const lastTest = latestTestScore == null
    ? 'Chưa kiểm tra'
    : `Lần kiểm tra gần nhất: ${latestTestScore}%${latestTestAt ? ` · ${new Date(latestTestAt).toLocaleDateString('vi-VN')}` : ''}`;

  return <div className="learning-progress">
    <div className="learning-progress-copy"><span>Đã học {learned}/{total} từ</span><span>{lastTest}</span></div>
    <div className="learning-progress-track" role="progressbar" aria-valuenow={percent} aria-valuemin={0} aria-valuemax={100}>
      <span style={{ width: `${percent}%` }} />
    </div>
  </div>;
};
