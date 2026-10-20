import React from 'react';
import { useParams } from 'react-router-dom';

/**
 * Page: Chi tiết bài học (Lesson Detail)
 * Danh sách topic + nút "Tất cả từ" để học/kiểm tra toàn bộ bài
 */
export const LessonDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();

  // TODO: implement topic listing, add topic, study all words, test all words
  return (
    <div className="page-container">
      <h1>Chi tiết bài học #{id}</h1>
    </div>
  );
};
