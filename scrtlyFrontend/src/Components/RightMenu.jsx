import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../Styles/RightMenu.css";
import {FaBell, FaCogs, FaCrown, FaMoon, FaRegHeart, FaSun} from "react-icons/fa";
import { NotificationsList } from "../features/Notification/NotificationsList.jsx";
import toast from "react-hot-toast";
import defaultAvatar from "../assets/user.jpg";
import {SubscribeButton} from "../features/Payment/SubscribeButton.jsx";
import {useGetCurrentUserQuery, useLogoutMutation} from "../Redux/services/authApi.js";
import {useGetNotificationsQuery} from "../Redux/services/notificationApi.js";
import {useTheme} from "../utils/useTheme.jsx";

function RightMenu() {
  const [openNotifications, setOpenNotifications] = useState(false);
  const { theme, toggleTheme } = useTheme();
  const base = window.location.origin;
  const [logout] = useLogoutMutation();
  const { data: currentUser } = useGetCurrentUserQuery(null, { skip: !localStorage.getItem('isLoggedIn') });
  const navigate = useNavigate();

  const { unseenCount } = useGetNotificationsQuery(
      { page: 0, size: 10 },
      {
        selectFromResult: ({ data }) => ({
          unseenCount:
              data?.notifications.filter((n) => !n.seen).length ?? 0,
        }),
      }
  );

  const handleLogout = async () => {
    try {
      await logout().unwrap();
      toast.success("Logged out successfully");
      navigate("/login");
    } catch (err) {
      toast.error(err.data.businessErrornDescription);
    }
  };


  const handleProfileClick = () => {
    if (currentUser) {
      navigate(`/profile/${currentUser?.nickName}`);
    } else {
      navigate("/login");
    }
  };

  return (
    <div className="rightMenu">
      {openNotifications && <NotificationsList />}
      <div className="top">
        {currentUser && (
            !currentUser.premium ? (
                <SubscribeButton
                    successUrl={`${base}/success`}
                    cancelUrl={`${base}/cancel`}
                />
            ) : (
                <i onClick={() => navigate('/account/billing')} title="My subscribe">
                  <FaCrown />
                  <p>
                    My <span>Premium</span>
                  </p>
                </i>
            )
        )}
        {currentUser && (
        <i
            className={unseenCount > 0 ? "has-unseen" : ""}
            onClick={() => setOpenNotifications((prev) => !prev)}
        >
          <FaBell/>
        </i> )}

        {/*<i>
          <FaRegHeart/>
        </i>*/}
      </div>
      <div className="profile">
          <i onClick={toggleTheme} title={theme === 'light' ? "Switch to Dark Mode" : "Switch to Light Mode"}>
              {theme === 'light' ? <FaMoon /> : <FaSun />}
          </i>

          {/* <i>
          <FaCogs/>
        </i>
        */}
        <div className="profileImg" onClick={handleProfileClick}>
          <img
              src={currentUser?.profilePicture || defaultAvatar}
              alt="Profilowe"
              onError={e => {
                e.currentTarget.src = defaultAvatar;
              }}
          />
        </div>
        {currentUser ? (
            <p className="loginBtn" onClick={handleLogout}>
              Logout
            </p>
        ) : (
            <p className="loginBtn" onClick={() => navigate("/login")}>
            Login
          </p>
        )}
      </div>
    </div>
  );
}

export { RightMenu };
