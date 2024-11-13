import React, {useState, useEffect} from 'react'
import { useNavigate } from 'react-router-dom';
import '../Styles/RightMenu.css'
import { FaBell, FaCogs, FaCrown, FaRegHeart, FaSun } from 'react-icons/fa'
import {useDispatch, useSelector} from "react-redux";
import {logoutAction} from "../Redux/Auth/Action.js";
import {BASE_API_URL} from "../config/api.js";
import {NotificationsList} from "./NotificationsList.jsx";


function RightMenu() {
  const [openNotifications, setOpenNotifications] = useState(false)
  const {auth} = useSelector(store => store);
  const dispatch = useDispatch()
  const navigate = useNavigate()

  const handleLogout = () => {
    dispatch(logoutAction())
    navigate('/login')
  }

  const handleProfileClick = () => {
    if (localStorage.getItem('token')) {
      navigate(`/profile/${auth.reqUser.id}`)
    } else {
      navigate('/login')
    }
  }

  return (
    <div className='rightMenu'>
      {openNotifications && <NotificationsList onClose={() => setOpenNotifications(false)}/>}
      <div className="top">
        <i><FaCrown /><p>Go <span>Premium</span></p></i>
        <i onClick={() => setOpenNotifications(((prev) => !prev))}><FaBell /></i>
        <i><FaRegHeart /></i>
      </div>
      <div className="profile">
        <i><FaSun /></i>
        <i><FaCogs /></i>
        <div className="profileImg" onClick={handleProfileClick}>
          <img src={`${BASE_API_URL}/${auth.reqUser?.profilePicture || ''}`} alt="" />
        </div>
        {localStorage.getItem('token') ? (
          <p className='loginBtn' onClick={handleLogout}>Logout</p>
        ) : (
          <p className='loginBtn' onClick={() => navigate('/login')}>Login</p>
        )}
      </div>
    </div>
  )
}

export { RightMenu }