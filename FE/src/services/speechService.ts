/**
 * Speech synthesis service utilizing browser Web Speech API.
 */
export class SpeechService {
  /**
   * Speaks English term using browser's speechSynthesis.
   */
  static speak(term: string, lang: string = 'en-US'): void {
    if (!('speechSynthesis' in window)) {
      console.warn('Speech synthesis not supported in this browser.');
      return;
    }
    window.speechSynthesis.cancel();
    const utterance = new SpeechSynthesisUtterance(term);
    utterance.lang = lang;
    window.speechSynthesis.speak(utterance);
  }
}
