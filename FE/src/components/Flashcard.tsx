import React from 'react';
import { Word } from '../types/vocab';
import { SpeechButton } from './SpeechButton';

interface FlashcardProps {
  word: Word;
  isFlipped: boolean;
  onFlip: () => void;
}

export const Flashcard: React.FC<FlashcardProps> = ({ word, isFlipped, onFlip }) => {
  // TODO: implement 3D card flip animation, audio pronunciation button
  return (
    <div className={`flashcard ${isFlipped ? 'flipped' : ''}`} onClick={onFlip}>
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
