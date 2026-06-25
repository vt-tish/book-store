import en from "./en";
import type { TranslationKeys } from "./en";
import uk from "./uk";

export type Locale = "en" | "uk";

export const SUPPORTED_LOCALES: Locale[] = ["en", "uk"];
export const DEFAULT_LOCALE: Locale = "en";

const translations: Record<Locale, typeof en> = { en, uk: uk as unknown as typeof en };

export function getTranslation(locale: Locale): typeof en {
  return translations[locale] || translations[DEFAULT_LOCALE];
}

export function t(locale: Locale, key: TranslationKeys): string {
  const dict = getTranslation(locale);
  return (dict[key] as string) ?? key;
}

export type { TranslationKeys };
