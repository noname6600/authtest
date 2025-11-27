import { Routes, Route, Navigate } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ForgotPassword from "./pages/ForgotPassword";
import ResetPassword from "./pages/ResetPassword";
import Profile from "./pages/Profile";
import ChangePassword from "./pages/ChangePassword";
import OrderPage from "./pages/Order";

export default function App() {
  return (
      <Routes>
        <Route path="/" element={<Home />} />

        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/reset-password" element={<ResetPassword />} />

        <Route path="/profile" element={<Profile />} />
        <Route path="/orders" element={<OrderPage />} />
        <Route path="/change-password" element={<ChangePassword />} />

        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
  );
}
