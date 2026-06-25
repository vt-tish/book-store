"use client";

import React from "react";
import { useLocale } from "@/common/hooks/useLocale";
import { Locale, SUPPORTED_LOCALES } from "@/i18n";

const LOCALE_LABELS: Record<Locale, string> = {
  en: "EN",
  uk: "UK",
};

export function LanguageSwitcher() {
  const { locale, setLocale } = useLocale();

  return (
    <div
      style={{ display: "flex", gap: "4px", alignItems: "center" }}
      role="group"
      aria-label="Language switcher"
    >
      {SUPPORTED_LOCALES.map((l) => (
        <button
          key={l}
          id={`lang-btn-${l}`}
          className={`btn btn-sm ${locale === l ? "btn-secondary" : "btn-ghost"}`}
          style={{ padding: "4px 10px", fontSize: "12px" }}
          onClick={() => setLocale(l)}
          aria-pressed={locale === l}
          aria-label={`Switch to ${l === "en" ? "English" : "Ukrainian"}`}
        >
          {LOCALE_LABELS[l]}
        </button>
      ))}
    </div>
  );
}
