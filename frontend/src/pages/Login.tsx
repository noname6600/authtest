import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../api/auth";
import { getAuthMode } from "../api/config";
import "../css/Login.css";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    if (!getAuthMode()) {
      navigate("/"); 
    }
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    try {
      const data = await login(email, password);
      console.log("Logged in user:", data.name, data.user_id);
      navigate("/profile");
    } catch (err: any) {
      setError(err.message || "Login failed");
    }
  };

  const handleBackToMode = () => {
    navigate("/");
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <h2 className="login-title">Login</h2>
        <form onSubmit={handleSubmit} className="login-form">
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
            className="login-input"
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
            className="login-input"
          />
          <button type="submit" className="login-button">Login</button>
        </form>
        {error && <p className="login-error">{error}</p>}

        <div className="login-links">
          <a href="/forgot-password">Forgot Password?</a>
          <a href="/register">Register</a>
        </div>

        <button onClick={handleBackToMode} className="back-button">
          Back to Select Auth Mode
        </button>
      </div>
    </div>
  );
}
