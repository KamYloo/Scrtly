import React, {useState, useEffect} from 'react'
import { useNavigate } from 'react-router-dom';
import '../Styles/RightMenu.css'
import { FaBell, FaCogs, FaCrown, FaRegHeart, FaSun } from 'react-icons/fa'
import {useDispatch} from "react-redux";
import {logoutAction} from "../Redux/Auth/Action.js";
import {BASE_API_URL} from "../config/api.js";


function RightMenu({auth, token}) {
  const [isLoggedIn, setIsLoggedIn] = useState(false)

  const dispatch = useDispatch()
  const navigate = useNavigate()


  useEffect(() => {
    const token = localStorage.getItem('token')
    setIsLoggedIn(!!token) // Ustawienie stanu na podstawie obecności tokenu
  }, [])

  const handleLogout = () => {
    dispatch(logoutAction())
    setIsLoggedIn(false)
    navigate('/login')
  }

  const handleProfileClick = () => {
    if (isLoggedIn) {
      navigate(`/profile/${auth.reqUser.id}`)
    } else {
      navigate('/login')
    }
  }

  return (
    <div className='rightMenu'>
      <div className="top">
        <i><FaCrown /><p>Go <span>Premium</span></p></i>
        <i><FaBell /></i>
        <i><FaRegHeart /></i>
      </div>
      <div className="profile">
        <i><FaSun /></i>
        <i><FaCogs /></i>
        <div className="profileImg" onClick={handleProfileClick}>
          <img src={`${BASE_API_URL}/${auth.reqUser?.profilePicture || ''}`} alt="" />
        </div>
        {isLoggedIn ? (
          <p className='loginBtn' onClick={handleLogout}>Logout</p>  // Przycisk Logout
        ) : (
          <p className='loginBtn' onClick={() => navigate('/login')}>Login</p> // Przycisk Login
        )}
      </div>
    </div>
  )
}

export { RightMenu }