import type { AxiosResponse } from "axios";
import type { ApiResponseList } from "./types";

export async function wrapApi<T>(request: Promise<AxiosResponse<any>>): Promise<T> {
  try {
    const res = await request;

    if (res.data?.status === "error") {
      throw new Error(res.data.message || "API Error");
    }
    const result = res.data?.data ?? res.data;

    if (result === null || result === undefined) {
      throw new Error(res.data?.message || "API returned empty data");
    }

    return result as T;
  } catch (err: any) {
    if (err.response?.data?.message) {
      throw new Error(err.response.data.message);
    }
    throw err;
  }
}


export async function wrapApiList<T>(request: Promise<AxiosResponse<any>>): Promise<ApiResponseList<T>> {
  try {
    const res = await request;

    if (res.data?.success === false) {
      throw new Error(res.data.message || "API Error");
    }

    return res.data as ApiResponseList<T>;
  } catch (err: any) {
    if (err.response?.data?.message) {
      throw new Error(err.response.data.message);
    }
    throw err;
  }
}