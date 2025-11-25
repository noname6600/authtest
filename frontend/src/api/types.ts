export interface ApiResponse<T> {
  status: "success" | "error";
  data: T | null;
  message: string;
}

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
