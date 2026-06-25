"use client";

import React from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";
import { useLocale } from "@/common/hooks/useLocale";
import { LanguageSwitcher } from "./LanguageSwitcher";
import { useToast } from "./Toast";

export function Navbar() {
  const { isAuthenticated, role, logout } = useAuth();
  const { t } = useLocale();
  const pathname = usePathname();
  const { showToast } = useToast();

  const isActive = (path: string) =>
    pathname === path || pathname.startsWith(path + "/");

  const handleLogout = async () => {
    await logout();
    showToast(t("auth.logout" as never) || "Logged out", "info");
  };

  if (!isAuthenticated) return null;

  return (
    <nav className="navbar">
      <div className="container navbar-inner">
        <Link href="/books" className="navbar-logo" id="nav-logo">
          {t("app.title")}
        </Link>

        <div className="navbar-links">
          {/* Client links */}
          {role === "CLIENT" && (
            <>
              <Link
                href="/books"
                className={`nav-link ${isActive("/books") ? "active" : ""}`}
                id="nav-books"
              >
                {t("nav.books")}
              </Link>
              <Link
                href="/cart"
                className={`nav-link ${isActive("/cart") ? "active" : ""}`}
                id="nav-cart"
              >
                {t("nav.cart")}
              </Link>
              <Link
                href="/orders"
                className={`nav-link ${isActive("/orders") ? "active" : ""}`}
                id="nav-orders"
              >
                {t("nav.orders")}
              </Link>
            </>
          )}

          {/* Employee links */}
          {(role === "EMPLOYEE" || role === "ADMIN") && (
            <>
              <Link
                href="/admin/books"
                className={`nav-link ${isActive("/admin/books") ? "active" : ""}`}
                id="nav-admin-books"
              >
                {t("nav.books")}
              </Link>
              <Link
                href="/admin/orders"
                className={`nav-link ${isActive("/admin/orders") ? "active" : ""}`}
                id="nav-admin-orders"
              >
                {t("nav.orders")}
              </Link>
              <Link
                href="/admin/clients"
                className={`nav-link ${isActive("/admin/clients") ? "active" : ""}`}
                id="nav-admin-clients"
              >
                {t("nav.admin")}
              </Link>
            </>
          )}

          {/* Admin-only */}
          {role === "ADMIN" && (
            <Link
              href="/admin/employees"
              className={`nav-link ${isActive("/admin/employees") ? "active" : ""}`}
              id="nav-admin-employees"
            >
              {t("admin.employees")}
            </Link>
          )}

          <LanguageSwitcher />

          <button
            className="btn btn-ghost btn-sm"
            onClick={handleLogout}
            id="nav-logout"
          >
            {t("nav.logout")}
          </button>
        </div>
      </div>
    </nav>
  );
}
