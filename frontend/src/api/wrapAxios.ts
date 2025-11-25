import type { AxiosResponse } from "axios";

export async function wrapApi<T>(request: Promise<AxiosResponse<any>>): Promise<T> {
  try {
    const res = await request;

    // Nếu API có kiểu {status, data, message}
    if (res.data?.status === "error") {
      throw new Error(res.data.message || "API Error");
    }

    // Nếu có data thì lấy data, nếu không thì trả luôn res.data
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
