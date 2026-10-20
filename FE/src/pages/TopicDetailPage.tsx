import React from 'react';
import { useParams } from 'react-router-dom';

/**
 * Page: Chi tiết chủ đề (Topic Detail)
 * Danh sách từ vựng, thêm từ (thêm quá 12 tự tách topic), sửa/xóa từ, nút Học / Kiểm tra
 */
export const TopicDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();

  // TODO: implement word list, add/edit/delete word, trigger AI generate, start Study / Test
  return (
    <div className="page-container">
      <h1>Chi tiết chủ đề #{id}</h1>
    </div>
  );
};
