"use client";

import React, { useState, useEffect, useCallback } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";
import { useLocale } from "@/common/hooks/useLocale";
import { useToast } from "@/common/components/Toast";
import { LoadingSpinner, ErrorMessage, ConfirmDialog } from "@/common/components/Shared";
import { getCart, addCartItem, updateCartItem, removeCartItem, clearCart } from "@/features/cart/api";
import { submitOrder } from "@/features/orders/api";
import { CartResponseDto, CartItemDto, ApiError } from "@/common/types/api";

export default function CartPage() {
  const { isAuthenticated, isLoading: authLoading, fetchWithAuth } = useAuth();
  const { t, locale, acceptLanguageHeader } = useLocale();
  const { showToast } = useToast();
  const router = useRouter();

  const [cart, setCart] = useState<CartResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionLoading, setActionLoading] = useState<string | null>(null);
  const [showClearConfirm, setShowClearConfirm] = useState(false);
  const [showOrderConfirm, setShowOrderConfirm] = useState(false);

  const loadCart = useCallback(async () => {
    if (!isAuthenticated) return;
    setLoading(true);
    setError("");
    try {
      const data = await getCart(fetchWithAuth, acceptLanguageHeader);
      setCart(data);
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message ?? t("app.error"));
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated, fetchWithAuth, acceptLanguageHeader, locale]);

  useEffect(() => {
    if (!authLoading && !isAuthenticated) router.replace("/login");
  }, [authLoading, isAuthenticated, router]);

  useEffect(() => {
    if (isAuthenticated) loadCart();
  }, [loadCart, isAuthenticated]);

  const handleUpdateQuantity = async (item: CartItemDto, qty: number) => {
    if (qty < 1) return;
    setActionLoading(item.id);
    try {
      const updated = await updateCartItem(item.id, { quantity: qty }, fetchWithAuth, acceptLanguageHeader);
      setCart(updated);
    } catch (err) {
      const apiErr = err as ApiError;
      showToast(apiErr.message ?? t("app.error"), "error");
    } finally {
      setActionLoading(null);
    }
  };

  const handleRemove = async (itemId: string) => {
    setActionLoading(itemId);
    try {
      const updated = await removeCartItem(itemId, fetchWithAuth, acceptLanguageHeader);
      setCart(updated);
      showToast(t("cart.remove"), "info");
    } catch (err) {
      const apiErr = err as ApiError;
      showToast(apiErr.message ?? t("app.error"), "error");
    } finally {
      setActionLoading(null);
    }
  };

  const handleClear = async () => {
    setShowClearConfirm(false);
    setActionLoading("clear");
    try {
      await clearCart(fetchWithAuth);
      setCart({ cartItems: [], totalPrice: 0 });
      showToast(t("cart.clear"), "info");
    } catch (err) {
      const apiErr = err as ApiError;
      showToast(apiErr.message ?? t("app.error"), "error");
    } finally {
      setActionLoading(null);
    }
  };

  const handleOrder = async () => {
    setShowOrderConfirm(false);
    setActionLoading("order");
    try {
      const order = await submitOrder(fetchWithAuth, acceptLanguageHeader);
      showToast(t("cart.checkout"), "success");
      router.push(`/orders/${order.id}`);
    } catch (err) {
      const apiErr = err as ApiError;
      showToast(apiErr.message ?? t("app.error"), "error");
    } finally {
      setActionLoading(null);
    }
  };

  if (authLoading || loading) return <LoadingSpinner />;
  if (error) return <ErrorMessage message={error} onRetry={loadCart} />;

  const isEmpty = !cart || cart.cartItems.length === 0;

  return (
    <div className="container page-wrapper page-enter">
      <div className="page-header">
        <h1 className="page-title">{t("cart.title")}</h1>
        {!isEmpty && (
          <button
            id="clear-cart-btn"
            className="btn btn-danger btn-sm"
            onClick={() => setShowClearConfirm(true)}
            disabled={actionLoading === "clear"}
          >
            {actionLoading === "clear" ? <span className="spinner spinner-sm" /> : t("cart.clear")}
          </button>
        )}
      </div>

      {isEmpty ? (
        <div className="empty-state">
          <div className="empty-state-icon">🛒</div>
          <h3 className="empty-state-title">{t("cart.empty")}</h3>
          <button className="btn btn-primary mt-md" onClick={() => router.push("/books")} id="browse-books-btn">
            {t("nav.books")}
          </button>
        </div>
      ) : (
        <div style={{ display: "grid", gridTemplateColumns: "1fr 320px", gap: "var(--space-xl)", alignItems: "start" }}>
          {/* Cart items */}
          <div style={{ display: "flex", flexDirection: "column", gap: "var(--space-md)" }}>
            {cart.cartItems.map((item) => (
              <div key={item.id} className="card" id={`cart-item-${item.id}`}>
                <div style={{ display: "flex", gap: "var(--space-md)", alignItems: "center" }}>
                  {item.previewUrl && (
                    <img
                      src={item.previewUrl}
                      alt={item.bookName}
                      style={{ width: "60px", height: "80px", objectFit: "cover", borderRadius: "6px", flexShrink: 0 }}
                      onError={(e) => { (e.target as HTMLImageElement).style.display = "none"; }}
                    />
                  )}
                  <div style={{ flex: 1 }}>
                    <div style={{ fontWeight: 600, marginBottom: "4px" }}>{item.bookName}</div>
                    <div className="text-sm text-muted">${Number(item.pricePerUnit).toFixed(2)} {t("cart.perUnit")}</div>
                    {!item.isAvailable && (
                      <span className="badge badge-error" style={{ marginTop: "4px" }}>{t("books.unavailable")}</span>
                    )}
                  </div>
                  {/* Quantity controls */}
                  <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                    <button
                      className="btn btn-ghost btn-sm btn-icon"
                      id={`cart-item-decrement-${item.id}`}
                      onClick={() => handleUpdateQuantity(item, item.quantity - 1)}
                      disabled={!!actionLoading || item.quantity <= 1}
                    >
                      −
                    </button>
                    <span style={{ minWidth: "24px", textAlign: "center", fontWeight: 600 }}>
                      {item.quantity}
                    </span>
                    <button
                      className="btn btn-ghost btn-sm btn-icon"
                      id={`cart-item-increment-${item.id}`}
                      onClick={() => handleUpdateQuantity(item, item.quantity + 1)}
                      disabled={!!actionLoading}
                    >
                      +
                    </button>
                  </div>
                  <div style={{ minWidth: "80px", textAlign: "right", fontWeight: 700, color: "var(--color-accent-light)" }}>
                    ${Number(item.subtotalPrice).toFixed(2)}
                  </div>
                  <button
                    className="btn btn-danger btn-sm btn-icon"
                    id={`cart-item-remove-${item.id}`}
                    onClick={() => handleRemove(item.id)}
                    disabled={actionLoading === item.id}
                    title={t("cart.remove")}
                  >
                    {actionLoading === item.id ? <span className="spinner spinner-sm" /> : "✕"}
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Order summary */}
          <div className="card" style={{ position: "sticky", top: "80px" }}>
            <h2 style={{ fontWeight: 700, marginBottom: "16px" }}>{t("cart.total")}</h2>
            <div style={{ display: "flex", justifyContent: "space-between", marginBottom: "8px", color: "var(--color-text-secondary)", fontSize: "14px" }}>
              <span>{t("cart.quantity")} ({cart.cartItems.length})</span>
              <span>${Number(cart.totalPrice).toFixed(2)}</span>
            </div>
            <hr className="divider" />
            <div style={{ display: "flex", justifyContent: "space-between", fontWeight: 700, fontSize: "20px", marginBottom: "20px" }}>
              <span>{t("cart.total")}</span>
              <span style={{ color: "var(--color-accent-light)" }}>${Number(cart.totalPrice).toFixed(2)}</span>
            </div>
            <button
              id="checkout-btn"
              className="btn btn-primary btn-lg"
              style={{ width: "100%" }}
              onClick={() => setShowOrderConfirm(true)}
              disabled={!!actionLoading || cart.cartItems.some(i => !i.isAvailable)}
            >
              {actionLoading === "order" ? <span className="spinner spinner-sm" /> : <>🛍 {t("cart.checkout")}</>}
            </button>
            {cart.cartItems.some(i => !i.isAvailable) && (
              <p className="text-error text-xs mt-sm">{t("books.unavailable")}</p>
            )}
          </div>
        </div>
      )}

      {showClearConfirm && (
        <ConfirmDialog
          message={t("cart.confirmClear")}
          onConfirm={handleClear}
          onCancel={() => setShowClearConfirm(false)}
          isLoading={actionLoading === "clear"}
        />
      )}

      {showOrderConfirm && (
        <ConfirmDialog
          message={t("cart.confirmOrder")}
          onConfirm={handleOrder}
          onCancel={() => setShowOrderConfirm(false)}
          isLoading={actionLoading === "order"}
          variant="primary"
        />
      )}
    </div>
  );
}
