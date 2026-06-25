"use client";

import React, { useState, useEffect, useCallback } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";
import { useLocale } from "@/common/hooks/useLocale";
import { useToast } from "@/common/components/Toast";
import { Pagination } from "@/common/components/Pagination";
import { LoadingSpinner, ErrorMessage, ConfirmDialog } from "@/common/components/Shared";
import { getAllEmployees, blockEmployee, unblockEmployee, registerEmployee } from "@/features/admin/api";
import { resendVerification } from "@/features/auth/api";
import { AdminEmployeeResponseDto, ApiError } from "@/common/types/api";

const PAGE_SIZE = 15;

export default function AdminEmployeesPage() {
  const { isAuthenticated, isLoading: authLoading, fetchWithAuth, role } = useAuth();
  const { t, language } = useLocale();
  const { showToast } = useToast();
  const router = useRouter();

  const [employees, setEmployees] = useState<AdminEmployeeResponseDto[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [sort, setSort] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionLoading, setActionLoading] = useState<string | null>(null);
  const [confirm, setConfirm] = useState<{ id: string; type: "block" | "unblock" } | null>(null);

  const [isRegisterModalOpen, setIsRegisterModalOpen] = useState(false);
  const [registerForm, setRegisterForm] = useState({ email: "", password: "", phone: "", birthDate: "" });
  const [showPassword, setShowPassword] = useState(false);
  const [registerLoading, setRegisterLoading] = useState(false);
  const [registerError, setRegisterError] = useState("");
  const [registerSuccess, setRegisterSuccess] = useState("");
  const [resendLoading, setResendLoading] = useState(false);
  const [resendSuccess, setResendSuccess] = useState("");
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const loadEmployees = useCallback(async () => {
    if (!isAuthenticated) return;
    setLoading(true);
    setError("");
    try {
      const data = await getAllEmployees(page, PAGE_SIZE, fetchWithAuth, sort);
      setEmployees(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message ?? t("app.error"));
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated, page, fetchWithAuth, t, sort]);

  useEffect(() => {
    if (!authLoading && (!isAuthenticated || role !== "ADMIN")) {
      router.replace("/login");
    }
  }, [authLoading, isAuthenticated, role, router]);

  useEffect(() => {
    if (isAuthenticated) loadEmployees();
  }, [loadEmployees, isAuthenticated]);

  const handleConfirm = async () => {
    if (!confirm) return;
    setActionLoading(confirm.id);
    const prevConfirm = confirm;
    setConfirm(null);
    try {
      if (prevConfirm.type === "block") await blockEmployee(prevConfirm.id, fetchWithAuth);
      else await unblockEmployee(prevConfirm.id, fetchWithAuth);
      showToast(t(prevConfirm.type === "block" ? "admin.employees.block" : "admin.employees.unblock"), "success");
      loadEmployees();
    } catch (err) {
      const apiErr = err as ApiError;
      showToast(apiErr.message ?? t("app.error"), "error");
    } finally {
      setActionLoading(null);
    }
  };

  const handleRegisterSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setRegisterLoading(true);
    setRegisterError("");
    setRegisterSuccess("");
    setFieldErrors({});
    try {
      const res = await registerEmployee(registerForm, fetchWithAuth, language);
      setRegisterSuccess(res.message);
      loadEmployees();
    } catch (err) {
      const apiErr = err as ApiError;
      if (apiErr.validationErrors?.length) {
        const errs: Record<string, string> = {};
        for (const ve of apiErr.validationErrors) errs[ve.field] = ve.message;
        setFieldErrors(errs);
        setRegisterError(apiErr.message ?? t("app.error"));
      } else {
        setRegisterError(apiErr.message ?? t("app.error"));
      }
    } finally {
      setRegisterLoading(false);
    }
  };

  const handleResendVerification = async () => {
    setResendLoading(true);
    setResendSuccess("");
    setRegisterError("");
    try {
      const res = await resendVerification({ email: registerForm.email }, language);
      setResendSuccess(res.message);
    } catch (err) {
      const apiErr = err as ApiError;
      setRegisterError(apiErr.message ?? t("app.error"));
    } finally {
      setResendLoading(false);
    }
  };

  const closeRegisterModal = () => {
    setIsRegisterModalOpen(false);
    setRegisterSuccess("");
    setResendSuccess("");
    setRegisterError("");
    setFieldErrors({});
    setRegisterForm({ email: "", password: "", phone: "", birthDate: "" });
    setShowPassword(false);
  };

  if (authLoading) return <LoadingSpinner />;

  return (
    <div className="admin-layout">


      <div className="admin-content">
        <div className="page-header" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <h1 className="page-title">{t("admin.employees.title")}</h1>
          <button className="btn btn-primary" onClick={() => setIsRegisterModalOpen(true)}>
            {t("admin.employees.register")}
          </button>
        </div>

        {loading ? <LoadingSpinner /> : error ? <ErrorMessage message={error} onRetry={loadEmployees} /> : (
          <>
            <div className="filter-bar">
              <div className="form-group" style={{ maxWidth: "250px" }}>
                <label className="form-label">{t("app.sortBy")}</label>
                <select className="form-select" value={sort} onChange={(e) => setSort(e.target.value)}>
                  <option value="">{t("app.sort.default")}</option>
                  <option value="user.isBlocked,asc">{t("app.sort.statusAsc")}</option>
                  <option value="user.isBlocked,desc">{t("app.sort.statusDesc")}</option>
                </select>
              </div>
            </div>
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr>
                    <th>{t("auth.email")}</th>
                    <th>{t("admin.employees.phone")}</th>
                    <th>{t("admin.employees.birthDate")}</th>
                    <th>{t("app.status")}</th>
                    <th>{t("app.actions")}</th>
                  </tr>
                </thead>
                <tbody>
                  {employees.map((employee) => (
                    <tr key={employee.id} id={`employee-row-${employee.id}`}>
                      <td style={{ fontWeight: 500 }}>{employee.email}</td>
                      <td>{employee.phone}</td>
                      <td>{employee.birthDate}</td>
                      <td>
                        {employee.isBlocked ? (
                          <span className="badge badge-error">Blocked</span>
                        ) : (
                          <span className="badge badge-success">Active</span>
                        )}
                      </td>
                      <td>
                        <div style={{ display: "flex", gap: "6px" }}>
                          <Link href={`/admin/orders?employeeId=${employee.id}`} className="btn btn-ghost btn-sm" id={`view-employee-orders-${employee.id}`}>
                            {t("admin.orders")}
                          </Link>
                          {employee.isBlocked ? (
                            <button
                              className="btn btn-success btn-sm"
                              id={`unblock-employee-${employee.id}`}
                              onClick={() => setConfirm({ id: employee.id, type: "unblock" })}
                              disabled={actionLoading === employee.id}
                            >
                              {actionLoading === employee.id ? <span className="spinner spinner-sm" /> : t("admin.employees.unblock")}
                            </button>
                          ) : (
                            <button
                              className="btn btn-danger btn-sm"
                              id={`block-employee-${employee.id}`}
                              onClick={() => setConfirm({ id: employee.id, type: "block" })}
                              disabled={actionLoading === employee.id}
                            >
                              {actionLoading === employee.id ? <span className="spinner spinner-sm" /> : t("admin.employees.block")}
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            {employees.length === 0 && (
              <div className="empty-state"><h3 className="empty-state-title">{t("app.noResults")}</h3></div>
            )}
            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </>
        )}
      </div>

      {confirm && (
        <ConfirmDialog
          message={t(confirm.type === "block" ? "admin.employees.confirmBlock" : "admin.employees.confirmUnblock")}
          onConfirm={handleConfirm}
          onCancel={() => setConfirm(null)}
          isLoading={!!actionLoading}
          variant={confirm.type === "block" ? "danger" : "primary"}
        />
      )}

      {isRegisterModalOpen && (
        <div className="modal-overlay" onClick={closeRegisterModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3 className="modal-title">{t("admin.employees.register")}</h3>
            {!registerSuccess && <p className="text-sm text-muted mb-md">{t("admin.employees.registrationNote")}</p>}
            {registerError && <div className="text-error text-sm mb-sm">{registerError}</div>}
            
            {registerSuccess ? (
              <div style={{ textAlign: "center", padding: "16px 0" }}>
                <div style={{ fontSize: "48px", marginBottom: "16px" }}>✉️</div>
                <div
                  className="badge badge-success"
                  style={{ padding: "12px 16px", borderRadius: "10px", width: "100%", justifyContent: "center", marginBottom: "16px" }}
                >
                  {registerSuccess}
                </div>
                <div style={{ marginTop: "12px", fontSize: "14px", marginBottom: "24px" }}>
                  {t("auth.resendNote")}{" "}
                  <button
                    className="btn btn-ghost btn-sm"
                    onClick={handleResendVerification}
                    disabled={resendLoading}
                  >
                    {resendLoading ? <span className="spinner spinner-sm" /> : t("auth.resendLink")}
                  </button>
                  {resendSuccess && <div className="text-sm mt-sm" style={{ fontWeight: 500, color: "var(--color-success)" }}>{resendSuccess}</div>}
                </div>
                <button type="button" className="btn btn-primary" onClick={closeRegisterModal} style={{ width: "100%" }}>
                  {t("app.close")}
                </button>
              </div>
            ) : (
              <form onSubmit={handleRegisterSubmit}>
              <div className="form-group">
                <label className="form-label">{t("auth.email")}</label>
                <input
                  type="email"
                  className={`form-input ${fieldErrors.email ? "error" : ""}`}
                  required
                  value={registerForm.email}
                  onChange={(e) => setRegisterForm({ ...registerForm, email: e.target.value })}
                />
                {fieldErrors.email && <span className="form-error">{fieldErrors.email}</span>}
              </div>
              <div className="form-group">
              <label className="form-label">{t("auth.password")}</label>
              <div style={{ position: "relative" }}>
                <input
                  type={showPassword ? "text" : "password"}
                  className={`form-input ${fieldErrors.password ? "error" : ""}`}
                  required
                  value={registerForm.password}
                  onChange={(e) => setRegisterForm({ ...registerForm, password: e.target.value })}
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
              {fieldErrors.password && <span className="form-error">{fieldErrors.password}</span>}
            </div>
              <div className="form-group">
                <label className="form-label">{t("admin.employees.phone")}</label>
                <input
                  type="text"
                  className={`form-input ${fieldErrors.phone ? "error" : ""}`}
                  required
                  value={registerForm.phone}
                  onChange={(e) => setRegisterForm({ ...registerForm, phone: e.target.value })}
                />
                {fieldErrors.phone && <span className="form-error">{fieldErrors.phone}</span>}
              </div>
              <div className="form-group">
                <label className="form-label">{t("admin.employees.birthDate")}</label>
                <input
                  type="date"
                  className={`form-input ${fieldErrors.birthDate ? "error" : ""}`}
                  required
                  value={registerForm.birthDate}
                  onChange={(e) => setRegisterForm({ ...registerForm, birthDate: e.target.value })}
                />
                {fieldErrors.birthDate && <span className="form-error">{fieldErrors.birthDate}</span>}
              </div>
              <div className="modal-footer mt-md">
                <button type="button" className="btn btn-ghost" onClick={closeRegisterModal}>
                  {t("app.cancel")}
                </button>
                <button type="submit" className="btn btn-primary" disabled={registerLoading}>
                  {registerLoading ? <span className="spinner spinner-sm" /> : t("admin.employees.register")}
                </button>
              </div>
            </form>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
