/** Levenshtein distance calculation for short-answer typo tolerance. */
export class LevenshteinCalculator {
  static distance(a: string, b: string): number {
    if (a === b) return 0;
    if (!a.length) return b.length;
    if (!b.length) return a.length;
    let previous = Array.from({ length: b.length + 1 }, (_, index) => index);
    for (let i = 1; i <= a.length; i++) {
      const current = new Array<number>(b.length + 1);
      current[0] = i;
      for (let j = 1; j <= b.length; j++) {
        current[j] = Math.min(current[j - 1] + 1, previous[j] + 1,
          previous[j - 1] + (a[i - 1] === b[j - 1] ? 0 : 1));
      }
      previous = current;
    }
    return previous[b.length];
  }

  static isMatch(target: string, candidate: string, maxDistance = 1, minLength = 5): boolean {
    const expected = target.trim().toLowerCase();
    const actual = candidate.trim().toLowerCase();
    if (expected.length <= minLength || actual.length <= minLength) return false;
    if (Math.abs(expected.length - actual.length) > maxDistance) return false;
    return LevenshteinCalculator.distance(expected, actual) <= maxDistance;
  }
}
