"use client";

import React, { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";
import { useLocale } from "@/common/hooks/useLocale";
import { useToast } from "@/common/components/Toast";
import { loginUser } from "@/features/auth/api";
import { ApiError } from "@/common/types/api";
import { LanguageSwitcher } from "@/common/components/LanguageSwitcher";

export default function LoginPage() {
  const { login } = useAuth();
  const { t, acceptLanguageHeader } = useLocale();
  const { showToast } = useToast();
  const router = useRouter();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [globalError, setGlobalError] = useState("");
  const [hasSubmitted, setHasSubmitted] = useState(false);

  const isInitialMount = React.useRef(true);
  React.useEffect(() => {
    if (isInitialMount.current) {
      isInitialMount.current = false;
      return;
    }
    if (hasSubmitted) {
      const form = document.getElementById("login-form") as HTMLFormElement;
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
      const res = await loginUser({ email, password }, acceptLanguageHeader);
      login(res.accessToken);
      router.replace("/");
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

        <h1 className="auth-title">{t("auth.loginTitle")}</h1>
        <p className="auth-subtitle">{t("auth.loginSubtitle")}</p>

        {globalError && (
          <div
            className="badge badge-error"
            style={{ marginBottom: "16px", width: "100%", justifyContent: "flex-start", borderRadius: "8px", padding: "10px 14px" }}
          >
            {globalError}
          </div>
        )}

        <form className="auth-form" onSubmit={handleSubmit} id="login-form" noValidate>
          <div className="form-group">
            <label className="form-label" htmlFor="login-email">
              {t("auth.email")}
            </label>
            <input
              id="login-email"
              type="email"
              className={`form-input ${fieldErrors.email ? "error" : ""}`}
              placeholder={t("auth.emailPlaceholder")}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="email"
              required
            />
            {fieldErrors.email && (
              <span className="form-error">{fieldErrors.email}</span>
            )}
          </div>

          <div className="form-group">
            <label className="form-label" htmlFor="login-password">
              {t("auth.password")}
            </label>
            <div style={{ position: "relative" }}>
              <input
                id="login-password"
                type={showPassword ? "text" : "password"}
                className={`form-input ${fieldErrors.password ? "error" : ""}`}
                placeholder={t("auth.passwordPlaceholder")}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                autoComplete="current-password"
                required
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

          <div style={{ textAlign: "right" }}>
            <Link
              href="/forgot-password"
              style={{ fontSize: "13px", color: "var(--color-accent-light)" }}
            >
              {t("auth.forgotPassword")}
            </Link>
          </div>

          <button
            id="login-submit"
            type="submit"
            className="btn btn-primary btn-lg"
            disabled={loading}
            style={{ width: "100%", marginTop: "8px" }}
          >
            {loading ? <span className="spinner spinner-sm" /> : t("auth.login")}
          </button>
        </form>

        <div className="auth-footer">
          {t("auth.noAccount")}{" "}
          <Link href="/register" id="register-link">
            {t("auth.register")}
          </Link>
        </div>

        <div className="auth-footer" style={{ marginTop: "8px" }}>
          {t("auth.resendNote")}{" "}
          <Link href="/resend-verification" id="resend-verification-link">
            {t("auth.resendLink")}
          </Link>
        </div>
      </div>
    </div>
  );
}
