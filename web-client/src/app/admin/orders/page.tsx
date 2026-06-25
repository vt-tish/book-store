"use client";

import React, { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";
import { useLocale } from "@/common/hooks/useLocale";
import { useToast } from "@/common/components/Toast";
import { Pagination } from "@/common/components/Pagination";
import { LoadingSpinner, ErrorMessage, ConfirmDialog } from "@/common/components/Shared";
import { getAllOrdersAdmin, acceptOrder, completeOrder, cancelOrder } from "@/features/orders/api";
import { AdminOrderCardResponseDto, OrderStatus, ApiError } from "@/common/types/api";

const PAGE_SIZE = 15;
const ORDER_STATUSES: OrderStatus[] = ["PENDING", "ACCEPTED", "CANCELLED", "COMPLETED"];

export default function AdminOrdersPage() {
  const { isAuthenticated, isLoading: authLoading, fetchWithAuth, role } = useAuth();
  const { t } = useLocale();
  const { showToast } = useToast();
  const router = useRouter();

  const [orders, setOrders] = useState<AdminOrderCardResponseDto[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionLoading, setActionLoading] = useState<string | null>(null);

  const [filterStatus, setFilterStatus] = useState<OrderStatus | "">("");
  const [activeStatus, setActiveStatus] = useState<OrderStatus | "">("");

  const [confirm, setConfirm] = useState<{ id: string; type: "accept" | "complete" | "cancel" } | null>(null);

  const loadOrders = useCallback(async () => {
    if (!isAuthenticated) return;
    setLoading(true);
    setError("");
    try {
      const data = await getAllOrdersAdmin(
        { status: activeStatus || undefined },
        page,
        PAGE_SIZE,
        fetchWithAuth
      );
      setOrders(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message ?? t("app.error"));
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated, page, activeStatus, fetchWithAuth]);

  useEffect(() => {
    if (!authLoading && (!isAuthenticated || (role !== "ADMIN" && role !== "EMPLOYEE"))) {
      router.replace("/login");
    }
  }, [authLoading, isAuthenticated, role, router]);

  useEffect(() => {
    if (isAuthenticated) loadOrders();
  }, [loadOrders, isAuthenticated]);

  const handleConfirm = async () => {
    if (!confirm) return;
    setActionLoading(confirm.id);
    setConfirm(null);
    try {
      if (confirm.type === "accept") await acceptOrder(confirm.id, fetchWithAuth);
      else if (confirm.type === "complete") await completeOrder(confirm.id, fetchWithAuth);
      else await cancelOrder(confirm.id, fetchWithAuth);
      showToast(t(`admin.orders.${confirm.type}` as never), "success");
      loadOrders();
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
        <Link href="/admin/orders" className="admin-sidebar-link active" id="sidebar-orders">{t("admin.orders")}</Link>
        <Link href="/admin/clients" className="admin-sidebar-link" id="sidebar-clients">{t("admin.clients")}</Link>
        {role === "ADMIN" && <Link href="/admin/employees" className="admin-sidebar-link" id="sidebar-employees">{t("admin.employees")}</Link>}
      </nav>

      <div className="admin-content">
        <div className="page-header">
          <h1 className="page-title">{t("admin.orders.title")}</h1>
        </div>

        {/* Filter */}
        <div className="filter-bar">
          <div className="form-group">
            <label className="form-label">{t("admin.orders.filterByStatus")}</label>
            <select
              id="admin-orders-status-filter"
              className="form-select"
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value as OrderStatus | "")}
            >
              <option value="">{t("app.all")}</option>
              {ORDER_STATUSES.map((s) => (
                <option key={s} value={s}>{t(`orders.status.${s}` as never)}</option>
              ))}
            </select>
          </div>
          <div style={{ display: "flex", gap: "8px", alignItems: "flex-end" }}>
            <button className="btn btn-primary" id="admin-orders-apply-btn" onClick={() => { setPage(0); setActiveStatus(filterStatus); }}>
              {t("books.applyFilter")}
            </button>
            <button className="btn btn-ghost" id="admin-orders-clear-btn" onClick={() => { setFilterStatus(""); setActiveStatus(""); setPage(0); }}>
              {t("books.clearFilter")}
            </button>
          </div>
        </div>

        {loading ? <LoadingSpinner /> : error ? <ErrorMessage message={error} onRetry={loadOrders} /> : (
          <>
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>{t("admin.orders.client")}</th>
                    <th>{t("admin.orders.employee")}</th>
                    <th>{t("orders.total")}</th>
                    <th>{t("orders.items")}</th>
                    <th>{t("app.status")}</th>
                    <th>{t("orders.created")}</th>
                    {role === "EMPLOYEE" && <th>{t("app.actions")}</th>}
                  </tr>
                </thead>
                <tbody>
                  {orders.map((order) => (
                    <tr key={order.id} id={`admin-order-row-${order.id}`}>
                      <td>
                        <Link href={`/admin/orders/${order.id}`} style={{ color: "var(--color-accent-light)", fontFamily: "monospace" }}>
                          #{order.id.slice(0, 8).toUpperCase()}
                        </Link>
                      </td>
                      <td className="text-sm text-muted">{order.clientEmail}</td>
                      <td className="text-sm text-muted">{order.employeeEmail || "—"}</td>
                      <td style={{ fontWeight: 600 }}>${Number(order.totalPrice).toFixed(2)}</td>
                      <td>{order.totalItems}</td>
                      <td><span className={`badge status-${order.status}`}>{t(`orders.status.${order.status}` as never)}</span></td>
                      <td className="text-sm text-muted">{new Date(order.createdAt).toLocaleDateString()}</td>
                      {role === "EMPLOYEE" && (
                        <td>
                          <div style={{ display: "flex", gap: "6px" }}>
                            {order.status === "PENDING" && (
                              <button
                                className="btn btn-success btn-sm"
                                id={`accept-order-${order.id}`}
                                onClick={() => setConfirm({ id: order.id, type: "accept" })}
                                disabled={actionLoading === order.id}
                              >
                                {t("admin.orders.accept")}
                              </button>
                            )}
                            {order.status === "ACCEPTED" && (
                              <>
                                <button
                                  className="btn btn-primary btn-sm"
                                  id={`complete-order-${order.id}`}
                                  onClick={() => setConfirm({ id: order.id, type: "complete" })}
                                  disabled={actionLoading === order.id}
                                >
                                  {t("admin.orders.complete")}
                                </button>
                                <button
                                  className="btn btn-danger btn-sm"
                                  id={`cancel-order-${order.id}`}
                                  onClick={() => setConfirm({ id: order.id, type: "cancel" })}
                                  disabled={actionLoading === order.id}
                                >
                                  {t("admin.orders.cancel")}
                                </button>
                              </>
                            )}
                          </div>
                        </td>
                      )}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            {orders.length === 0 && (
              <div className="empty-state"><div className="empty-state-icon">📭</div><h3 className="empty-state-title">{t("app.noResults")}</h3></div>
            )}
            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>

      {confirm && (
        <ConfirmDialog
          message={t(`admin.orders.${confirm.type}` as never)}
          onConfirm={handleConfirm}
          onCancel={() => setConfirm(null)}
          isLoading={!!actionLoading}
          variant={confirm.type === "cancel" ? "danger" : "primary"}
        />
      )}
    </div>
  );
}
