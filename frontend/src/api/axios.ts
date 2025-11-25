import axios from "axios";
import { getAuthMode } from "./config";

const api = axios.create({});

api.interceptors.request.use((config) => {
  const mode = getAuthMode();
  config.baseURL =
    mode === "jwt"
      ? import.meta.env.VITE_API_BASE_JWT
      : import.meta.env.VITE_API_BASE_BASIC;

  const skipAuth = config.url?.includes("/login") || config.url?.includes("/reset-password");
  if (skipAuth) return config;

  if (mode === "jwt") {
    const token = localStorage.getItem("access_token");
    if (token) config.headers.Authorization = `Bearer ${token}`;
  } else if (mode === "basic") {
    const token = localStorage.getItem("basic_token");
    if (token) config.headers.Authorization = `Basic ${token}`;
  }

  return config;
});

export default api;
