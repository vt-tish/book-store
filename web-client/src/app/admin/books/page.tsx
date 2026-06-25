"use client";

import React, { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";
import { useLocale } from "@/common/hooks/useLocale";
import { useToast } from "@/common/components/Toast";
import { Pagination } from "@/common/components/Pagination";
import { LoadingSpinner, ErrorMessage, ConfirmDialog } from "@/common/components/Shared";
import { getAllBooksAdmin, archiveBook, unarchiveBook, deleteBook } from "@/features/books/api";
import { AdminBookCardResponseDto, ApiError } from "@/common/types/api";

const PAGE_SIZE = 15;

export default function AdminBooksPage() {
  const { isAuthenticated, isLoading: authLoading, fetchWithAuth, role } = useAuth();
  const { t, locale, acceptLanguageHeader } = useLocale();
  const { showToast } = useToast();
  const router = useRouter();

  const [books, setBooks] = useState<AdminBookCardResponseDto[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionLoading, setActionLoading] = useState<string | null>(null);

  const [search, setSearch] = useState("");
  const [activeSearch, setActiveSearch] = useState("");

  const [confirmAction, setConfirmAction] = useState<{ id: string; type: "delete" | "archive" | "unarchive" } | null>(null);

  const loadBooks = useCallback(async () => {
    if (!isAuthenticated) return;
    setLoading(true);
    setError("");
    try {
      const data = await getAllBooksAdmin(
        { search: activeSearch || undefined },
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
  }, [isAuthenticated, page, activeSearch, locale, fetchWithAuth, acceptLanguageHeader]);

  useEffect(() => {
    if (!authLoading && (!isAuthenticated || (role !== "ADMIN" && role !== "EMPLOYEE"))) {
      router.replace("/login");
    }
  }, [authLoading, isAuthenticated, role, router]);

  useEffect(() => {
    if (isAuthenticated) loadBooks();
  }, [loadBooks, isAuthenticated]);

  const handleConfirm = async () => {
    if (!confirmAction) return;
    setActionLoading(confirmAction.id);
    setConfirmAction(null);

    try {
      if (confirmAction.type === "delete") {
        await deleteBook(confirmAction.id, fetchWithAuth);
        showToast(t("books.deleteBook"), "success");
      } else if (confirmAction.type === "archive") {
        await archiveBook(confirmAction.id, fetchWithAuth);
        showToast(t("books.archiveBook"), "success");
      } else {
        await unarchiveBook(confirmAction.id, fetchWithAuth);
        showToast(t("books.unarchiveBook"), "success");
      }
      loadBooks();
    } catch (err) {
      const apiErr = err as ApiError;
      showToast(apiErr.message ?? t("app.error"), "error");
    } finally {
      setActionLoading(null);
    }
  };

  if (authLoading) return <LoadingSpinner />;

  return (
    <div className="admin-layout">
      {/* Sidebar */}
      <nav className="admin-sidebar">
        <Link href="/admin/books" className="admin-sidebar-link active" id="sidebar-books">{t("admin.books")}</Link>
        <Link href="/admin/orders" className="admin-sidebar-link" id="sidebar-orders">{t("admin.orders")}</Link>
        <Link href="/admin/clients" className="admin-sidebar-link" id="sidebar-clients">{t("admin.clients")}</Link>
        {role === "ADMIN" && (
          <Link href="/admin/employees" className="admin-sidebar-link" id="sidebar-employees">{t("admin.employees")}</Link>
        )}
      </nav>

      <div className="admin-content">
        <div className="page-header">
          <h1 className="page-title">{t("admin.books")}</h1>
          {role === "ADMIN" && (
            <Link href="/admin/books/new" className="btn btn-primary" id="add-book-btn">
              + {t("books.addBook")}
            </Link>
          )}
        </div>

        {/* Search */}
        <div className="filter-bar">
          <div className="form-group" style={{ flex: 1 }}>
            <label className="form-label">{t("books.search")}</label>
            <input
              id="admin-books-search"
              className="form-input"
              placeholder={t("books.search")}
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              onKeyDown={(e) => { if (e.key === "Enter") { setPage(0); setActiveSearch(search); } }}
            />
          </div>
          <div style={{ display: "flex", gap: "8px", alignItems: "flex-end" }}>
            <button className="btn btn-primary" id="admin-books-search-btn" onClick={() => { setPage(0); setActiveSearch(search); }}>
              {t("app.search")}
            </button>
            <button className="btn btn-ghost" id="admin-books-clear-btn" onClick={() => { setSearch(""); setActiveSearch(""); setPage(0); }}>
              {t("books.clearFilter")}
            </button>
          </div>
        </div>

        {loading ? <LoadingSpinner /> : error ? <ErrorMessage message={error} onRetry={loadBooks} /> : (
          <>
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>{t("books.name")}</th>
                    <th>{t("books.author")}</th>
                    <th>{t("books.genre")}</th>
                    <th>{t("books.price")}</th>
                    <th>{t("app.status")}</th>
                    <th>{t("app.actions")}</th>
                  </tr>
                </thead>
                <tbody>
                  {books.map((book) => (
                    <tr key={book.id} id={`admin-book-row-${book.id}`}>
                      <td style={{ fontWeight: 500, maxWidth: "200px", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
                        <Link href={`/admin/books/${book.id}`} style={{ color: "var(--color-accent-light)" }}>
                          {book.name}
                        </Link>
                      </td>
                      <td className="text-muted text-sm">{book.author}</td>
                      <td className="text-sm">{book.genre}</td>
                      <td style={{ fontWeight: 600 }}>${Number(book.price).toFixed(2)}</td>
                      <td>
                        {book.isArchived ? (
                          <span className="badge badge-neutral">{t("books.archived")}</span>
                        ) : (
                          <span className="badge badge-success">{t("books.available")}</span>
                        )}
                      </td>
                      <td>
                        <div style={{ display: "flex", gap: "6px" }}>
                          <Link href={`/admin/books/${book.id}/edit`} className="btn btn-ghost btn-sm" id={`edit-book-${book.id}`}>
                            {t("app.edit")}
                          </Link>
                          {book.isArchived ? (
                            <button
                              className="btn btn-success btn-sm"
                              id={`unarchive-book-${book.id}`}
                              onClick={() => setConfirmAction({ id: book.id, type: "unarchive" })}
                              disabled={actionLoading === book.id}
                            >
                              {actionLoading === book.id ? <span className="spinner spinner-sm" /> : t("books.unarchiveBook")}
                            </button>
                          ) : (
                            <button
                              className="btn btn-ghost btn-sm"
                              id={`archive-book-${book.id}`}
                              onClick={() => setConfirmAction({ id: book.id, type: "archive" })}
                              disabled={actionLoading === book.id}
                            >
                              {actionLoading === book.id ? <span className="spinner spinner-sm" /> : t("books.archiveBook")}
                            </button>
                          )}
                          {role === "ADMIN" && (
                            <button
                              className="btn btn-danger btn-sm"
                              id={`delete-book-${book.id}`}
                              onClick={() => setConfirmAction({ id: book.id, type: "delete" })}
                              disabled={actionLoading === book.id}
                            >
                              {t("app.delete")}
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            {books.length === 0 && (
              <div className="empty-state"><div className="empty-state-icon">📭</div><h3 className="empty-state-title">{t("app.noResults")}</h3></div>
            )}
            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>

      {confirmAction && (
        <ConfirmDialog
          message={confirmAction.type === "delete" ? t("books.deleteBook") : confirmAction.type === "archive" ? t("books.archiveBook") : t("books.unarchiveBook")}
          onConfirm={handleConfirm}
          onCancel={() => setConfirmAction(null)}
          isLoading={!!actionLoading}
          variant={confirmAction.type === "delete" ? "danger" : "primary"}
        />
      )}
    </div>
  );
}
