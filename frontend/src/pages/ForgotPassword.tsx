import { useState, useEffect } from "react";
import { forgotPassword } from "../api/auth";
import { useNavigate } from "react-router-dom";
import "../css/ForgotPassword.css";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [timer, setTimer] = useState(0);
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    let interval: number | undefined;

    if (timer > 0) {
      interval = window.setInterval(() => setTimer(prev => prev - 1), 1000);
    }

    return () => {
      if (interval) window.clearInterval(interval);
    };
  }, [timer]);

  const sendResetToken = () => {
    if (!email) {
      setError("Email is required");
      return;
    }

    setError("");
    setMessage("A password reset link has been sent!");
    setTimer(60);
    setLoading(true);

    forgotPassword(email)
      .catch(err => console.error("Forgot password API error:", err))
      .finally(() => setLoading(false));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    sendResetToken();
  };

  const handleBack = () => {
    navigate("/login");
  };

  return (
    <div className="forgot-container">
      <div className="forgot-box">
        <h2 className="forgot-title">Forgot Password</h2>
        <form onSubmit={handleSubmit}>
          <input
            type="email"
            placeholder="Your email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
            className="forgot-input"
          />
          <button
            type="submit"
            disabled={loading || timer > 0}
            className="forgot-button"
          >
            {timer > 0 ? `Resend in ${timer}s` : loading ? "Sending..." : "Send Reset Token"}
          </button>
        </form>
        {message && <p className="forgot-message">{message}</p>}
        {error && <p className="forgot-error">{error}</p>}

        <button onClick={handleBack} className="back-button">
          Back to Login
        </button>
      </div>
    </div>
  );
}
