export type LlmProviderType = 'GEMINI' | 'GROQ';

export interface AiKey {
  id: number;
  provider: LlmProviderType;
  maskedKey: string;
  isDefault: boolean;
}

export interface AppSetting {
  key: string;
  value: string;
}
