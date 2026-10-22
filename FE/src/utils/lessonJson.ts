import { LessonImportTopic, LessonImportWord, LessonJsonFile } from '../types/vocab';

export function parseLessonJson(jsonText: string): LessonJsonFile {
  const parsed: unknown = JSON.parse(jsonText);
  if (!parsed || typeof parsed !== 'object' || Array.isArray(parsed)) throw new Error('JSON cần là một object.');
  const source = parsed as Record<string, unknown>;
  const rawTitle = source.title ?? source.lesson ?? source.name;
  if (rawTitle !== undefined && typeof rawTitle !== 'string') throw new Error('Tên bài học cần là chuỗi.');
  if (!Array.isArray(source.topics) || source.topics.length === 0) throw new Error('JSON cần có ít nhất một topic.');

  const topics: LessonImportTopic[] = source.topics.map((value, topicIndex) => {
    if (!value || typeof value !== 'object' || Array.isArray(value)) throw new Error(`Topic ${topicIndex + 1} không hợp lệ.`);
    const topic = value as Record<string, unknown>;
    const rawTopicTitle = topic.title ?? topic.name;
    if (typeof rawTopicTitle !== 'string' || !rawTopicTitle.trim()) throw new Error(`Topic ${topicIndex + 1} cần có tên.`);
    if (!Array.isArray(topic.words) || topic.words.length === 0) throw new Error(`Topic “${rawTopicTitle}” cần có ít nhất một từ.`);
    const words: LessonImportWord[] = topic.words.map((item, wordIndex) => {
      if (!item || typeof item !== 'object' || Array.isArray(item)) throw new Error(`Từ ${wordIndex + 1} trong topic “${rawTopicTitle}” không hợp lệ.`);
      const word = item as Record<string, unknown>;
      const text = (key: string) => typeof word[key] === 'string' ? word[key] as string : '';
      return { term: text('term'), pos: text('pos'), ipa: text('ipa'), meaningVi: text('meaningVi'), example: text('example') };
    });
    return { title: rawTopicTitle, words };
  });
  return { title: rawTitle as string | undefined, topics };
}
