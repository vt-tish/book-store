"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";
import { useLocale } from "@/common/hooks/useLocale";
import { useToast } from "@/common/components/Toast";
import { createBook } from "@/features/books/api";
import { BookRequestDto, AgeGroup, BookLanguage, ApiError } from "@/common/types/api";

const AGE_GROUPS: AgeGroup[] = ["CHILD", "TEEN", "ADULT", "OTHER"];
const LANGUAGES: BookLanguage[] = ["ENGLISH", "SPANISH", "FRENCH", "GERMAN", "JAPANESE", "UKRAINIAN", "OTHER"];
const SUPPORTED_LANGS = ["en", "uk"];

type FieldErrors = Record<string, string>;

export default function AdminBookNewPage() {
  const { fetchWithAuth, role } = useAuth();
  const { t, acceptLanguageHeader } = useLocale();
  const { showToast } = useToast();
  const router = useRouter();

  const [previewUrl, setPreviewUrl] = useState("");
  const [ageGroup, setAgeGroup] = useState<AgeGroup>("ADULT");
  const [price, setPrice] = useState("");
  const [publicationDate, setPublicationDate] = useState("");
  const [pages, setPages] = useState("");
  const [language, setLanguage] = useState<BookLanguage>("ENGLISH");

  const [translations, setTranslations] = useState<Record<string, { name: string; genre: string; author: string; characteristics: string; description: string; }>>(
    Object.fromEntries(SUPPORTED_LANGS.map((l) => [l, { name: "", genre: "", author: "", characteristics: "", description: "" }]))
  );

  const [loading, setLoading] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});
  const [globalError, setGlobalError] = useState("");
  const [hasSubmitted, setHasSubmitted] = useState(false);

  const isInitialMount = React.useRef(true);
  React.useEffect(() => {
    if (isInitialMount.current) {
      isInitialMount.current = false;
      return;
    }
    if (hasSubmitted) {
      const form = document.getElementById("book-form") as HTMLFormElement;
      if (form) form.requestSubmit();
    }
  }, [acceptLanguageHeader, hasSubmitted]);

  if (role !== "ADMIN" && role !== "EMPLOYEE") {
    router.replace("/admin/books");
    return null;
  }

  const handleTranslation = (lang: string, field: string, value: string) => {
    setTranslations((prev) => ({
      ...prev,
      [lang]: { ...prev[lang], [field]: value },
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setHasSubmitted(true);
    setLoading(true);
    setFieldErrors({});
    setGlobalError("");

    const data: BookRequestDto = {
      previewUrl: previewUrl || undefined,
      ageGroup,
      price: parseFloat(price),
      publicationDate,
      pages: parseInt(pages),
      language,
      translations: Object.fromEntries(
        Object.entries(translations).map(([k, v]) => [k, { ...v }])
      ),
    };

    try {
      await createBook(data, fetchWithAuth, acceptLanguageHeader);
      showToast(t("books.addBook"), "success");
      router.push("/admin/books");
    } catch (err) {
      const apiErr = err as ApiError;
      if (apiErr.validationErrors?.length) {
        const errs: FieldErrors = {};
        for (const ve of apiErr.validationErrors) errs[ve.field] = ve.message;
        setFieldErrors(errs);
      } else {
        setGlobalError(apiErr.message ?? t("app.error"));
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="admin-layout">


      <div className="admin-content">
        <div style={{ display: "flex", alignItems: "center", gap: "16px", marginBottom: "24px" }}>
          <Link href="/admin/books" className="btn btn-ghost btn-sm" id="back-to-admin-books">← {t("app.back")}</Link>
          <h1 className="page-title" style={{ margin: 0 }}>{t("books.addBook")}</h1>
        </div>

        {globalError && (
          <div className="badge badge-error" style={{ marginBottom: "16px", width: "100%", justifyContent: "flex-start", borderRadius: "8px", padding: "10px 14px" }}>
            {globalError}
          </div>
        )}

        <form onSubmit={handleSubmit} id="book-form" noValidate>
          <div className="card" style={{ marginBottom: "24px" }}>
            <h2 style={{ fontWeight: 700, marginBottom: "16px" }}>{t("books.details")}</h2>
            <div className="grid-2">
              <div className="form-group">
                <label className="form-label">{t("books.previewUrl")}</label>
                <input id="book-preview-url" className={`form-input ${fieldErrors.previewUrl ? "error" : ""}`} value={previewUrl} onChange={(e) => setPreviewUrl(e.target.value)} placeholder="https://..." />
                {fieldErrors.previewUrl && <span className="form-error">{fieldErrors.previewUrl}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">{t("books.ageGroup")}</label>
                <select id="book-age-group" className={`form-select ${fieldErrors.ageGroup ? "error" : ""}`} value={ageGroup} onChange={(e) => setAgeGroup(e.target.value as AgeGroup)}>
                  {AGE_GROUPS.map((ag) => <option key={ag} value={ag}>{t(`ageGroup.${ag}` as never)}</option>)}
                </select>
                {fieldErrors.ageGroup && <span className="form-error">{fieldErrors.ageGroup}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">{t("books.price")}</label>
                <input id="book-price" type="number" min="0" step="0.01" className={`form-input ${fieldErrors.price ? "error" : ""}`} value={price} onChange={(e) => setPrice(e.target.value)} placeholder="0.00" />
                {fieldErrors.price && <span className="form-error">{fieldErrors.price}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">{t("books.publicationDate")}</label>
                <input id="book-pub-date" type="date" className={`form-input ${fieldErrors.publicationDate ? "error" : ""}`} value={publicationDate} onChange={(e) => setPublicationDate(e.target.value)} />
                {fieldErrors.publicationDate && <span className="form-error">{fieldErrors.publicationDate}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">{t("books.pages")}</label>
                <input id="book-pages" type="number" min="5" className={`form-input ${fieldErrors.pages ? "error" : ""}`} value={pages} onChange={(e) => setPages(e.target.value)} placeholder="100" />
                {fieldErrors.pages && <span className="form-error">{fieldErrors.pages}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">{t("books.language")}</label>
                <select id="book-language" className={`form-select ${fieldErrors.language ? "error" : ""}`} value={language} onChange={(e) => setLanguage(e.target.value as BookLanguage)}>
                  {LANGUAGES.map((l) => <option key={l} value={l}>{t(`language.${l}` as never)}</option>)}
                </select>
                {fieldErrors.language && <span className="form-error">{fieldErrors.language}</span>}
              </div>
            </div>
          </div>

          {/* Translations */}
          {SUPPORTED_LANGS.map((lang) => (
            <div key={lang} className="card" style={{ marginBottom: "16px" }}>
              <h2 style={{ fontWeight: 700, marginBottom: "16px" }}>{t("books.translationFor")} [{lang.toUpperCase()}]</h2>
              <div className="grid-2" style={{ gap: "16px" }}>
                <div className="form-group">
                  <label className="form-label">{t("books.name")}</label>
                  <input
                    id={`book-name-${lang}`}
                    className={`form-input ${fieldErrors[`translations[${lang}].name`] ? "error" : ""}`}
                    value={translations[lang].name}
                    onChange={(e) => handleTranslation(lang, "name", e.target.value)}
                  />
                  {fieldErrors[`translations[${lang}].name`] && <span className="form-error">{fieldErrors[`translations[${lang}].name`]}</span>}
                </div>
                <div className="form-group">
                  <label className="form-label">{t("books.genre")}</label>
                  <input id={`book-genre-${lang}`} className="form-input" value={translations[lang].genre} onChange={(e) => handleTranslation(lang, "genre", e.target.value)} />
                </div>
                <div className="form-group">
                  <label className="form-label">{t("books.author")}</label>
                  <input id={`book-author-${lang}`} className="form-input" value={translations[lang].author} onChange={(e) => handleTranslation(lang, "author", e.target.value)} />
                </div>
                <div className="form-group">
                  <label className="form-label">{t("books.characteristics")}</label>
                  <textarea id={`book-characteristics-${lang}`} className="form-textarea" value={translations[lang].characteristics} onChange={(e) => handleTranslation(lang, "characteristics", e.target.value)} rows={3} />
                </div>
                <div className="form-group" style={{ gridColumn: "1 / -1" }}>
                  <label className="form-label">{t("books.description")}</label>
                  <textarea id={`book-description-${lang}`} className="form-textarea" value={translations[lang].description} onChange={(e) => handleTranslation(lang, "description", e.target.value)} rows={4} />
                </div>
              </div>
            </div>
          ))}

          {/* Translation-level errors */}
          {fieldErrors.translations && (
            <div className="badge badge-error" style={{ marginBottom: "16px", width: "100%", justifyContent: "flex-start", borderRadius: "8px", padding: "10px 14px" }}>
              {fieldErrors.translations}
            </div>
          )}

          <div style={{ display: "flex", gap: "12px", justifyContent: "flex-end" }}>
            <Link href="/admin/books" className="btn btn-ghost" id="cancel-book-btn">{t("app.cancel")}</Link>
            <button id="save-book-btn" type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? <span className="spinner spinner-sm" /> : t("app.save")}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
