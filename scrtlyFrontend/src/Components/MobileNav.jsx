import React, { useState, useEffect } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { MenuList } from "./MenuList";
import "../Styles/MobileNav.css";
import logo from "../assets/logo.png";
import {FaBell, FaAlignRight, FaCrown} from "react-icons/fa";
import { RiPlayListLine } from "react-icons/ri";
import { NotificationsList } from "../features/Notification/NotificationsList.jsx";
import { useDispatch, useSelector } from "react-redux";
import { getNotifications } from "../Redux/NotificationService/Action.js";
import toast from "react-hot-toast";
import defaultAvatar from "../assets/user.jpg";
import { MenuPlayList } from "../features/PlayList/MenuPlayList.jsx";
import {SubscribeButton} from "../features/Payment/SubscribeButton.jsx";
import {useGetCurrentUserQuery, useLogoutMutation} from "../Redux/services/authApi.js";

function MobileNav({setCreatePlayList}) {
  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserDropdown, setShowUserDropdown] = useState(false);
  const [showPlaylists, setShowPlaylists] = useState(false);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { data: currentUser } = useGetCurrentUserQuery(null, { skip: !localStorage.getItem('isLoggedIn') });
  const [logout] = useLogoutMutation();
  const base = window.location.origin;

  const notifications = useSelector((state) => state.notifications.notifications) || [];
  const unseenCount = notifications.filter((notif) => !notif.seen).length;

  const handleLogout = async () => {
    try {
      await logout().unwrap();
      toast.success("Logged out successfully");
      navigate("/login");
    } catch (err) {
      toast.error(err?.data?.message || "Logout failed");
    }
  };

  const handleProfileClick = () => {
    setShowUserDropdown(false);
    if (currentUser) {
      navigate(`/profile/${currentUser.nickName}`);
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

  return (
    <>
      <div className="mobileNav-top">
        <div className="mobileNav-top-left" onClick={() => navigate("/")}>
          <img src={logo} alt="Logo" className="mobileNav-logo" />
          <span className="mobileNav-title">Zuvoria</span>
        </div>
        <div className="mobileNav-top-right">
          <div
            className="mobileNav-icon"
            onClick={() => setShowPlaylists((prev) => !prev)}
          >
            <RiPlayListLine className="mobileNav-bell" />
          </div>
          <div
            className="mobileNav-icon"
            onClick={() => setShowNotifications(!showNotifications)}
          >
            <FaBell className="mobileNav-bell" />
            {unseenCount > 0 && <span className="badge"></span>}
          </div>
          <div className="mobileNav-icon" onClick={handleProfileClick}>
            <img
              src={currentUser?.profilePicture || defaultAvatar}
              alt="Profile"
              className="mobileNav-profile"
            />
          </div>
          <div className="mobileNav-user-dropdown">
            <p className="mobileNav-userText" onClick={toggleUserDropdown}>
              {currentUser ? (
                <FaAlignRight className="mobileNav-menu" />
              ) : (
                "Login"
              )}
            </p>
            {showUserDropdown && (
              <div className="dropdown-menu">
                {currentUser ? (
                  <>
                    <div className="dropdown-item" onClick={handleProfileClick}>
                      Profile
                    </div>
                    <div className="dropdown-item" onClick={handleLogout}>
                      Logout
                    </div>
                    {!currentUser.premium ? (
                        <div className="dropdown-item">
                          <SubscribeButton
                              successUrl={`${base}/success`}
                              cancelUrl={`${base}/cancel`}
                          />
                        </div>
                    ):
                        <div className="dropdown-item">
                          <i onClick={() => navigate('/account/billing')} title="My subscribe">
                          <FaCrown/>
                          <p>My <span>Premium</span></p>
                          </i>
                        </div>}
                  </>
                ) : (
                  <div
                    className="dropdown-item"
                    onClick={() => {
                      navigate("/login");
                      setShowUserDropdown(false);
                    }}
                  >
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
            <button
              className="closeBtn"
              onClick={() => setShowNotifications(false)}
            >
              ×
            </button>
          </div>
          <div className="modalContent">
            <NotificationsList notifications={notifications} />
          </div>
        </div>
      )}

      {showPlaylists && (
          <div className="mobilePlaylistsModal">
            <div className="modalHeader">
              <h2>Playlists</h2>
              <button
                  className="closeBtn"
                  onClick={() => setShowPlaylists(false)}
              >
                ×
              </button>
            </div>
            <div className="modalContent">
              <MenuPlayList
                  setCreatePlayList={setCreatePlayList}
                  closeModals={() => setShowPlaylists(false)}
              />
            </div>
          </div>
      )}
    </>
  );
}

export { MobileNav };
