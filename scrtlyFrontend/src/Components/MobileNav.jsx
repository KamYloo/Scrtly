import React, { useState, useEffect } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { MenuList } from './MenuList';
import '../Styles/MobileNav.css';
import logo from '../assets/logo.png';
import {FaBell, FaAlignRight} from "react-icons/fa";
import { NotificationsList } from '../features/Notification/NotificationsList.jsx';
import { useDispatch, useSelector } from "react-redux";
import { logoutAction } from "../Redux/AuthService/Action.js";
import { getNotifications } from "../Redux/NotificationService/Action.js";
import toast from "react-hot-toast";

function MobileNav() {
  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserDropdown, setShowUserDropdown] = useState(false);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const userData = (() => {
    try {
      return JSON.parse(localStorage.getItem("user")) || null;
    } catch {
      return null;
    }
  })();

  const notifications = useSelector((state) => state.notifications.notifications) || [];
  const { logoutResponse, error } = useSelector((state) => state.auth);
  const unseenCount = notifications.filter((notif) => !notif.seen).length;

  const handleLogout = () => {
    dispatch(logoutAction());
    setShowUserDropdown(false);
  };

  const handleProfileClick = () => {
    setShowUserDropdown(false);
    if (userData) {
      navigate(`/profile/${userData.nickName}`);
    } else {
      navigate("/login");
    }
  };

  const toggleUserDropdown = () => {
    setShowUserDropdown((prev) => !prev);
  };

  useEffect(() => {
    if (showNotifications) {
      dispatch(getNotifications());
    }
  }, [showNotifications, dispatch]);

  useEffect(() => {
    if (logoutResponse) {
      toast.success(logoutResponse);
      localStorage.removeItem("user");
      navigate("/login");
    }
  }, [logoutResponse, navigate]);

  useEffect(() => {
    if (error) {
      toast.error(error);
    }
  }, [error]);

  return (
    <>
      <div className="mobileNav-top">
        <div className="mobileNav-top-left" onClick={() => navigate("/")}>
          <img src={logo} alt="Logo" className="mobileNav-logo" />
          <span className="mobileNav-title">Zuvoria</span>
        </div>
        <div className="mobileNav-top-right">
          <div className="mobileNav-icon" onClick={() => setShowNotifications(!showNotifications)}>
            <FaBell className="mobileNav-bell" />
            {unseenCount > 0 && <span className="badge">{unseenCount}</span>}
          </div>
          <div className="mobileNav-icon" onClick={handleProfileClick}>
            <img
              src={userData?.profilePicture || ""}
              alt="Profile"
              className="mobileNav-profile"
            />
          </div>
          <div className="mobileNav-user-dropdown">
            <p className="mobileNav-userText" onClick={toggleUserDropdown}>
              {localStorage.getItem("user") ? <FaAlignRight className="mobileNav-menu"/> : "Login"}
            </p>
            {showUserDropdown && (
              <div className="dropdown-menu">
                {localStorage.getItem("user") ? (
                  <>
                    <div className="dropdown-item" onClick={handleProfileClick}>Profile</div>
                    <div className="dropdown-item" onClick={handleLogout}>Logout</div>
                  </>
                ) : (
                  <div className="dropdown-item" onClick={() => { navigate("/login"); setShowUserDropdown(false); }}>
                    Login
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      </div>

      <div className="mobileNav-bottom">
        {MenuList.map((menu) => (
          <NavLink
            key={menu.id}
            to={menu.route}
            className={({ isActive }) =>
              "mobileNav-item" + (isActive ? " active" : "")
            }
          >
            <div className="icon">{menu.icon}</div>
            <span>{menu.name}</span>
          </NavLink>
        ))}
      </div>

      {showNotifications && (
        <div className="mobileNotificationsModal">
          <div className="modalHeader">
            <h2>Notifications</h2>
            <button className="closeBtn" onClick={() => setShowNotifications(false)}>×</button>
          </div>
          <div className="modalContent">
            <NotificationsList notifications={notifications} />
          </div>
        </div>
      )}
    </>
  );
}

export { MobileNav };