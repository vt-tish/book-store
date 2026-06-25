import {
  OrderCardResponseDto,
  OrderDetailsResponseDto,
  AdminOrderCardResponseDto,
  AdminOrderDetailsResponseDto,
  OrderFilterRequestDto,
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

export async function submitOrder(
  fetchFn: FetchFn,
  acceptLanguage: string
): Promise<OrderDetailsResponseDto> {
  const res = await fetchFn(
    `${API_BASE}/orders`,
    { method: "POST" },
    { "Accept-Language": acceptLanguage }
  );
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function getMyOrders(
  page: number,
  size: number,
  fetchFn: FetchFn
): Promise<Page<OrderCardResponseDto>> {
  const query = buildQuery({ page, size });
  const res = await fetchFn(`${API_BASE}/orders${query}`);
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function getMyOrderById(
  id: string,
  fetchFn: FetchFn,
  acceptLanguage: string
): Promise<OrderDetailsResponseDto> {
  const res = await fetchFn(
    `${API_BASE}/orders/${id}`,
    {},
    { "Accept-Language": acceptLanguage }
  );
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

// Admin
export async function getAllOrdersAdmin(
  filter: OrderFilterRequestDto,
  page: number,
  size: number,
  fetchFn: FetchFn,
  sort?: string
): Promise<Page<AdminOrderCardResponseDto>> {
  const query = buildQuery({ ...filter, page, size, sort });
  const res = await fetchFn(`${API_BASE}/admin/orders${query}`);
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function getOrderByIdAdmin(
  id: string,
  fetchFn: FetchFn,
  acceptLanguage: string
): Promise<AdminOrderDetailsResponseDto> {
  const res = await fetchFn(
    `${API_BASE}/admin/orders/${id}`,
    {},
    { "Accept-Language": acceptLanguage }
  );
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function acceptOrder(id: string, fetchFn: FetchFn): Promise<void> {
  const res = await fetchFn(`${API_BASE}/admin/orders/${id}/accept`, {
    method: "PUT",
  });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
}

export async function completeOrder(
  id: string,
  fetchFn: FetchFn
): Promise<void> {
  const res = await fetchFn(`${API_BASE}/admin/orders/${id}/complete`, {
    method: "PUT",
  });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
}

export async function cancelOrder(id: string, fetchFn: FetchFn): Promise<void> {
  const res = await fetchFn(`${API_BASE}/admin/orders/${id}/cancel`, {
    method: "PUT",
  });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
}
