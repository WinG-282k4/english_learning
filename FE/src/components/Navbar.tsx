import React from 'react';
import { Link } from 'react-router-dom';

export const Navbar: React.FC = () => {
  return (
    <nav className="navbar">
      <Link to="/" className="nav-logo">Vocab AI</Link>
      <div className="nav-links">
        <Link to="/">Bài học</Link>
        <Link to="/stats">Thống kê từ sai</Link>
        <Link to="/settings">Cài đặt</Link>
      </div>
    </nav>
  );
};
