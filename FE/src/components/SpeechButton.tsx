import React from 'react';
import { SpeechService } from '../services/speechService';

interface SpeechButtonProps {
  term: string;
}

export const SpeechButton: React.FC<SpeechButtonProps> = ({ term }) => {
  const handleClick = (e: React.MouseEvent) => {
    e.stopPropagation();
    SpeechService.speak(term);
  };

  return (
    <button type="button" className="speech-btn" onClick={handleClick} title="Phát âm">
      🔊
    </button>
  );
};
