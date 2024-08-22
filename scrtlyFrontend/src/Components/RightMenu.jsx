import React, {useState, useEffect} from 'react'
import { useNavigate } from 'react-router-dom';
import '../Styles/RightMenu.css'
import { FaBell, FaCogs, FaCrown, FaRegHeart, FaSun } from 'react-icons/fa'


function RightMenu() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    const token = localStorage.getItem('jwtToken')
    setIsLoggedIn(!!token) // Ustawienie stanu na podstawie obecności tokenu
  }, [])

  const handleLogout = () => {
    localStorage.removeItem('jwtToken')
    setIsLoggedIn(false) 
    navigate('/login') 
  }

  const handleProfileClick = () => {
    if (isLoggedIn) {
      navigate('/profile')
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
          <img src="#" alt="" />
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