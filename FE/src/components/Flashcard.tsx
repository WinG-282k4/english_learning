import React from 'react';
import { Word } from '../types/vocab';
import { SpeechButton } from './SpeechButton';

interface FlashcardProps {
  word: Word;
  isFlipped: boolean;
  onFlip: () => void;
}

export const Flashcard: React.FC<FlashcardProps> = ({ word, isFlipped, onFlip }) => {
  return (
    <div className={`flashcard ${isFlipped ? 'flipped' : ''}`} onClick={onFlip} onKeyDown={event => { if (event.target === event.currentTarget && (event.key === 'Enter' || event.key === ' ')) { event.preventDefault(); onFlip(); } }} role="button" tabIndex={0} aria-label={isFlipped ? 'Th\u1ebb \u0111ang hi\u1ec7n ngh\u0129a, nh\u1ea5n \u0111\u1ec3 xem t\u1eeb' : 'Th\u1ebb \u0111ang hi\u1ec7n t\u1eeb, nh\u1ea5n \u0111\u1ec3 xem ngh\u0129a'}>
      <div className="card-front">
        <h2>{word.term}</h2>
        {word.pos && <span className="pos-badge">{word.pos}</span>}
        {word.ipa && <p className="ipa">{word.ipa}</p>}
        <SpeechButton term={word.term} />
      </div>
      <div className="card-back">
        <p className="meaning">{word.meaningVi}</p>
        {word.example && <p className="example">{word.example}</p>}
      </div>
    </div>
  );
};
