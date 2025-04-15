import React, {useEffect, useState} from 'react'
import { useNavigate } from 'react-router-dom';
import '../Styles/RightMenu.css'
import { FaBell, FaCogs, FaCrown, FaRegHeart, FaSun } from 'react-icons/fa'
import {useDispatch, useSelector} from "react-redux";
import {logoutAction} from "../Redux/AuthService/Action.js";
import {NotificationsList} from "../features/Notification/NotificationsList.jsx";
import toast from "react-hot-toast";
import {getNotifications} from "../Redux/NotificationService/Action.js";


function RightMenu() {
  const [openNotifications, setOpenNotifications] = useState(false)
  const userData = (() => { try { return JSON.parse(localStorage.getItem("user")) || null; } catch { return null; } })();
  const dispatch = useDispatch()
  const {auth} = useSelector(state => state);
  const notifications = useSelector(state => state.notifications.notifications);
  const navigate = useNavigate()

  const unseenCount = notifications.filter(notif => !notif.seen).length;

  const handleLogout = () => {
    dispatch(logoutAction())
        .then(() => {
          navigate("/login");
          toast.success(auth.logout || 'Logged out successfully');
        })
        .catch(() => {
          toast.error("Failed to logout. Please try again.");
        })
  }

  const handleProfileClick = () => {
    if (localStorage.getItem("user")) {
      navigate(`/profile/${userData?.nickName}`)
    } else {
      navigate('/login')
    }
  }

    useEffect(() => {
        if (openNotifications) {
            dispatch(getNotifications());
        }
    }, [openNotifications, notifications.length, dispatch ]);

  return (
    <div className='rightMenu'>
      {openNotifications && <NotificationsList notifications={notifications}/>}
        <div className="top">
            <i><FaCrown/><p>Go <span>Premium</span></p></i>
            <i
                className={unseenCount > 0 ? "has-unseen" : ""}
                onClick={() => setOpenNotifications(prev => !prev)}
            >
                <FaBell/>
            </i>
            <i><FaRegHeart/></i>
        </div>
        <div className="profile">
            <i><FaSun/></i>
            <i><FaCogs/></i>
            <div className="profileImg" onClick={handleProfileClick}>
                <img src={userData?.profilePicture || ''} alt="" />
        </div>
        {localStorage.getItem("user") ? (
          <p className='loginBtn' onClick={handleLogout}>Logout</p>
        ) : (
          <p className='loginBtn' onClick={() => navigate('/login')}>Login</p>
        )}
      </div>
    </div>
  )
}

export { RightMenu }