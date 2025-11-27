import { useEffect, useState } from "react";
import { me, updateProfile } from "../api/auth";
import { useNavigate } from "react-router-dom";
import type { UserData } from "../api/types";
import "../css/Profile.css";

export default function Profile() {
  const [user, setUser] = useState<UserData>({
    name: "",
    address: "",
    phoneNumber: "",
  });

  const [editMode, setEditMode] = useState(false);
  const [tempUser, setTempUser] = useState<UserData>(user);
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    me()
      .then(data => {
        const profileData = {
          name: data.name || "",
          address: data.address || "",
          phoneNumber: data.phoneNumber || "",
        };
        setUser(profileData);
        setTempUser(profileData);
      })
      .catch(() => navigate("/login"));
  }, []);

  const handleEdit = () => {
    setTempUser(user);
    setEditMode(true);
  };

  const handleCancel = () => {
    setTempUser(user);
    setEditMode(false);
  };

  const handleSave = async () => {
    try {
      await updateProfile(tempUser);
      setUser(tempUser);
      setMessage("Profile updated!");
      setEditMode(false);
    } catch (err: any) {
      setMessage(err.message || "Update failed");
    }
  };

  const logout = () => {
    localStorage.removeItem("access_token");
    localStorage.removeItem("basic_token");
    navigate("/login");
  };

  const goToOrders = () => {
    navigate("/orders");
  };

  return (
    <div className="profile-container">
      <div className="profile-card">

        <div className="profile-header">
          <h2>Profile</h2>

          {!editMode && (
            <div className="header-buttons">
              <button
                className="top-right-btn"
                onClick={() => navigate("/change-password")}
              >
                Change Password
              </button>

              <button
                className="top-right-btn"
                onClick={goToOrders}
              >
                Order
              </button>
            </div>
          )}
        </div>

        <div className="profile-field">
          <label>Name</label>
          <input
            disabled={!editMode}
            value={tempUser.name}
            onChange={e => setTempUser({ ...tempUser, name: e.target.value })}
          />
        </div>

        <div className="profile-field">
          <label>Address</label>
          <input
            disabled={!editMode}
            value={tempUser.address}
            onChange={e => setTempUser({ ...tempUser, address: e.target.value })}
          />
        </div>

        <div className="profile-field">
          <label>Phone Number</label>
          <input
            disabled={!editMode}
            value={tempUser.phoneNumber}
            onChange={e =>
              setTempUser({ ...tempUser, phoneNumber: e.target.value })
            }
          />
        </div>

        {message && <p className="profile-message">{message}</p>}

        <div className="profile-buttons">
          {!editMode ? (
            <>
              <button className="primary-btn" onClick={handleEdit}>
                Edit
              </button>

              <button className="primary-btn" onClick={goToOrders}>
                Order
              </button>

              <button className="danger-btn" onClick={logout}>
                Logout
              </button>
            </>
          ) : (
            <>
              <button className="primary-btn" onClick={handleSave}>
                Save
              </button>
              <button className="secondary-btn" onClick={handleCancel}>
                Cancel
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
