// API Types matching Java server DTOs

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: ValidationError[];
}

export interface ValidationError {
  field: string;
  message: string;
}

export interface ResponseDto {
  message: string;
}

// Auth
export interface AuthResponseDto {
  accessToken: string;
}

export interface RegisterRequestDto {
  email: string;
  password: string;
}

export interface LoginRequestDto {
  email: string;
  password: string;
}

export interface VerifyRequestDto {
  token: string;
}

export interface ResendVerificationRequestDto {
  email: string;
}

export interface ForgotPasswordRequestDto {
  email: string;
}

export interface ResetPasswordRequestDto {
  token: string;
  newPassword: string;
}

// Books
export type AgeGroup = "CHILD" | "TEEN" | "ADULT" | "OTHER";
export type BookLanguage =
  | "ENGLISH"
  | "SPANISH"
  | "FRENCH"
  | "GERMAN"
  | "JAPANESE"
  | "UKRAINIAN"
  | "OTHER";

export interface BookCardResponseDto {
  id: string;
  name: string;
  author: string;
  genre: string;
  price: number;
  previewUrl: string;
}

export interface BookDetailsResponseDto {
  id: string;
  name: string;
  genre: string;
  previewUrl: string;
  ageGroup: AgeGroup;
  price: number;
  publicationDate: string;
  author: string;
  pages: number;
  characteristics: string;
  description: string;
  language: BookLanguage;
  isAvailable: boolean;
}

export interface AdminBookCardResponseDto {
  id: string;
  name: string;
  author: string;
  genre: string;
  price: number;
  previewUrl: string;
  isArchived: boolean;
}

export interface AdminBookDetailsResponseDto {
  id: string;
  name: string;
  genre: string;
  previewUrl: string;
  ageGroup: AgeGroup;
  price: number;
  publicationDate: string;
  author: string;
  pages: number;
  characteristics: string;
  description: string;
  language: BookLanguage;
  createdAt: string;
  updatedAt: string;
  isArchived: boolean;
}

export interface BookTranslationDto {
  name: string;
  genre: string;
  author: string;
  characteristics?: string;
  description?: string;
}

export interface BookRequestDto {
  previewUrl?: string;
  ageGroup: AgeGroup;
  price: number;
  publicationDate: string;
  pages: number;
  language: BookLanguage;
  translations: Record<string, BookTranslationDto>;
}

export interface BookFilterRequestDto {
  search?: string;
  minPrice?: number;
  maxPrice?: number;
}

// Cart
export interface CartItemDto {
  id: string;
  bookId: string;
  bookName: string;
  previewUrl: string;
  quantity: number;
  pricePerUnit: number;
  subtotalPrice: number;
  isAvailable: boolean;
}

export interface CartResponseDto {
  cartItems: CartItemDto[];
  totalPrice: number;
}

export interface AddCartItemRequestDto {
  bookId: string;
  quantity: number;
}

export interface UpdateCartItemRequestDto {
  quantity: number;
}

// Orders
export type OrderStatus = "PENDING" | "ACCEPTED" | "CANCELLED" | "COMPLETED";

export interface OrderItemDto {
  id: string;
  bookId: string;
  bookName: string;
  bookAuthor: string;
  pricePerUnit: number;
  quantity: number;
  subtotalPrice: number;
}

export interface OrderCardResponseDto {
  id: string;
  totalPrice: number;
  totalItems: number;
  status: OrderStatus;
  createdAt: string;
  closedAt: string;
}

export interface OrderDetailsResponseDto {
  id: string;
  totalPrice: number;
  status: OrderStatus;
  createdAt: string;
  closedAt: string;
  items: OrderItemDto[];
}

export interface AdminOrderCardResponseDto {
  id: string;
  clientId: string;
  clientEmail: string;
  employeeId: string;
  employeeEmail: string;
  totalPrice: number;
  totalItems: number;
  status: OrderStatus;
  createdAt: string;
  closedAt: string;
}

export interface AdminOrderDetailsResponseDto {
  id: string;
  clientId: string;
  clientEmail: string;
  employeeId: string;
  employeeEmail: string;
  totalPrice: number;
  status: OrderStatus;
  createdAt: string;
  closedAt: string;
  items: OrderItemDto[];
}

export interface OrderFilterRequestDto {
  clientId?: string;
  employeeId?: string;
  status?: OrderStatus;
  minPrice?: number;
  maxPrice?: number;
}

// Clients
export interface AdminClientResponseDto {
  id: string;
  email: string;
  ordersCount: number;
  isBlocked: boolean;
  createdAt: string;
}

// Employees
export interface AdminEmployeeResponseDto {
  id: string;
  email: string;
  phone: string;
  birthDate: string;
  isBlocked: boolean;
  createdAt: string;
}

export interface EmployeeResponseDto {
  email: string;
  phone: string;
  birthDate: string;
}

export interface RegisterEmployeeRequestDto {
  email: string;
  password: string;
  phone: string;
  birthDate: string;
}

export interface UpdateEmployeeRequestDto {
  phone: string;
  birthDate: string;
}

export interface VerifyEmployeeRequestDto {
  token: string;
}

// Pagination
export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// Auth Token payload
export type UserRole = "CLIENT" | "EMPLOYEE" | "ADMIN";

export interface TokenPayload {
  sub: string; // userId
  roles: UserRole[];
  exp: number;
  iat: number;
}
