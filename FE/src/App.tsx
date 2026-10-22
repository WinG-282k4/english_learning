import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Navbar } from './components/Navbar';
import { LessonListPage } from './pages/LessonListPage';
import { CreateLessonPage } from './pages/CreateLessonPage';
import { LessonDetailPage } from './pages/LessonDetailPage';
import { TopicDetailPage } from './pages/TopicDetailPage';
import { StudyFlashcardPage } from './pages/StudyFlashcardPage';
import { TestPage } from './pages/TestPage';
import { TestSummaryPage } from './pages/TestSummaryPage';
import { StatsPage } from './pages/StatsPage';
import { SettingsPage } from './pages/SettingsPage';
import { PageScrollButtons } from './components/PageScrollButtons';

export const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Navbar />
      <PageScrollButtons />
      <main className="main-content">
        <Routes>
          <Route path="/" element={<LessonListPage />} />
          <Route path="/lessons/new" element={<CreateLessonPage />} />
          <Route path="/lessons/:id" element={<LessonDetailPage />} />
          <Route path="/topics/:id" element={<TopicDetailPage />} />
          <Route path="/study/:type/:id" element={<StudyFlashcardPage />} />
          <Route path="/test/:type/:id" element={<TestPage />} />
          <Route path="/test/summary/:sessionId" element={<TestSummaryPage />} />
          <Route path="/stats" element={<StatsPage />} />
          <Route path="/settings" element={<SettingsPage />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
};
