"use client";

import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
} from "react";
import { Locale, DEFAULT_LOCALE, SUPPORTED_LOCALES } from "@/i18n";
import { TranslationKeys } from "@/i18n/en";

interface LocaleContextType {
  locale: Locale;
  setLocale: (locale: Locale) => void;
  t: (key: TranslationKeys) => string;
  acceptLanguageHeader: string;
}

const LocaleContext = createContext<LocaleContextType | null>(null);

const LOCALE_STORAGE_KEY = "bookstore_locale";

export function LocaleProvider({ children }: { children: React.ReactNode }) {
  const [locale, setLocaleState] = useState<Locale>(DEFAULT_LOCALE);
  const [dict, setDict] = useState<Record<string, string>>({});

  useEffect(() => {
    const stored = localStorage.getItem(LOCALE_STORAGE_KEY) as Locale | null;
    if (stored && SUPPORTED_LOCALES.includes(stored)) {
      setLocaleState(stored);
    }
  }, []);

  useEffect(() => {
    import(`@/i18n/${locale}`).then((m) => {
      setDict(m.default);
    });
  }, [locale]);

  const setLocale = useCallback((newLocale: Locale) => {
    localStorage.setItem(LOCALE_STORAGE_KEY, newLocale);
    setLocaleState(newLocale);
  }, []);

  const t = useCallback(
    (key: TranslationKeys): string => {
      return (dict[key] as string) ?? key;
    },
    [dict]
  );

  const acceptLanguageHeader = locale === "uk" ? "uk,en;q=0.5" : "en,uk;q=0.5";

  return (
    <LocaleContext.Provider
      value={{ locale, setLocale, t, acceptLanguageHeader }}
    >
      {children}
    </LocaleContext.Provider>
  );
}

export function useLocale() {
  const ctx = useContext(LocaleContext);
  if (!ctx) throw new Error("useLocale must be used within LocaleProvider");
  return ctx;
}
