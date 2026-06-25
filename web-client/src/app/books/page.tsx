"use client";

import React, { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";
import { useLocale } from "@/common/hooks/useLocale";
import { useToast } from "@/common/components/Toast";
import { Pagination } from "@/common/components/Pagination";
import { LoadingSpinner, ErrorMessage } from "@/common/components/Shared";
import { getBooks } from "@/features/books/api";
import { addCartItem } from "@/features/cart/api";
import { BookCardResponseDto, ApiError } from "@/common/types/api";

const PAGE_SIZE = 12;

export default function BooksPage() {
  const { isAuthenticated, isLoading: authLoading, fetchWithAuth, role } = useAuth();
  const { t, locale, acceptLanguageHeader } = useLocale();
  const { showToast } = useToast();
  const router = useRouter();

  const [books, setBooks] = useState<BookCardResponseDto[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [activeFilter, setActiveFilter] = useState({ search: "", minPrice: "", maxPrice: "" });

  const [addingToCart, setAddingToCart] = useState<string | null>(null);

  const loadBooks = useCallback(async () => {
    if (!isAuthenticated) return;
    setLoading(true);
    setError("");
    try {
      const data = await getBooks(
        {
          search: activeFilter.search || undefined,
          minPrice: activeFilter.minPrice ? parseFloat(activeFilter.minPrice) : undefined,
          maxPrice: activeFilter.maxPrice ? parseFloat(activeFilter.maxPrice) : undefined,
        },
        page,
        PAGE_SIZE,
        fetchWithAuth,
        acceptLanguageHeader
      );
      setBooks(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message ?? t("app.error"));
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated, page, activeFilter, locale, fetchWithAuth, acceptLanguageHeader]);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.replace("/login");
    }
  }, [authLoading, isAuthenticated, router]);

  useEffect(() => {
    if (isAuthenticated) loadBooks();
  }, [loadBooks, isAuthenticated]);

  const applyFilter = () => {
    setPage(0);
    setActiveFilter({ search, minPrice, maxPrice });
  };

  const clearFilter = () => {
    setSearch("");
    setMinPrice("");
    setMaxPrice("");
    setPage(0);
    setActiveFilter({ search: "", minPrice: "", maxPrice: "" });
  };

  const handleAddToCart = async (bookId: string) => {
    if (role !== "CLIENT") return;
    setAddingToCart(bookId);
    try {
      await addCartItem({ bookId, quantity: 1 }, fetchWithAuth, acceptLanguageHeader);
      showToast(t("books.addToCart"), "success");
    } catch (err) {
      const apiErr = err as ApiError;
      showToast(apiErr.message ?? t("app.error"), "error");
    } finally {
      setAddingToCart(null);
    }
  };

  if (authLoading) return <LoadingSpinner />;

  return (
    <div className="container page-wrapper page-enter">
      <div className="page-header">
        <div>
          <h1 className="page-title">{t("books.title")}</h1>
          <p className="page-subtitle">{books.length > 0 ? `${books.length}+ ${t("books.title").toLowerCase()}` : ""}</p>
        </div>
      </div>

      {/* Filter bar */}
      <div className="filter-bar">
        <div className="form-group" style={{ flex: 2, minWidth: "180px" }}>
          <label className="form-label">{t("books.search")}</label>
          <input
            id="books-search"
            className="form-input"
            type="text"
            placeholder={t("books.search")}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && applyFilter()}
          />
        </div>
        <div className="form-group">
          <label className="form-label">{t("books.minPrice")}</label>
          <input
            id="books-min-price"
            className="form-input"
            type="number"
            min="0"
            placeholder="0.00"
            value={minPrice}
            onChange={(e) => setMinPrice(e.target.value)}
          />
        </div>
        <div className="form-group">
          <label className="form-label">{t("books.maxPrice")}</label>
          <input
            id="books-max-price"
            className="form-input"
            type="number"
            min="0"
            placeholder="999.99"
            value={maxPrice}
            onChange={(e) => setMaxPrice(e.target.value)}
          />
        </div>
        <div style={{ display: "flex", gap: "8px", alignItems: "flex-end" }}>
          <button id="apply-filter-btn" className="btn btn-primary" onClick={applyFilter}>
            {t("books.applyFilter")}
          </button>
          <button id="clear-filter-btn" className="btn btn-ghost" onClick={clearFilter}>
            {t("books.clearFilter")}
          </button>
        </div>
      </div>

      {/* Content */}
      {loading ? (
        <LoadingSpinner />
      ) : error ? (
        <ErrorMessage message={error} onRetry={loadBooks} />
      ) : books.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">📭</div>
          <h3 className="empty-state-title">{t("app.noResults")}</h3>
        </div>
      ) : (
        <>
          <div className="grid-books">
            {books.map((book) => (
              <div key={book.id} className="book-card" id={`book-card-${book.id}`}>
                <Link href={`/books/${book.id}`} style={{ textDecoration: "none", color: "inherit" }}>
                  {book.previewUrl ? (
                    <img
                      src={book.previewUrl}
                      alt={book.name}
                      className="book-card-image"
                      onError={(e) => {
                        (e.target as HTMLImageElement).style.display = "none";
                      }}
                    />
                  ) : (
                    <div className="book-card-placeholder">📖</div>
                  )}
                  <div className="book-card-body">
                    <div className="book-card-name">{book.name}</div>
                    <div className="book-card-author">{book.author}</div>
                    <div
                      className="text-xs text-muted"
                      style={{ padding: "2px 8px", background: "var(--color-bg-secondary)", borderRadius: "4px", width: "fit-content" }}
                    >
                      {book.genre}
                    </div>
                    <div className="book-card-price">${Number(book.price).toFixed(2)}</div>
                  </div>
                </Link>
                {role === "CLIENT" && (
                  <div style={{ padding: "0 var(--space-md) var(--space-md)" }}>
                    <button
                      id={`add-to-cart-${book.id}`}
                      className="btn btn-primary"
                      style={{ width: "100%" }}
                      onClick={() => handleAddToCart(book.id)}
                      disabled={addingToCart === book.id}
                    >
                      {addingToCart === book.id ? (
                        <span className="spinner spinner-sm" />
                      ) : (
                        <>🛒 {t("books.addToCart")}</>
                      )}
                    </button>
                  </div>
                )}
              </div>
            ))}
          </div>

          <Pagination
            currentPage={page}
            totalPages={totalPages}
            onPageChange={setPage}
          />
        </>
      )}
    </div>
  );
}
