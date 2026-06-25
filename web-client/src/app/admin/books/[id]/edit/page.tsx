"use client";

import React, { useState, useEffect, use } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";
import { useLocale } from "@/common/hooks/useLocale";
import { useToast } from "@/common/components/Toast";
import { LoadingSpinner } from "@/common/components/Shared";
import { getBookByIdAdmin, updateBook } from "@/features/books/api";
import { BookRequestDto, AgeGroup, BookLanguage, AdminBookDetailsResponseDto, ApiError } from "@/common/types/api";

const AGE_GROUPS: AgeGroup[] = ["CHILD", "TEEN", "ADULT", "OTHER"];
const LANGUAGES: BookLanguage[] = ["ENGLISH", "SPANISH", "FRENCH", "GERMAN", "JAPANESE", "UKRAINIAN", "OTHER"];
const SUPPORTED_LANGS = ["en", "uk"];

interface Props {
  params: Promise<{ id: string }>;
}

export default function AdminBookEditPage({ params }: Props) {
  const { id } = use(params);
  const { fetchWithAuth, role } = useAuth();
  const { t, locale, acceptLanguageHeader } = useLocale();
  const { showToast } = useToast();
  const router = useRouter();

  const [book, setBook] = useState<AdminBookDetailsResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [globalError, setGlobalError] = useState("");
  const [hasSubmitted, setHasSubmitted] = useState(false);

  const isInitialMount = React.useRef(true);
  useEffect(() => {
    if (isInitialMount.current) {
      isInitialMount.current = false;
      return;
    }
    if (hasSubmitted) {
      const form = document.getElementById("edit-book-form") as HTMLFormElement;
      if (form) form.requestSubmit();
    }
  }, [acceptLanguageHeader, hasSubmitted]);

  // Form state
  const [previewUrl, setPreviewUrl] = useState("");
  const [ageGroup, setAgeGroup] = useState<AgeGroup>("ADULT");
  const [price, setPrice] = useState("");
  const [publicationDate, setPublicationDate] = useState("");
  const [pages, setPages] = useState("");
  const [language, setLanguage] = useState<BookLanguage>("ENGLISH");
  const [translations, setTranslations] = useState<Record<string, { name: string; genre: string; author: string; characteristics: string; description: string; }>>(
    Object.fromEntries(SUPPORTED_LANGS.map((l) => [l, { name: "", genre: "", author: "", characteristics: "", description: "" }]))
  );

  useEffect(() => {
    getBookByIdAdmin(id, fetchWithAuth, acceptLanguageHeader)
      .then((b) => {
        setBook(b);
        setPreviewUrl(b.previewUrl ?? "");
        setAgeGroup(b.ageGroup);
        setPrice(String(b.price));
        setPublicationDate(b.publicationDate);
        setPages(String(b.pages));
        setLanguage(b.language);
        
        if (b.translations) {
          setTranslations((prev) => {
            const next = { ...prev };
            for (const lang of SUPPORTED_LANGS) {
              if (b.translations[lang]) {
                next[lang] = {
                  name: b.translations[lang].name ?? "",
                  genre: b.translations[lang].genre ?? "",
                  author: b.translations[lang].author ?? "",
                  characteristics: b.translations[lang].characteristics ?? "",
                  description: b.translations[lang].description ?? ""
                };
              }
            }
            return next;
          });
        }
      })
      .catch((err: ApiError) => setGlobalError(err.message ?? t("app.error")))
      .finally(() => setLoading(false));
  }, [id, fetchWithAuth, acceptLanguageHeader, t]);

  const handleTranslation = (lang: string, field: string, value: string) => {
    setTranslations((prev) => ({ ...prev, [lang]: { ...prev[lang], [field]: value } }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setHasSubmitted(true);
    setSaving(true);
    setFieldErrors({});
    setGlobalError("");

    const data: BookRequestDto = {
      previewUrl: previewUrl || undefined,
      ageGroup,
      price: parseFloat(price),
      publicationDate,
      pages: parseInt(pages),
      language,
      translations: Object.fromEntries(Object.entries(translations).map(([k, v]) => [k, { ...v }])),
    };

    try {
      await updateBook(id, data, fetchWithAuth, acceptLanguageHeader);
      showToast(t("books.editBook"), "success");
      router.push("/admin/books");
    } catch (err) {
      const apiErr = err as ApiError;
      if (apiErr.validationErrors?.length) {
        const errs: Record<string, string> = {};
        for (const ve of apiErr.validationErrors) errs[ve.field] = ve.message;
        setFieldErrors(errs);
      } else {
        setGlobalError(apiErr.message ?? t("app.error"));
      }
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="admin-layout">


      <div className="admin-content">
        <div style={{ display: "flex", alignItems: "center", gap: "16px", marginBottom: "24px" }}>
          <Link href="/admin/books" className="btn btn-ghost btn-sm" id="back-to-admin-books">← {t("app.back")}</Link>
          <h1 className="page-title" style={{ margin: 0 }}>{t("books.editBook")}</h1>
        </div>

        {globalError && (
          <div className="badge badge-error" style={{ marginBottom: "16px", width: "100%", justifyContent: "flex-start", borderRadius: "8px", padding: "10px 14px" }}>
            {globalError}
          </div>
        )}

        <form onSubmit={handleSubmit} id="edit-book-form" noValidate>
          <div className="card" style={{ marginBottom: "24px" }}>
            <h2 style={{ fontWeight: 700, marginBottom: "16px" }}>{t("books.details")}</h2>
            <div className="grid-2">
              <div className="form-group">
                <label className="form-label">{t("books.previewUrl")}</label>
                <input id="edit-book-preview-url" className={`form-input ${fieldErrors.previewUrl ? "error" : ""}`} value={previewUrl} onChange={(e) => setPreviewUrl(e.target.value)} />
                {fieldErrors.previewUrl && <span className="form-error">{fieldErrors.previewUrl}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">{t("books.ageGroup")}</label>
                <select id="edit-book-age-group" className="form-select" value={ageGroup} onChange={(e) => setAgeGroup(e.target.value as AgeGroup)}>
                  {AGE_GROUPS.map((ag) => <option key={ag} value={ag}>{t(`ageGroup.${ag}` as never)}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">{t("books.price")}</label>
                <input id="edit-book-price" type="number" min="0" step="0.01" className={`form-input ${fieldErrors.price ? "error" : ""}`} value={price} onChange={(e) => setPrice(e.target.value)} />
                {fieldErrors.price && <span className="form-error">{fieldErrors.price}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">{t("books.publicationDate")}</label>
                <input id="edit-book-pub-date" type="date" className="form-input" value={publicationDate} onChange={(e) => setPublicationDate(e.target.value)} />
              </div>
              <div className="form-group">
                <label className="form-label">{t("books.pages")}</label>
                <input id="edit-book-pages" type="number" min="5" className="form-input" value={pages} onChange={(e) => setPages(e.target.value)} />
              </div>
              <div className="form-group">
                <label className="form-label">{t("books.language")}</label>
                <select id="edit-book-language" className="form-select" value={language} onChange={(e) => setLanguage(e.target.value as BookLanguage)}>
                  {LANGUAGES.map((l) => <option key={l} value={l}>{t(`language.${l}` as never)}</option>)}
                </select>
              </div>
            </div>
          </div>

          {SUPPORTED_LANGS.map((lang) => (
            <div key={lang} className="card" style={{ marginBottom: "16px" }}>
              <h2 style={{ fontWeight: 700, marginBottom: "16px" }}>{t("books.translationFor")} [{lang.toUpperCase()}]</h2>
              <div className="grid-2">
                <div className="form-group">
                  <label className="form-label">{t("books.name")}</label>
                  <input id={`edit-book-name-${lang}`} className="form-input" value={translations[lang].name} onChange={(e) => handleTranslation(lang, "name", e.target.value)} />
                </div>
                <div className="form-group">
                  <label className="form-label">{t("books.genre")}</label>
                  <input id={`edit-book-genre-${lang}`} className="form-input" value={translations[lang].genre} onChange={(e) => handleTranslation(lang, "genre", e.target.value)} />
                </div>
                <div className="form-group">
                  <label className="form-label">{t("books.author")}</label>
                  <input id={`edit-book-author-${lang}`} className="form-input" value={translations[lang].author} onChange={(e) => handleTranslation(lang, "author", e.target.value)} />
                </div>
                <div className="form-group">
                  <label className="form-label">{t("books.characteristics")}</label>
                  <textarea id={`edit-book-characteristics-${lang}`} className="form-textarea" value={translations[lang].characteristics} onChange={(e) => handleTranslation(lang, "characteristics", e.target.value)} rows={3} />
                </div>
                <div className="form-group" style={{ gridColumn: "1 / -1" }}>
                  <label className="form-label">{t("books.description")}</label>
                  <textarea id={`edit-book-description-${lang}`} className="form-textarea" value={translations[lang].description} onChange={(e) => handleTranslation(lang, "description", e.target.value)} rows={4} />
                </div>
              </div>
            </div>
          ))}

          <div style={{ display: "flex", gap: "12px", justifyContent: "flex-end" }}>
            <Link href="/admin/books" className="btn btn-ghost" id="cancel-edit-book-btn">{t("app.cancel")}</Link>
            <button id="update-book-btn" type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? <span className="spinner spinner-sm" /> : t("app.save")}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
