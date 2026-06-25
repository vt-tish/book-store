"use client";

import React, { useState, Suspense } from "react";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { useLocale } from "@/common/hooks/useLocale";
import { resetPassword } from "@/features/auth/api";
import { ApiError } from "@/common/types/api";
import { LanguageSwitcher } from "@/common/components/LanguageSwitcher";

function ResetContent() {
  const { t, acceptLanguageHeader } = useLocale();
  const router = useRouter();
  const searchParams = useSearchParams();

  const [token, setToken] = useState(searchParams.get("token") ?? "");
  const [newPassword, setNewPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [globalError, setGlobalError] = useState("");
  const [success, setSuccess] = useState("");
  const [hasSubmitted, setHasSubmitted] = useState(false);

  const isInitialMount = React.useRef(true);
  React.useEffect(() => {
    if (isInitialMount.current) {
      isInitialMount.current = false;
      return;
    }
    if (hasSubmitted) {
      const form = document.getElementById("reset-password-form") as HTMLFormElement;
      if (form) form.requestSubmit();
    }
  }, [acceptLanguageHeader, hasSubmitted]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setHasSubmitted(true);
    setLoading(true);
    setFieldErrors({});
    setGlobalError("");

    try {
      const res = await resetPassword({ token, newPassword }, acceptLanguageHeader);
      setSuccess(res.message);
      setTimeout(() => router.replace("/login"), 2500);
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
      setLoading(false);
    }
  };

  return (
    <div className="auth-bg">
      <div className="auth-card">
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "8px" }}>
          <div className="auth-logo">BookStore</div>
          <LanguageSwitcher />
        </div>

        <div style={{ fontSize: "48px", textAlign: "center", marginBottom: "8px" }}>🔑</div>
        <h1 className="auth-title" style={{ textAlign: "center" }}>{t("auth.resetTitle")}</h1>
        <p className="auth-subtitle" style={{ textAlign: "center" }}>{t("auth.resetSubtitle")}</p>

        {globalError && (
          <div className="badge badge-error" style={{ marginBottom: "16px", width: "100%", justifyContent: "flex-start", borderRadius: "8px", padding: "10px 14px" }}>
            {globalError}
          </div>
        )}

        {success ? (
          <div style={{ textAlign: "center", padding: "16px 0" }}>
            <div className="badge badge-success" style={{ padding: "12px 16px", borderRadius: "10px", width: "100%", justifyContent: "center" }}>
              ✓ {success}
            </div>
            <p className="text-secondary text-sm mt-md">Redirecting to login...</p>
          </div>
        ) : (
          <form className="auth-form" onSubmit={handleSubmit} id="reset-password-form" noValidate>
            <div className="form-group">
              <label className="form-label" htmlFor="reset-token">{t("auth.resetToken")}</label>
              <input
                id="reset-token"
                type="text"
                className={`form-input ${fieldErrors.token ? "error" : ""}`}
                placeholder={t("auth.resetTokenPlaceholder")}
                value={token}
                onChange={(e) => setToken(e.target.value)}
              />
              {fieldErrors.token && (
                <span className="form-error">{fieldErrors.token}</span>
              )}
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="reset-password">{t("auth.newPassword")}</label>
              <input
                id="reset-password"
                type="password"
                className={`form-input ${fieldErrors.newPassword ? "error" : ""}`}
                placeholder={t("auth.newPasswordPlaceholder")}
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                autoComplete="new-password"
              />
              {fieldErrors.newPassword && (
                <span className="form-error">{fieldErrors.newPassword}</span>
              )}
            </div>

            <button
              id="reset-submit"
              type="submit"
              className="btn btn-primary btn-lg"
              disabled={loading}
              style={{ width: "100%" }}
            >
              {loading ? <span className="spinner spinner-sm" /> : t("auth.resetPassword")}
            </button>
          </form>
        )}

        <div className="auth-footer">
          <Link href="/login" id="back-login-link">{t("app.back")}</Link>
        </div>
      </div>
    </div>
  );
}

export default function ResetPasswordPage() {
  return (
    <Suspense fallback={<div className="auth-bg"><div className="spinner spinner-lg" /></div>}>
      <ResetContent />
    </Suspense>
  );
}
