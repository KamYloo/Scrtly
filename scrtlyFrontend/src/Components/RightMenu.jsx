import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../Styles/RightMenu.css";
import { FaBell, FaCogs, FaCrown, FaRegHeart, FaSun } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import { logoutAction } from "../Redux/AuthService/Action.js";
import { NotificationsList } from "../features/Notification/NotificationsList.jsx";
import toast from "react-hot-toast";
import { getNotifications } from "../Redux/NotificationService/Action.js";
import defaultAvatar from "../assets/user.jpg";

function RightMenu() {
  const [openNotifications, setOpenNotifications] = useState(false);
  const dispatch = useDispatch();
  const { logoutResponse, error, reqUser } = useSelector((state) => state.auth);
  const notifications = useSelector(
    (state) => state.notifications.notifications
  );
  const navigate = useNavigate();

  const unseenCount = notifications.filter((notif) => !notif.seen).length;

  const handleLogout = () => {
    dispatch(logoutAction());
  };

  const handleProfileClick = () => {
    if (reqUser) {
      navigate(`/profile/${reqUser?.nickName}`);
    } else {
      navigate("/login");
    }
  };

  useEffect(() => {
    if (openNotifications) {
      dispatch(getNotifications());
    }
  }, [openNotifications, notifications.length]);

  useEffect(() => {
    if (logoutResponse) {
      toast.success(logoutResponse);
      navigate("/login");
    }
  }, [dispatch, logoutResponse, navigate]);

  useEffect(() => {
    if (error) {
      toast.error(error);
    }
  }, [error]);

  return (
    <div className="rightMenu">
      {openNotifications && <NotificationsList notifications={notifications} />}
      <div className="top">
        <i>
          <FaCrown />
          <p>
            Go <span>Premium</span>
          </p>
        </i>
        <i
          className={unseenCount > 0 ? "has-unseen" : ""}
          onClick={() => setOpenNotifications((prev) => !prev)}
        >
          <FaBell />
        </i>
        <i>
          <FaRegHeart />
        </i>
      </div>
      <div className="profile">
        <i>
          <FaSun />
        </i>
        <i>
          <FaCogs />
        </i>
        <div className="profileImg" onClick={handleProfileClick}>
          <img
              src={reqUser?.profilePicture || defaultAvatar}
              alt="Profilowe"
              onError={e => {
                e.currentTarget.src = defaultAvatar;
              }}
          />
        </div>
        {reqUser ? (
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
