import { useState, useEffect  } from "react";
import { useNavigate } from "react-router-dom";
import { changePassword } from "../api/auth";
import "../css/ChangePassword.css";

export default function ChangePassword() {
  const [oldPassword, setOld] = useState("");
  const [newPassword, setNew] = useState("");
  const [confirmPassword, setConfirm] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

    useEffect(() => {
    const token = localStorage.getItem("access_token") || localStorage.getItem("basic_token");
    if (!token) {
      navigate("/login");
    }
  }, [navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMessage("");
    setError("");

    if (!oldPassword || !newPassword || !confirmPassword) {
      setError("Please fill in all fields");
      return;
    }

    if (newPassword !== confirmPassword) {
      setError("New password and confirmation do not match");
      return;
    }

    try {
      const data: string = await changePassword(oldPassword, newPassword);
      setMessage(data);
      setOld(""); setNew(""); setConfirm("");
    } catch (err: any) {
      setError(err.message || "Change failed");
    }
  };

  return (
    <div className="change-container">
      <div className="change-box">
        <h2 className="change-title">Change Password</h2>
        <form onSubmit={handleSubmit}>
          <input
            type="password"
            placeholder="Old password"
            value={oldPassword}
            onChange={e => setOld(e.target.value)}
            className="change-input"
            required
          />
          <input
            type="password"
            placeholder="New password"
            value={newPassword}
            onChange={e => setNew(e.target.value)}
            className="change-input"
            required
          />
          <input
            type="password"
            placeholder="Confirm new password"
            value={confirmPassword}
            onChange={e => setConfirm(e.target.value)}
            className="change-input"
            required
          />
          <button type="submit" className="change-button">Change Password</button>
        </form>
        {message && <p className="change-message">{message}</p>}
        {error && <p className="change-error">{error}</p>}

        <button className="back-button" onClick={() => navigate("/profile")}>
          Back to Profile
        </button>
      </div>
    </div>
  );
}
