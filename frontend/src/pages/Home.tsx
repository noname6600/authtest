import { setAuthMode } from "../api/config";
import { useNavigate } from "react-router-dom";
import "../css/Home.css"; 

export default function Home() {
  const navigate = useNavigate();

  const chooseMode = (mode: "jwt" | "basic") => {
    setAuthMode(mode);
    navigate("/login");
  };

  return (
    <div className="home-container">
      <div className="home-box">
        <h2 className="home-title">Select Auth Mode</h2>
        <div className="home-buttons">
          <button onClick={() => chooseMode("jwt")} className="home-button jwt">
            JWT
          </button>
          <button onClick={() => chooseMode("basic")} className="home-button basic">
            Basic
          </button>
        </div>
      </div>
    </div>
  );
}
