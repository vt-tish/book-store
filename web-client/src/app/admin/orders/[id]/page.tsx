"use client";

import React, { useState, useEffect, use } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";
import { useLocale } from "@/common/hooks/useLocale";
import { LoadingSpinner, ErrorMessage } from "@/common/components/Shared";
import { getOrderByIdAdmin } from "@/features/orders/api";
import { AdminOrderDetailsResponseDto, OrderStatus, ApiError } from "@/common/types/api";

interface Props {
  params: Promise<{ id: string }>;
}

function StatusBadge({ status }: { status: OrderStatus }) {
  const { t } = useLocale();
  return (
    <span className={`badge status-${status}`} style={{ fontSize: "14px", padding: "4px 14px" }}>
      {t(`orders.status.${status}` as never)}
    </span>
  );
}

export default function AdminOrderDetailPage({ params }: Props) {
  const { id } = use(params);
  const { isAuthenticated, isLoading: authLoading, fetchWithAuth, role } = useAuth();
  const { t, locale, acceptLanguageHeader } = useLocale();
  const router = useRouter();

  const [order, setOrder] = useState<AdminOrderDetailsResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!authLoading && (!isAuthenticated || (role !== "ADMIN" && role !== "EMPLOYEE"))) {
      router.replace("/login");
    }
  }, [authLoading, isAuthenticated, role, router]);

  useEffect(() => {
    if (!isAuthenticated) return;
    setLoading(true);
    setError("");
    getOrderByIdAdmin(id, fetchWithAuth, acceptLanguageHeader)
      .then(setOrder)
      .catch((err: ApiError) => setError(err.message ?? t("app.error")))
      .finally(() => setLoading(false));
  }, [id, isAuthenticated, locale, acceptLanguageHeader]);

  if (authLoading || loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message={error} />;
  if (!order) return null;

  return (
    <div className="admin-layout">
      <div className="admin-content">
        <Link href="/admin/orders" className="btn btn-ghost btn-sm" style={{ marginBottom: "24px", display: "inline-flex" }} id="back-to-orders">
          ← {t("orders.back")}
        </Link>

        <div style={{ display: "grid", gridTemplateColumns: "1fr 320px", gap: "var(--space-xl)", alignItems: "start" }}>
          {/* Order items */}
          <div>
            <div className="page-header" style={{ marginBottom: "var(--space-md)" }}>
              <div>
                <h1 className="page-title" style={{ fontSize: "var(--font-size-2xl)" }}>
                  #{order.id.slice(0, 8).toUpperCase()}
                </h1>
                <div className="text-sm text-muted" style={{ marginTop: "4px" }}>
                  {t("orders.created")}: {new Date(order.createdAt).toLocaleString()}
                </div>
              </div>
              <StatusBadge status={order.status} />
            </div>

            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>{t("orders.book")}</th>
                    <th>{t("orders.author")}</th>
                    <th>{t("orders.pricePerUnit")}</th>
                    <th>{t("orders.quantity")}</th>
                    <th>{t("orders.subtotal")}</th>
                  </tr>
                </thead>
                <tbody>
                  {order.items.map((item) => (
                    <tr key={item.id} id={`order-item-${item.id}`}>
                      <td style={{ fontWeight: 500 }}>{item.bookName}</td>
                      <td className="text-muted">{item.bookAuthor}</td>
                      <td>${Number(item.pricePerUnit).toFixed(2)}</td>
                      <td>{item.quantity}</td>
                      <td style={{ fontWeight: 600, color: "var(--color-accent-light)" }}>
                        ${Number(item.subtotalPrice).toFixed(2)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Summary card */}
          <div className="card" style={{ position: "sticky", top: "80px" }}>
            <h2 style={{ fontWeight: 700, marginBottom: "16px" }}>{t("orders.total")}</h2>
            <div className="divider" />
            
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "12px" }}>
              <span className="text-muted">{t("admin.orders.client")}:</span>
              <span className="text-sm">{order.clientEmail}</span>
            </div>
            {order.employeeEmail && (
              <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "16px" }}>
                <span className="text-muted">{t("admin.orders.employee")}:</span>
                <span className="text-sm">{order.employeeEmail}</span>
              </div>
            )}
            
            <div className="divider" />

            <div style={{ display: "flex", justifyContent: "space-between", fontWeight: 700, fontSize: "24px", color: "var(--color-accent-light)" }}>
              <span>{t("orders.total")}</span>
              <span>${Number(order.totalPrice).toFixed(2)}</span>
            </div>

            {order.closedAt && (
              <div className="text-sm text-muted mt-md">
                {t("orders.closed")}: {new Date(order.closedAt).toLocaleString()}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
