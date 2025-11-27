import api from "./axios";
import { wrapApi, wrapApiList } from "./wrapAxios";
import type { LoginData, UserData } from "./types";
import { getAuthMode } from "./config";
import type { Order, OrderForm } from "./types";

const LOGIN_URL = "/api/v1/auth/login";
const REGISTER_URL = "/api/v1/auth/register";
const FORGOT_PASSWORD_URL = "/api/v1/auth/forgot-password";
const RESET_PASSWORD_URL = "/api/v1/auth/reset-password";
const PROFILE_URL = "/api/v1/users/me";
const CHANGE_PASSWORD_URL = "/api/v1/accounts/me/password";
const ORDERS_URL = "/api/v1/orders";

// --- Login ---
export function login(email: string, password: string) {
  if (getAuthMode() === "basic") {
    return wrapApi<LoginData>(
      api.post(LOGIN_URL, { email, password })
    ).then((data) => {
      if (data.token) localStorage.setItem("basic_token", data.token);
      return data;
    });
  } else {
    return wrapApi<LoginData>(
      api.post(LOGIN_URL, { email, password })
    ).then((data) => {
      if (data.token) localStorage.setItem("access_token", data.token);
      return data;
    });
  }
}

// --- Register ---
export function register(email: string, password: string, name?: string) {
  return wrapApi<string>(api.post(REGISTER_URL, { email, password, name }));
}

// --- Forgot / Reset ---
export function forgotPassword(email: string) {
  return wrapApi<string>(api.post(FORGOT_PASSWORD_URL, { email }));
}

export function resetPassword(token: string, newPassword: string) {
  return wrapApi<string>(api.post(RESET_PASSWORD_URL, { token, newPassword }));
}

// --- Profile ---
export function me() {
  return wrapApi<UserData>(api.get(PROFILE_URL));
}

export function updateProfile(data: Partial<UserData>) {
  return wrapApi<UserData>(api.put(PROFILE_URL, data));
}

// --- Change password ---
export function changePassword(oldPassword: string, newPassword: string) {
  return wrapApi<string>(api.put(CHANGE_PASSWORD_URL, { oldPassword, newPassword }));
}


// --- GET LIST ---
export function getOrders(page = 0, size = 10) {
  return wrapApiList<Order>(api.get(`${ORDERS_URL}?page=${page}&size=${size}`));
}

// --- GET ONE ---
export function getOrder(id: number) {
  return wrapApi<Order>(api.get(`${ORDERS_URL}/${id}`));
}

// --- CREATE ---
export function createOrder(order: OrderForm) {
  return wrapApi<Order>(api.post(ORDERS_URL, order));
}

// --- UPDATE ---
export function updateOrder(id: number, order: OrderForm) {
  return wrapApi<Order>(api.put(`${ORDERS_URL}/${id}`, order));
}

// --- DELETE ---
export function deleteOrder(id: number) {
  return wrapApi<Order>(api.delete(`${ORDERS_URL}/${id}`));
}