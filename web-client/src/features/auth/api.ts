import {
  AuthResponseDto,
  RegisterRequestDto,
  LoginRequestDto,
  VerifyRequestDto,
  ResendVerificationRequestDto,
  ForgotPasswordRequestDto,
  ResetPasswordRequestDto,
  ResponseDto,
  ApiError,
} from "@/common/types/api";

const API_BASE =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api/v1";

async function authPost<TBody, TResponse>(
  path: string,
  body: TBody,
  acceptLanguage?: string
): Promise<TResponse> {
  const res = await fetch(`${API_BASE}${path}`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...(acceptLanguage ? { "Accept-Language": acceptLanguage } : {}),
    },
    body: JSON.stringify(body),
  });

  if (!res.ok) {
    const err: ApiError = await res.json();
    throw err;
  }

  // Some endpoints return no body (204)
  const text = await res.text();
  return text ? JSON.parse(text) : ({} as TResponse);
}

export async function registerUser(
  data: RegisterRequestDto,
  acceptLanguage: string
): Promise<ResponseDto> {
  return authPost("/auth/register", data, acceptLanguage);
}

export async function loginUser(
  data: LoginRequestDto,
  acceptLanguage: string
): Promise<AuthResponseDto> {
  return authPost("/auth/login", data, acceptLanguage);
}

export async function verifyUser(
  data: VerifyRequestDto,
  acceptLanguage: string
): Promise<AuthResponseDto> {
  return authPost("/auth/verify", data, acceptLanguage);
}

export async function resendVerification(
  data: ResendVerificationRequestDto,
  acceptLanguage: string
): Promise<ResponseDto> {
  return authPost("/auth/resend-verification", data, acceptLanguage);
}

export async function forgotPassword(
  data: ForgotPasswordRequestDto,
  acceptLanguage: string
): Promise<ResponseDto> {
  return authPost("/auth/forgot-password", data, acceptLanguage);
}

export async function resetPassword(
  data: ResetPasswordRequestDto,
  acceptLanguage: string
): Promise<ResponseDto> {
  return authPost("/auth/reset-password", data, acceptLanguage);
}

export async function refreshToken(): Promise<AuthResponseDto | null> {
  const res = await fetch(`${API_BASE}/auth/refresh`, {
    method: "POST",
    credentials: "include",
  });
  if (!res.ok) return null;
  return res.json();
}
