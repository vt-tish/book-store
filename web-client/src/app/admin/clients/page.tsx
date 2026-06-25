"use client";

import React, { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";
import { useLocale } from "@/common/hooks/useLocale";
import { useToast } from "@/common/components/Toast";
import { Pagination } from "@/common/components/Pagination";
import { LoadingSpinner, ErrorMessage, ConfirmDialog } from "@/common/components/Shared";
import { getAllClients, blockClient, unblockClient } from "@/features/admin/api";
import { AdminClientResponseDto, ApiError } from "@/common/types/api";

const PAGE_SIZE = 15;

export default function AdminClientsPage() {
  const { isAuthenticated, isLoading: authLoading, fetchWithAuth, role } = useAuth();
  const { t } = useLocale();
  const { showToast } = useToast();
  const router = useRouter();

  const [clients, setClients] = useState<AdminClientResponseDto[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionLoading, setActionLoading] = useState<string | null>(null);
  const [confirm, setConfirm] = useState<{ id: string; type: "block" | "unblock" } | null>(null);

  const loadClients = useCallback(async () => {
    if (!isAuthenticated) return;
    setLoading(true);
    setError("");
    try {
      const data = await getAllClients(page, PAGE_SIZE, fetchWithAuth);
      setClients(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message ?? t("app.error"));
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated, page, fetchWithAuth]);

  useEffect(() => {
    if (!authLoading && (!isAuthenticated || (role !== "ADMIN" && role !== "EMPLOYEE"))) {
      router.replace("/login");
    }
  }, [authLoading, isAuthenticated, role, router]);

  useEffect(() => {
    if (isAuthenticated) loadClients();
  }, [loadClients, isAuthenticated]);

  const handleConfirm = async () => {
    if (!confirm) return;
    setActionLoading(confirm.id);
    setConfirm(null);
    try {
      if (confirm.type === "block") await blockClient(confirm.id, fetchWithAuth);
      else await unblockClient(confirm.id, fetchWithAuth);
      showToast(t(confirm.type === "block" ? "admin.clients.block" : "admin.clients.unblock"), "success");
      loadClients();
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
      <nav className="admin-sidebar">
        <Link href="/admin/books" className="admin-sidebar-link" id="sidebar-books">{t("admin.books")}</Link>
        <Link href="/admin/orders" className="admin-sidebar-link" id="sidebar-orders">{t("admin.orders")}</Link>
        <Link href="/admin/clients" className="admin-sidebar-link active" id="sidebar-clients">{t("admin.clients")}</Link>
        {role === "ADMIN" && <Link href="/admin/employees" className="admin-sidebar-link" id="sidebar-employees">{t("admin.employees")}</Link>}
      </nav>

      <div className="admin-content">
        <div className="page-header">
          <h1 className="page-title">{t("admin.clients.title")}</h1>
        </div>

        {loading ? <LoadingSpinner /> : error ? <ErrorMessage message={error} onRetry={loadClients} /> : (
          <>
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>{t("auth.email")}</th>
                    <th>{t("admin.clients.orders")}</th>
                    <th>{t("admin.clients.registered")}</th>
                    <th>{t("app.status")}</th>
                    <th>{t("app.actions")}</th>
                  </tr>
                </thead>
                <tbody>
                  {clients.map((client) => (
                    <tr key={client.id} id={`client-row-${client.id}`}>
                      <td style={{ fontWeight: 500 }}>{client.email}</td>
                      <td>{client.ordersCount}</td>
                      <td className="text-sm text-muted">{new Date(client.createdAt).toLocaleDateString()}</td>
                      <td>
                        {client.isBlocked ? (
                          <span className="badge badge-error">{t("admin.clients.blocked")}</span>
                        ) : (
                          <span className="badge badge-success">{t("admin.clients.active")}</span>
                        )}
                      </td>
                      <td>
                        {client.isBlocked ? (
                          <button
                            className="btn btn-success btn-sm"
                            id={`unblock-client-${client.id}`}
                            onClick={() => setConfirm({ id: client.id, type: "unblock" })}
                            disabled={actionLoading === client.id}
                          >
                            {actionLoading === client.id ? <span className="spinner spinner-sm" /> : t("admin.clients.unblock")}
                          </button>
                        ) : (
                          <button
                            className="btn btn-danger btn-sm"
                            id={`block-client-${client.id}`}
                            onClick={() => setConfirm({ id: client.id, type: "block" })}
                            disabled={actionLoading === client.id}
                          >
                            {actionLoading === client.id ? <span className="spinner spinner-sm" /> : t("admin.clients.block")}
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            {clients.length === 0 && (
              <div className="empty-state"><div className="empty-state-icon">👤</div><h3 className="empty-state-title">{t("app.noResults")}</h3></div>
            )}
            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>

      {confirm && (
        <ConfirmDialog
          message={t(confirm.type === "block" ? "admin.clients.confirmBlock" : "admin.clients.confirmUnblock")}
          onConfirm={handleConfirm}
          onCancel={() => setConfirm(null)}
          isLoading={!!actionLoading}
          variant={confirm.type === "block" ? "danger" : "primary"}
        />
      )}
    </div>
  );
}
