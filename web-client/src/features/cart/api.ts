import {
  CartResponseDto,
  AddCartItemRequestDto,
  UpdateCartItemRequestDto,
  ApiError,
} from "@/common/types/api";

const API_BASE =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api/v1";

type FetchFn = (
  url: string,
  opts?: RequestInit,
  headers?: Record<string, string>
) => Promise<Response>;

export async function getCart(
  fetchFn: FetchFn,
  acceptLanguage: string
): Promise<CartResponseDto> {
  const res = await fetchFn(
    `${API_BASE}/cart`,
    {},
    { "Accept-Language": acceptLanguage }
  );
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function addCartItem(
  data: AddCartItemRequestDto,
  fetchFn: FetchFn,
  acceptLanguage: string
): Promise<CartResponseDto> {
  const res = await fetchFn(
    `${API_BASE}/cart/items`,
    { method: "POST", body: JSON.stringify(data) },
    { "Accept-Language": acceptLanguage }
  );
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function updateCartItem(
  id: string,
  data: UpdateCartItemRequestDto,
  fetchFn: FetchFn,
  acceptLanguage: string
): Promise<CartResponseDto> {
  const res = await fetchFn(
    `${API_BASE}/cart/items/${id}`,
    { method: "PUT", body: JSON.stringify(data) },
    { "Accept-Language": acceptLanguage }
  );
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function removeCartItem(
  id: string,
  fetchFn: FetchFn,
  acceptLanguage: string
): Promise<CartResponseDto> {
  const res = await fetchFn(
    `${API_BASE}/cart/items/${id}`,
    { method: "DELETE" },
    { "Accept-Language": acceptLanguage }
  );
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
  return res.json();
}

export async function clearCart(fetchFn: FetchFn): Promise<void> {
  const res = await fetchFn(`${API_BASE}/cart`, { method: "DELETE" });
  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }
}
