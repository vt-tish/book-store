"use client";

import React from "react";
import { useLocale } from "@/common/hooks/useLocale";

interface ConfirmDialogProps {
  message: string;
  onConfirm: () => void;
  onCancel: () => void;
  isLoading?: boolean;
  variant?: "danger" | "primary";
}

export function ConfirmDialog({
  message,
  onConfirm,
  onCancel,
  isLoading,
  variant = "danger",
}: ConfirmDialogProps) {
  const { t } = useLocale();

  return (
    <div className="modal-overlay" onClick={onCancel}>
      <div
        className="modal-content"
        onClick={(e) => e.stopPropagation()}
        role="alertdialog"
        aria-modal="true"
      >
        <h3 className="modal-title">{t("app.confirm")}</h3>
        <p className="confirm-dialog-message">{message}</p>
        <div className="modal-footer">
          <button
            className="btn btn-ghost"
            onClick={onCancel}
            disabled={isLoading}
          >
            {t("app.cancel")}
          </button>
          <button
            className={`btn btn-${variant}`}
            onClick={onConfirm}
            disabled={isLoading}
          >
            {isLoading ? (
              <span className="spinner spinner-sm" />
            ) : (
              t("app.confirm")
            )}
          </button>
        </div>
      </div>
    </div>
  );
}

interface LoadingSpinnerProps {
  size?: "sm" | "md" | "lg";
  label?: string;
}

export function LoadingSpinner({ size = "md", label }: LoadingSpinnerProps) {
  const { t } = useLocale();
  return (
    <div className="loading-center">
      <div className={`spinner ${size === "lg" ? "spinner-lg" : size === "sm" ? "spinner-sm" : ""}`} />
      {label && <span className="text-secondary text-sm">{label}</span>}
    </div>
  );
}

interface ErrorMessageProps {
  message: string;
  onRetry?: () => void;
}

export function ErrorMessage({ message, onRetry }: ErrorMessageProps) {
  const { t } = useLocale();
  return (
    <div className="empty-state">
      <h3 className="empty-state-title text-error">{t("app.error")}</h3>
      <p className="text-muted text-sm mt-md">{message}</p>
      {onRetry && (
        <button
          className="btn btn-ghost mt-md"
          onClick={onRetry}
        >
          {t("app.retry")}
        </button>
      )}
    </div>
  );
}
