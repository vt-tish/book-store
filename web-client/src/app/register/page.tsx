"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useLocale } from "@/common/hooks/useLocale";
import { registerUser } from "@/features/auth/api";
import { ApiError } from "@/common/types/api";
import { LanguageSwitcher } from "@/common/components/LanguageSwitcher";

export default function RegisterPage() {
  const { t, acceptLanguageHeader } = useLocale();
  const router = useRouter();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
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
      const form = document.getElementById("register-form") as HTMLFormElement;
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
      const res = await registerUser({ email, password }, acceptLanguageHeader);
      setSuccess(res.message);
    } catch (err) {
      const apiErr = err as ApiError;
      if (apiErr.validationErrors?.length) {
        const errs: Record<string, string> = {};
        for (const ve of apiErr.validationErrors) {
          errs[ve.field] = ve.message;
        }
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
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            marginBottom: "8px",
          }}
        >
          <div className="auth-logo">BookStore</div>
          <LanguageSwitcher />
        </div>

        <h1 className="auth-title">{t("auth.registerTitle")}</h1>
        <p className="auth-subtitle">{t("auth.registerSubtitle")}</p>

        {globalError && (
          <div
            className="badge badge-error"
            style={{ marginBottom: "16px", width: "100%", justifyContent: "flex-start", borderRadius: "8px", padding: "10px 14px" }}
          >
            {globalError}
          </div>
        )}

        {success ? (
          <div style={{ textAlign: "center", padding: "16px 0" }}>
            <div style={{ fontSize: "48px", marginBottom: "16px" }}>✉️</div>
            <div
              className="badge badge-success"
              style={{ padding: "12px 16px", borderRadius: "10px", width: "100%", justifyContent: "center", marginBottom: "16px" }}
            >
              {success}
            </div>
            <div style={{ display: "flex", gap: "8px", flexDirection: "column" }}>
              <Link href="/verify" className="btn btn-primary" id="go-verify-link">
                {t("auth.verify")}
              </Link>
              <Link href="/login" className="btn btn-ghost" id="go-login-link">
                {t("auth.login")}
              </Link>
            </div>
          </div>
        ) : (
          <form className="auth-form" onSubmit={handleSubmit} id="register-form" noValidate>
            <div className="form-group">
              <label className="form-label" htmlFor="register-email">
                {t("auth.email")}
              </label>
              <input
                id="register-email"
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

            <div className="form-group">
              <label className="form-label" htmlFor="register-password">
                {t("auth.password")}
              </label>
            <div style={{ position: "relative" }}>
              <input
                id="register-password"
                type={showPassword ? "text" : "password"}
                className={`form-input ${fieldErrors.password ? "error" : ""}`}
                placeholder={t("auth.passwordPlaceholder")}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                autoComplete="new-password"
                style={{ paddingRight: "40px" }}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                style={{
                  position: "absolute",
                  right: "12px",
                  top: "50%",
                  transform: "translateY(-50%)",
                  background: "none",
                  border: "none",
                  cursor: "pointer",
                  color: "var(--color-text-secondary)",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  padding: 0
                }}
                tabIndex={-1}
              >
                {showPassword ? (
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                    <line x1="1" y1="1" x2="23" y2="23"></line>
                  </svg>
                ) : (
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                    <circle cx="12" cy="12" r="3"></circle>
                  </svg>
                )}
              </button>
            </div>
              {fieldErrors.password && (
                <span className="form-error">{fieldErrors.password}</span>
              )}
            </div>

            <button
              id="register-submit"
              type="submit"
              className="btn btn-primary btn-lg"
              disabled={loading}
              style={{ width: "100%", marginTop: "8px" }}
            >
              {loading ? (
                <span className="spinner spinner-sm" />
              ) : (
                t("auth.register")
              )}
            </button>
          </form>
        )}

        <div className="auth-footer">
          {t("auth.hasAccount")}{" "}
          <Link href="/login" id="login-link">
            {t("auth.login")}
          </Link>
        </div>
      </div>
    </div>
  );
}
