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
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionLoading, setActionLoading] = useState<string | null>(null);
  const [confirm, setConfirm] = useState<{ id: string; type: "block" | "unblock" } | null>(null);

  const [isRegisterModalOpen, setIsRegisterModalOpen] = useState(false);
  const [registerForm, setRegisterForm] = useState({ email: "", password: "", phone: "", birthDate: "" });
  const [registerLoading, setRegisterLoading] = useState(false);
  const [registerError, setRegisterError] = useState("");

  const loadEmployees = useCallback(async () => {
    if (!isAuthenticated) return;
    setLoading(true);
    setError("");
    try {
      const data = await getAllEmployees(page, PAGE_SIZE, fetchWithAuth);
      setEmployees(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      const apiErr = err as ApiError;
      setError(apiErr.message ?? t("app.error"));
    } finally {
      setLoading(false);
    }
  }, [isAuthenticated, page, fetchWithAuth, t]);

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
    try {
      await registerEmployee(registerForm, fetchWithAuth, language);
      showToast(t("app.success"), "success");
      setIsRegisterModalOpen(false);
      setRegisterForm({ email: "", password: "", phone: "", birthDate: "" });
      loadEmployees();
    } catch (err) {
      const apiErr = err as ApiError;
      setRegisterError(apiErr.message ?? t("app.error"));
    } finally {
      setRegisterLoading(false);
    }
  };

  if (authLoading) return <LoadingSpinner />;

  return (
    <div className="admin-layout">
      <nav className="admin-sidebar">
        <Link href="/admin/books" className="admin-sidebar-link" id="sidebar-books">{t("admin.books")}</Link>
        <Link href="/admin/orders" className="admin-sidebar-link" id="sidebar-orders">{t("admin.orders")}</Link>
        <Link href="/admin/clients" className="admin-sidebar-link" id="sidebar-clients">{t("admin.clients")}</Link>
        {role === "ADMIN" && <Link href="/admin/employees" className="admin-sidebar-link active" id="sidebar-employees">{t("admin.employees")}</Link>}
      </nav>

      <div className="admin-content">
        <div className="page-header" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <h1 className="page-title">{t("admin.employees.title")}</h1>
          <button className="btn btn-primary" onClick={() => setIsRegisterModalOpen(true)}>
            {t("admin.employees.register")}
          </button>
        </div>

        {loading ? <LoadingSpinner /> : error ? <ErrorMessage message={error} onRetry={loadEmployees} /> : (
          <>
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
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            {employees.length === 0 && (
              <div className="empty-state"><div className="empty-state-icon"></div><h3 className="empty-state-title">{t("app.noResults")}</h3></div>
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
        <div className="modal-overlay" onClick={() => setIsRegisterModalOpen(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3 className="modal-title">{t("admin.employees.register")}</h3>
            <p className="text-sm text-muted mb-md">{t("admin.employees.registrationNote")}</p>
            {registerError && <div className="text-error text-sm mb-sm">{registerError}</div>}
            <form onSubmit={handleRegisterSubmit}>
              <div className="form-group">
                <label className="form-label">{t("auth.email")}</label>
                <input
                  type="email"
                  className="form-input"
                  required
                  value={registerForm.email}
                  onChange={(e) => setRegisterForm({ ...registerForm, email: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label className="form-label">{t("auth.password")}</label>
                <input
                  type="password"
                  className="form-input"
                  required
                  value={registerForm.password}
                  onChange={(e) => setRegisterForm({ ...registerForm, password: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label className="form-label">{t("admin.employees.phone")}</label>
                <input
                  type="text"
                  className="form-input"
                  required
                  value={registerForm.phone}
                  onChange={(e) => setRegisterForm({ ...registerForm, phone: e.target.value })}
                />
              </div>
              <div className="form-group">
                <label className="form-label">{t("admin.employees.birthDate")}</label>
                <input
                  type="date"
                  className="form-input"
                  required
                  value={registerForm.birthDate}
                  onChange={(e) => setRegisterForm({ ...registerForm, birthDate: e.target.value })}
                />
              </div>
              <div className="modal-footer mt-md">
                <button type="button" className="btn btn-ghost" onClick={() => setIsRegisterModalOpen(false)}>
                  {t("app.cancel")}
                </button>
                <button type="submit" className="btn btn-primary" disabled={registerLoading}>
                  {registerLoading ? <span className="spinner spinner-sm" /> : t("admin.employees.register")}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
