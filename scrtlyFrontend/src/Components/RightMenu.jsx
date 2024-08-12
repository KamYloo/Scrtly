import React from 'react'
import '../Styles/RightMenu.css'
import { FaBell, FaCogs, FaCrown, FaRegHeart, FaSun } from 'react-icons/fa'


function RightMenu() {
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
        <div className="profileImg">
          <img src="#" alt="" />
        </div>
      </div>
    </div>
  )
}

export { RightMenu }