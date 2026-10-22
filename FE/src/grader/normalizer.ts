/**
 * Normalization utilities for text comparison in grading.
 */

export class TextNormalizer {
  /**
   * Normalizes text: lowercase, trim, remove punctuation.
   */
  static normalize(text: string): string {
    // TODO: implement normalization (lowercase, trim, punctuation removal)
    return text.trim().toLowerCase();
  }

  /**
   * Removes Vietnamese diacritics via NFD, handles special 'đ' -> 'd'.
   */
  static removeAccents(text: string): string {
    // TODO: implement NFD diacritics removal and 'đ' -> 'd' replacement
    return text;
  }

  /**
   * Splits meaning string (e.g. "chạy; vận hành") into individual normalized meanings.
   */
  static parseMeanings(_meaningVi: string): string[] {
    // TODO: split by ';' or ',' and normalize each meaning
    return [];
  }
}
