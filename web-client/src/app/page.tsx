"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "@/common/hooks/useAuth";

export default function RootPage() {
  const { isAuthenticated, isLoading, role } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (isLoading) return;

    if (!isAuthenticated) {
      router.replace("/login");
    } else if (role === "CLIENT") {
      router.replace("/books");
    } else if (role === "EMPLOYEE" || role === "ADMIN") {
      router.replace("/admin/books");
    }
  }, [isAuthenticated, isLoading, role, router]);

  // Show a branded splash while redirecting
  return (
    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        background: "var(--gradient-hero)",
      }}
    >
      <div style={{ textAlign: "center" }}>

        <div
          style={{
            fontSize: "32px",
            fontWeight: 800,
            background: "var(--gradient-primary)",
            WebkitBackgroundClip: "text",
            WebkitTextFillColor: "transparent",
            backgroundClip: "text",
            letterSpacing: "-0.03em",
          }}
        >
          BookStore
        </div>
        <div style={{ marginTop: "24px" }}>
          <div className="spinner spinner-lg" style={{ margin: "0 auto" }} />
        </div>
      </div>
    </div>
  );
}
