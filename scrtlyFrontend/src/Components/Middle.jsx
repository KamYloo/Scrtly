import React, { useEffect } from 'react'
import '../Styles/Middle.css'
import '../Styles/form.css'
import { Banner } from './Banner'
import { FaUsers } from 'react-icons/fa'
import { AudioList } from './AudioList'


function Middle({ volume, onTrackChange}) {
  useEffect(() => {
    const allLi = document.querySelector(".menuList").querySelectorAll("li")

    function changeManeuActive() {
      allLi.forEach((n) => n.classList.remove("active"))
      this.classList.add("active")
    }

    allLi.forEach((n) => n.addEventListener("click", changeManeuActive))
  }, [])

  return (
    <div className='mainBox'>
      <Banner />

      <div className="menuList">
        <ul>
          <li><a href="#">Popular</a></li>
          <li><a href="#">Albums</a></li>
          <li><a href="#">Songs</a></li>
          <li><a href="#">Fans</a></li>
          <li><a href="#">About</a></li>
        </ul>
        <p><i><FaUsers /></i>12.3M <span>Followers</span></p>
      </div>
      <AudioList volume={volume} onTrackChange={onTrackChange}/>
    </div>
  )
}

export { Middle }