"use client";

import React, { useState, useEffect, Suspense } from "react";
import Link from "next/link";
import { useRouter, useSearchParams } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";
import { useLocale } from "@/common/hooks/useLocale";
import { verifyUser } from "@/features/auth/api";
import { ApiError } from "@/common/types/api";
import { LanguageSwitcher } from "@/common/components/LanguageSwitcher";

function VerifyContent() {
  const { login } = useAuth();
  const { t, acceptLanguageHeader } = useLocale();
  const router = useRouter();
  const searchParams = useSearchParams();

  const [token, setToken] = useState(searchParams.get("token") ?? "");
  const [loading, setLoading] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [globalError, setGlobalError] = useState("");
  const [hasSubmitted, setHasSubmitted] = useState(false);

  const isInitialMount = React.useRef(true);
  useEffect(() => {
    if (isInitialMount.current) {
      isInitialMount.current = false;
      return;
    }
    if (hasSubmitted) {
      const form = document.getElementById("verify-form") as HTMLFormElement;
      if (form) form.requestSubmit();
    }
  }, [acceptLanguageHeader, hasSubmitted]);

  // Auto-submit if token is provided via URL
  useEffect(() => {
    const urlToken = searchParams.get("token");
    if (urlToken) {
      setToken(urlToken);
      handleVerify(urlToken);
    }
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const handleVerify = async (tkn?: string) => {
    const t_ = tkn ?? token;
    if (!t_) return;

    setLoading(true);
    setFieldErrors({});
    setGlobalError("");

    try {
      const res = await verifyUser({ token: t_ }, acceptLanguageHeader);
      login(res.accessToken);
      router.replace("/");
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

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setHasSubmitted(true);
    handleVerify();
  };

  return (
    <div className="auth-bg">
      <div className="auth-card">
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "8px" }}>
          <div className="auth-logo">BookStore</div>
          <LanguageSwitcher />
        </div>

        <div style={{ fontSize: "48px", textAlign: "center", marginBottom: "8px" }}>✉️</div>
        <h1 className="auth-title" style={{ textAlign: "center" }}>{t("auth.verifyTitle")}</h1>
        <p className="auth-subtitle" style={{ textAlign: "center" }}>{t("auth.verifyNote")}</p>

        {globalError && (
          <div className="badge badge-error" style={{ marginBottom: "16px", width: "100%", justifyContent: "flex-start", borderRadius: "8px", padding: "10px 14px" }}>
            {globalError}
          </div>
        )}

        <form className="auth-form" onSubmit={handleSubmit} id="verify-form" noValidate>
          <div className="form-group">
            <label className="form-label" htmlFor="verify-token">{t("auth.token")}</label>
            <input
              id="verify-token"
              type="text"
              className={`form-input ${fieldErrors.token ? "error" : ""}`}
              placeholder={t("auth.tokenPlaceholder")}
              value={token}
              onChange={(e) => setToken(e.target.value)}
            />
            {fieldErrors.token && (
              <span className="form-error">{fieldErrors.token}</span>
            )}
          </div>

          <button
            id="verify-submit"
            type="submit"
            className="btn btn-primary btn-lg"
            disabled={loading}
            style={{ width: "100%" }}
          >
            {loading ? <span className="spinner spinner-sm" /> : t("auth.verify")}
          </button>
        </form>

        <div className="auth-footer">
          <Link href="/login" id="back-login-link">{t("app.back")}</Link>
        </div>
      </div>
    </div>
  );
}

export default function VerifyPage() {
  return (
    <Suspense fallback={<div className="auth-bg"><div className="spinner spinner-lg" /></div>}>
      <VerifyContent />
    </Suspense>
  );
}
