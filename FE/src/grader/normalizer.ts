/** Text normalization used by the two local grading rounds. */
export class TextNormalizer {
  static normalize(text: string): string {
    return text.toLocaleLowerCase().trim().replace(/[^\p{L}\p{N}\s]/gu, ' ').replace(/\s+/g, ' ').trim();
  }

  static removeAccents(text: string): string {
    return text.toLocaleLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/đ/g, 'd');
  }

  static parseMeanings(meaningVi: string): string[] {
    return meaningVi.split(/[;,，、]/).map(TextNormalizer.normalize).filter(Boolean);
  }
}
