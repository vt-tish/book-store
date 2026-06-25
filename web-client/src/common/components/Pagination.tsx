"use client";

import React from "react";
import { useLocale } from "@/common/hooks/useLocale";

interface PaginationProps {
  currentPage: number; // 0-indexed (Spring)
  totalPages: number;
  onPageChange: (page: number) => void;
}

export function Pagination({
  currentPage,
  totalPages,
  onPageChange,
}: PaginationProps) {
  const { t } = useLocale();

  if (totalPages <= 1) return null;

  const pages = Array.from({ length: totalPages }, (_, i) => i);
  const displayPages = pages.filter(
    (p) =>
      p === 0 ||
      p === totalPages - 1 ||
      Math.abs(p - currentPage) <= 1
  );

  const withEllipsis: (number | "...")[] = [];
  let prev = -1;
  for (const p of displayPages) {
    if (prev !== -1 && p - prev > 1) {
      withEllipsis.push("...");
    }
    withEllipsis.push(p);
    prev = p;
  }

  return (
    <div className="pagination">
      <button
        className="pagination-btn"
        disabled={currentPage === 0}
        onClick={() => onPageChange(currentPage - 1)}
        aria-label="Previous page"
      >
        ‹
      </button>

      {withEllipsis.map((item, i) =>
        item === "..." ? (
          <span
            key={`ellipsis-${i}`}
            style={{ color: "var(--color-text-muted)", padding: "0 4px" }}
          >
            …
          </span>
        ) : (
          <button
            key={item}
            className={`pagination-btn ${item === currentPage ? "active" : ""}`}
            onClick={() => onPageChange(item as number)}
            aria-label={`${t("app.page")} ${(item as number) + 1}`}
            aria-current={item === currentPage ? "page" : undefined}
          >
            {(item as number) + 1}
          </button>
        )
      )}

      <button
        className="pagination-btn"
        disabled={currentPage >= totalPages - 1}
        onClick={() => onPageChange(currentPage + 1)}
        aria-label="Next page"
      >
        ›
      </button>
    </div>
  );
}
