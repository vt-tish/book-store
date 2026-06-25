"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useLocale } from "@/common/hooks/useLocale";
import { forgotPassword } from "@/features/auth/api";
import { ApiError } from "@/common/types/api";
import { LanguageSwitcher } from "@/common/components/LanguageSwitcher";

export default function ForgotPasswordPage() {
  const { t, acceptLanguageHeader } = useLocale();
  const [email, setEmail] = useState("");
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
      const form = document.getElementById("forgot-password-form") as HTMLFormElement;
      if (form) form.requestSubmit();
    }
  }, [acceptLanguageHeader, hasSubmitted]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setHasSubmitted(true);
    setLoading(true);
    setFieldErrors({});
    setGlobalError("");
    setSuccess("");

    try {
      const res = await forgotPassword({ email }, acceptLanguageHeader);
      setSuccess(res.message);
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

        <div style={{ fontSize: "48px", textAlign: "center", marginBottom: "8px" }}>🔐</div>
        <h1 className="auth-title" style={{ textAlign: "center" }}>{t("auth.forgotTitle")}</h1>
        <p className="auth-subtitle" style={{ textAlign: "center" }}>{t("auth.forgotSubtitle")}</p>

          <div className="badge badge-error" style={{ marginBottom: "16px", width: "100%", justifyContent: "flex-start", borderRadius: "8px", padding: "10px 14px" }}>
            {globalError}
          </div>
        )}

        {success ? (
          <div style={{ textAlign: "center", padding: "16px 0" }}>
            <div className="badge badge-success" style={{ padding: "12px 16px", borderRadius: "10px", width: "100%", justifyContent: "center", marginBottom: "16px" }}>
              ✓ {success}
            </div>
            <Link href="/reset-password" className="btn btn-primary" style={{ display: "block" }} id="go-reset-link">
              {t("auth.resetPassword")}
            </Link>
          </div>
        ) : (
          <form className="auth-form" onSubmit={handleSubmit} id="forgot-password-form" noValidate>
            <div className="form-group">
              <label className="form-label" htmlFor="forgot-email">{t("auth.email")}</label>
              <input
                id="forgot-email"
                type="email"
                className={`form-input ${fieldErrors.email ? "error" : ""}`}
                placeholder={t("auth.emailPlaceholder")}
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                autoComplete="email"
              />
              {fieldErrors.email && (
                <span className="form-error">{fieldErrors.email}</span>
              )}
            </div>

            <button
              id="forgot-submit"
              type="submit"
              className="btn btn-primary btn-lg"
              disabled={loading}
              style={{ width: "100%" }}
            >
              {loading ? <span className="spinner spinner-sm" /> : t("auth.sendReset")}
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
