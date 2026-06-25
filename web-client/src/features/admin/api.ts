import {
  AdminClientResponseDto,
  AdminEmployeeResponseDto,
  RegisterEmployeeRequestDto,
  UpdateEmployeeRequestDto,
  VerifyEmployeeRequestDto,
  ResponseDto,
  Page,
  ApiError,
} from "@/common/types/api";

const API_BASE =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api/v1";

type FetchFn = (
  url: string,
  opts?: RequestInit,
  headers?: Record<string, string>
) => Promise<Response>;

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

// Clients
export async function getAllClients(
  page: number,
  size: number,
  fetchFn: FetchFn,
  sort?: string
): Promise<Page<AdminClientResponseDto>> {
  const query = buildQuery({ page, size, sort });
  const res = await fetchFn(`${API_BASE}/admin/clients${query}`);
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function blockClient(id: string, fetchFn: FetchFn): Promise<void> {
  const res = await fetchFn(`${API_BASE}/admin/clients/${id}/block`, {
    method: "PUT",
  });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
}

export async function unblockClient(
  id: string,
  fetchFn: FetchFn
): Promise<void> {
  const res = await fetchFn(`${API_BASE}/admin/clients/${id}/unblock`, {
    method: "PUT",
  });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
}

// Employees
export async function getAllEmployees(
  page: number,
  size: number,
  fetchFn: FetchFn,
  sort?: string
): Promise<Page<AdminEmployeeResponseDto>> {
  const query = buildQuery({ page, size, sort });
  const res = await fetchFn(`${API_BASE}/admin/employees${query}`);
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function registerEmployee(
  data: RegisterEmployeeRequestDto,
  fetchFn: FetchFn,
  acceptLanguage: string
): Promise<ResponseDto> {
  const res = await fetchFn(
    `${API_BASE}/admin/employees/register`,
    { method: "POST", body: JSON.stringify(data) },
    { "Accept-Language": acceptLanguage }
  );
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function verifyEmployee(
  data: VerifyEmployeeRequestDto,
  fetchFn: FetchFn,
  acceptLanguage: string
): Promise<ResponseDto> {
  const res = await fetchFn(
    `${API_BASE}/admin/employees/verify`,
    { method: "POST", body: JSON.stringify(data) },
    { "Accept-Language": acceptLanguage }
  );
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function updateEmployee(
  id: string,
  data: UpdateEmployeeRequestDto,
  fetchFn: FetchFn,
  acceptLanguage: string
): Promise<AdminEmployeeResponseDto> {
  const res = await fetchFn(
    `${API_BASE}/admin/employees/${id}`,
    { method: "PUT", body: JSON.stringify(data) },
    { "Accept-Language": acceptLanguage }
  );
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function blockEmployee(
  id: string,
  fetchFn: FetchFn
): Promise<void> {
  const res = await fetchFn(`${API_BASE}/admin/employees/${id}/block`, {
    method: "PUT",
  });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
}

export async function unblockEmployee(
  id: string,
  fetchFn: FetchFn
): Promise<void> {
  const res = await fetchFn(`${API_BASE}/admin/employees/${id}/unblock`, {
    method: "PUT",
  });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
}
