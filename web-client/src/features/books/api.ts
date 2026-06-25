import {
  BookCardResponseDto,
  BookDetailsResponseDto,
  AdminBookCardResponseDto,
  AdminBookDetailsResponseDto,
  BookFilterRequestDto,
  BookRequestDto,
  Page,
  ApiError,
} from "@/common/types/api";

const API_BASE =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api/v1";

function buildQuery(
  params: Record<string, string | number | boolean | undefined | null>
): string {
  const q = new URLSearchParams();
  for (const [k, v] of Object.entries(params)) {
    if (v !== undefined && v !== null && v !== "") {
      q.set(k, String(v));
    }
  }
  const s = q.toString();
  return s ? `?${s}` : "";
}

export async function getBooks(
  filter: BookFilterRequestDto,
  page: number,
  size: number,
  fetchFn: (url: string, opts?: RequestInit, headers?: Record<string, string>) => Promise<Response>,
  acceptLanguage: string
): Promise<Page<BookCardResponseDto>> {
  const query = buildQuery({ ...filter, page, size });
  const res = await fetchFn(`${API_BASE}/books${query}`, {}, { "Accept-Language": acceptLanguage });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function getBookById(
  id: string,
  fetchFn: (url: string, opts?: RequestInit, headers?: Record<string, string>) => Promise<Response>,
  acceptLanguage: string
): Promise<BookDetailsResponseDto> {
  const res = await fetchFn(`${API_BASE}/books/${id}`, {}, { "Accept-Language": acceptLanguage });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function getAllBooksAdmin(
  filter: BookFilterRequestDto,
  page: number,
  size: number,
  fetchFn: (url: string, opts?: RequestInit, headers?: Record<string, string>) => Promise<Response>,
  acceptLanguage: string,
  sort?: string
): Promise<Page<AdminBookCardResponseDto>> {
  const query = buildQuery({ ...filter, page, size, sort });
  const res = await fetchFn(`${API_BASE}/admin/books${query}`, {}, { "Accept-Language": acceptLanguage });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function getBookByIdAdmin(
  id: string,
  fetchFn: (url: string, opts?: RequestInit, headers?: Record<string, string>) => Promise<Response>,
  acceptLanguage: string
): Promise<AdminBookDetailsResponseDto> {
  const res = await fetchFn(`${API_BASE}/admin/books/${id}`, {}, { "Accept-Language": acceptLanguage });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function createBook(
  data: BookRequestDto,
  fetchFn: (url: string, opts?: RequestInit, headers?: Record<string, string>) => Promise<Response>,
  acceptLanguage: string
): Promise<AdminBookDetailsResponseDto> {
  const res = await fetchFn(
    `${API_BASE}/admin/books`,
    { method: "POST", body: JSON.stringify(data) },
    { "Accept-Language": acceptLanguage }
  );
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function updateBook(
  id: string,
  data: BookRequestDto,
  fetchFn: (url: string, opts?: RequestInit, headers?: Record<string, string>) => Promise<Response>,
  acceptLanguage: string
): Promise<AdminBookDetailsResponseDto> {
  const res = await fetchFn(
    `${API_BASE}/admin/books/${id}`,
    { method: "PUT", body: JSON.stringify(data) },
    { "Accept-Language": acceptLanguage }
  );
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function deleteBook(
  id: string,
  fetchFn: (url: string, opts?: RequestInit, headers?: Record<string, string>) => Promise<Response>
): Promise<void> {
  const res = await fetchFn(`${API_BASE}/admin/books/${id}`, { method: "DELETE" });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
}

export async function archiveBook(
  id: string,
  fetchFn: (url: string, opts?: RequestInit, headers?: Record<string, string>) => Promise<Response>
): Promise<void> {
  const res = await fetchFn(`${API_BASE}/admin/books/${id}/archive`, { method: "PUT" });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
}

export async function unarchiveBook(
  id: string,
  fetchFn: (url: string, opts?: RequestInit, headers?: Record<string, string>) => Promise<Response>
): Promise<void> {
  const res = await fetchFn(`${API_BASE}/admin/books/${id}/unarchive`, { method: "PUT" });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
}
