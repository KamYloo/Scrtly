import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../Styles/RightMenu.css";
import { FaBell, FaCogs, FaCrown, FaRegHeart, FaSun } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import { NotificationsList } from "../features/Notification/NotificationsList.jsx";
import toast from "react-hot-toast";
import { getNotifications } from "../Redux/NotificationService/Action.js";
import defaultAvatar from "../assets/user.jpg";
import {SubscribeButton} from "../features/Payment/SubscribeButton.jsx";
import {useGetCurrentUserQuery, useLogoutMutation} from "../Redux/services/authApi.js";

function RightMenu() {
  const [openNotifications, setOpenNotifications] = useState(false);
  const dispatch = useDispatch();
  const base = window.location.origin;
  const [logout] = useLogoutMutation();
  const { data: currentUser } = useGetCurrentUserQuery(null, { skip: !localStorage.getItem('isLoggedIn') });
  const notifications = useSelector(
    (state) => state.notifications.notifications
  );
  const navigate = useNavigate();

  const unseenCount = notifications.filter((notif) => !notif.seen).length;

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

  useEffect(() => {
    if (currentUser)
      dispatch(getNotifications());
  }, []);

  return (
    <div className="rightMenu">
      {openNotifications && <NotificationsList notifications={notifications} />}
      <div className="top">
        {!currentUser?.premium ? (
                <SubscribeButton
                    successUrl={`${base}/success`}
                    cancelUrl={`${base}/cancel`}
                />
            ) :
            <i onClick={() => navigate('/account/billing')} title="My subscribe">
              <FaCrown/>
              <p>
                My <span>Premium</span>
              </p>
            </i>}
        <i
            className={unseenCount > 0 ? "has-unseen" : ""}
            onClick={() => setOpenNotifications((prev) => !prev)}
        >
          <FaBell/>
        </i>

        <i>
          <FaRegHeart/>
        </i>
      </div>
      <div className="profile">
        <i>
          <FaSun/>
        </i>
        <i>
          <FaCogs/>
        </i>
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
