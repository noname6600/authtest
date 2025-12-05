export interface ApiResponse<T> {
  status: "success" | "error";
  data: T | null;
  message: string;
}

export interface ApiResponseList<T> {
  success: boolean;
  message: string | null;  
  data: T[];             
  page: number;
  size: number;
  totalPages: number;
}

export type OrderStatus = "N" | "C" | "A";

export interface LoginData {
  token?: string;
  name: string;
  user_id?: number;
}

export interface UserData {
  name: string;
  address?: string;
  phoneNumber?: string;
}

export interface ChangePasswordData {
  oldPassword: string;
  newPassword: string;
}

export interface Order {
  id: number;
  userId: number;
  product: string;
  deliveryDate: string;
  status: OrderStatus;
}

export interface OrderForm {
  product: string;
  deliveryDate: string;
  status: OrderStatus;
}