"use client";

import React, { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";
import { useLocale } from "@/common/hooks/useLocale";
import { Pagination } from "@/common/components/Pagination";
import { LoadingSpinner, ErrorMessage } from "@/common/components/Shared";
import { getMyOrders } from "@/features/orders/api";
import { OrderCardResponseDto, OrderStatus, ApiError } from "@/common/types/api";

const PAGE_SIZE = 10;

function StatusBadge({ status }: { status: OrderStatus }) {
  const { t } = useLocale();
  return (
    <span className={`badge status-${status}`}>
      {t(`orders.status.${status}` as never)}
    </span>
  );
}

export default function OrdersPage() {
  const { isAuthenticated, isLoading: authLoading, fetchWithAuth } = useAuth();
  const { t } = useLocale();
  const router = useRouter();

  const [orders, setOrders] = useState<OrderCardResponseDto[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadOrders = useCallback(async () => {
    if (!isAuthenticated) return;
    setLoading(true);
    setError("");
    try {
      const data = await getMyOrders(page, PAGE_SIZE, fetchWithAuth);
      setOrders(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message ?? t("app.error"));
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated, page, fetchWithAuth]);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) router.replace("/login");
  }, [authLoading, isAuthenticated, router]);

  useEffect(() => {
    if (isAuthenticated) loadOrders();
  }, [loadOrders, isAuthenticated]);

  if (authLoading || loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message={error} onRetry={loadOrders} />;

  return (
    <div className="container page-wrapper page-enter">
      <div className="page-header">
        <h1 className="page-title">{t("orders.title")}</h1>
      </div>

      {orders.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">📦</div>
          <h3 className="empty-state-title">{t("orders.noOrders")}</h3>
          <button className="btn btn-primary mt-md" onClick={() => router.push("/books")} id="browse-books-btn">
            {t("nav.books")}
          </button>
        </div>
      ) : (
        <>
          <div style={{ display: "flex", flexDirection: "column", gap: "var(--space-md)" }}>
            {orders.map((order) => (
              <Link
                key={order.id}
                href={`/orders/${order.id}`}
                style={{ textDecoration: "none" }}
                id={`order-card-${order.id}`}
              >
                <div className="card" style={{ cursor: "pointer" }}>
                  <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", flexWrap: "wrap", gap: "12px" }}>
                    <div>
                      <div style={{ fontWeight: 600, marginBottom: "4px" }}>
                        #{order.id.slice(0, 8).toUpperCase()}
                      </div>
                      <div className="text-sm text-muted">
                        {t("orders.created")}: {new Date(order.createdAt).toLocaleDateString()}
                      </div>
                      <div className="text-sm text-muted">
                        {t("orders.items")}: {order.totalItems}
                      </div>
                    </div>
                    <div style={{ textAlign: "right" }}>
                      <StatusBadge status={order.status} />
                      <div style={{ fontSize: "var(--font-size-xl)", fontWeight: 700, color: "var(--color-accent-light)", marginTop: "8px" }}>
                        ${Number(order.totalPrice).toFixed(2)}
                      </div>
                    </div>
                  </div>
                </div>
              </Link>
            ))}
          </div>

          <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
        </>
      )}
    </div>
  );
}
