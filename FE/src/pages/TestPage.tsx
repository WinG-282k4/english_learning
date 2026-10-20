import React from 'react';

/**
 * Page: Chế độ Kiểm tra (2 lượt: EN->VI và VI->EN)
 * State bài kiểm tra lưu trong RAM, chấm local tức thì qua grader.ts,
 * gọi POST /grade/ai khi cần, nút "Tôi đúng" để sửa trong RAM, submit cuối lượt 2
 */
export const TestPage: React.FC = () => {
  // TODO: implement 2-round test flow, local grader integration, AI grading fallback, instant feedback
  return (
    <div className="page-container">
      <h1>Chế độ Kiểm tra</h1>
    </div>
  );
};
