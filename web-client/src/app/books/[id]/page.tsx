"use client";

import React, { useState, useEffect, use } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";
import { useLocale } from "@/common/hooks/useLocale";
import { useToast } from "@/common/components/Toast";
import { LoadingSpinner, ErrorMessage } from "@/common/components/Shared";
import { getBookById } from "@/features/books/api";
import { addCartItem } from "@/features/cart/api";
import { BookDetailsResponseDto, ApiError } from "@/common/types/api";

interface Props {
  params: Promise<{ id: string }>;
}

export default function BookDetailPage({ params }: Props) {
  const { id } = use(params);
  const { isAuthenticated, isLoading: authLoading, fetchWithAuth, role } = useAuth();
  const { t, locale, acceptLanguageHeader } = useLocale();
  const { showToast } = useToast();
  const router = useRouter();

  const [book, setBook] = useState<BookDetailsResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [addingToCart, setAddingToCart] = useState(false);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) router.replace("/login");
  }, [authLoading, isAuthenticated, router]);

  useEffect(() => {
    if (!isAuthenticated) return;
    setLoading(true);
    setError("");
    getBookById(id, fetchWithAuth, acceptLanguageHeader)
      .then(setBook)
      .catch((err: ApiError) => setError(err.message ?? t("app.error")))
      .finally(() => setLoading(false));
  }, [id, isAuthenticated, locale, acceptLanguageHeader]);

  const handleAddToCart = async () => {
    if (!book || role !== "CLIENT") return;
    setAddingToCart(true);
    try {
      await addCartItem({ bookId: book.id, quantity: 1 }, fetchWithAuth, acceptLanguageHeader);
      showToast(t("books.addToCart"), "success");
    } catch (err) {
      const apiErr = err as ApiError;
      showToast(apiErr.message ?? t("app.error"), "error");
    } finally {
      setAddingToCart(false);
    }
  };

  if (authLoading || loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message={error} onRetry={() => router.back()} />;
  if (!book) return null;

  return (
    <div className="container page-wrapper page-enter">
      <Link href="/books" className="btn btn-ghost btn-sm" style={{ marginBottom: "24px", display: "inline-flex" }} id="back-to-books">
        ← {t("books.back")}
      </Link>

      <div className="detail-grid">
        {/* Book Image */}
        <div>
          {book.previewUrl ? (
            <img
              src={book.previewUrl}
              alt={book.name}
              className="detail-image"
              onError={(e) => { (e.target as HTMLImageElement).style.display = "none"; }}
            />
          ) : (
            <div
              className="detail-image"
              style={{ display: "flex", alignItems: "center", justifyContent: "center", fontSize: "80px", background: "var(--color-bg-card)", border: "1px solid var(--color-border)" }}
            >
              📖
            </div>
          )}
        </div>

        {/* Book Info */}
        <div className="detail-info">
          <div>
            <div
              className="text-xs"
              style={{ color: "var(--color-accent-light)", fontWeight: 600, textTransform: "uppercase", letterSpacing: "0.06em", marginBottom: "8px" }}
            >
              {book.genre}
            </div>
            <h1 style={{ fontSize: "var(--font-size-3xl)", fontWeight: 800, letterSpacing: "-0.02em", lineHeight: 1.1, marginBottom: "8px" }}>
              {book.name}
            </h1>
            <p style={{ fontSize: "var(--font-size-lg)", color: "var(--color-text-secondary)" }}>
              {t("books.author")}: <strong style={{ color: "var(--color-text-primary)" }}>{book.author}</strong>
            </p>
          </div>

          {/* Price & Status */}
          <div className="card" style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: "16px" }}>
            <div>
              <div className="detail-label">{t("books.price")}</div>
              <div style={{ fontSize: "var(--font-size-3xl)", fontWeight: 800, color: "var(--color-accent-light)" }}>
                ${Number(book.price).toFixed(2)}
              </div>
            </div>
            <span className={`badge ${book.isAvailable ? "badge-success" : "badge-error"}`}>
              {book.isAvailable ? t("books.available") : t("books.unavailable")}
            </span>
          </div>

          {/* Details grid */}
          <div className="card">
            <div className="grid-2" style={{ gap: "16px" }}>
              <div>
                <div className="detail-label">{t("books.ageGroup")}</div>
                <div className="detail-value">{t(`ageGroup.${book.ageGroup}` as never)}</div>
              </div>
              <div>
                <div className="detail-label">{t("books.language")}</div>
                <div className="detail-value">{t(`language.${book.language}` as never)}</div>
              </div>
              <div>
                <div className="detail-label">{t("books.pages")}</div>
                <div className="detail-value">{book.pages}</div>
              </div>
              <div>
                <div className="detail-label">{t("books.publicationDate")}</div>
                <div className="detail-value">{book.publicationDate}</div>
              </div>
            </div>
          </div>

          {/* Description & Characteristics */}
          {book.characteristics && (
            <div className="card">
              <div className="detail-label" style={{ marginBottom: "8px" }}>{t("books.characteristics")}</div>
              <p style={{ color: "var(--color-text-secondary)", lineHeight: 1.7, fontSize: "var(--font-size-sm)" }}>{book.characteristics}</p>
            </div>
          )}

          {book.description && (
            <div className="card">
              <div className="detail-label" style={{ marginBottom: "8px" }}>{t("books.description")}</div>
              <p style={{ color: "var(--color-text-secondary)", lineHeight: 1.7, fontSize: "var(--font-size-sm)" }}>{book.description}</p>
            </div>
          )}

          {/* Add to cart */}
          {role === "CLIENT" && book.isAvailable && (
            <button
              id={`book-detail-add-cart-${book.id}`}
              className="btn btn-primary btn-lg"
              onClick={handleAddToCart}
              disabled={addingToCart}
              style={{ width: "100%" }}
            >
              {addingToCart ? <span className="spinner spinner-sm" /> : <>🛒 {t("books.addToCart")}</>}
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
