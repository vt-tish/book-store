"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useLocale } from "@/common/hooks/useLocale";
import { resendVerification } from "@/features/auth/api";
import { ApiError } from "@/common/types/api";
import { LanguageSwitcher } from "@/common/components/LanguageSwitcher";

export default function ResendVerificationPage() {
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
      const form = document.getElementById("resend-verification-form") as HTMLFormElement;
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
      const res = await resendVerification({ email }, acceptLanguageHeader);
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

        <h1 className="auth-title">{t("auth.resendVerification")}</h1>
        <p className="auth-subtitle">{t("auth.forgotSubtitle")}</p>

        {globalError && (
          <div className="badge badge-error" style={{ marginBottom: "16px", width: "100%", justifyContent: "flex-start", borderRadius: "8px", padding: "10px 14px" }}>
            {globalError}
          </div>
        )}

        {success && (
          <div className="badge badge-success" style={{ marginBottom: "16px", width: "100%", justifyContent: "flex-start", borderRadius: "8px", padding: "10px 14px" }}>
            ✓ {success}
          </div>
        )}

        <form className="auth-form" onSubmit={handleSubmit} id="resend-verification-form" noValidate>
          <div className="form-group">
            <label className="form-label" htmlFor="resend-email">{t("auth.email")}</label>
            <input
              id="resend-email"
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
            id="resend-submit"
            type="submit"
            className="btn btn-primary btn-lg"
            disabled={loading}
            style={{ width: "100%" }}
          >
            {loading ? <span className="spinner spinner-sm" /> : t("auth.resendVerification")}
          </button>
        </form>

        <div className="auth-footer">
          <Link href="/login" id="back-login-link">{t("auth.hasAccount")} {t("auth.login")}</Link>
        </div>
      </div>
    </div>
  );
}
