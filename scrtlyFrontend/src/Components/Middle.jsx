import React, { useEffect } from 'react'
import '../Styles/Middle.css'
import '../Styles/form.css'
import { Banner } from './Banner'
import { FaUsers } from 'react-icons/fa'
import { AudioList } from './AudioList'
import {useDispatch, useSelector} from "react-redux";
import {Link, useParams} from "react-router-dom";
import {findArtistById, getArtistTracks} from "../Redux/Artist/Action.js";


function Middle({ volume, onTrackChange}) {
  const {artistId} = useParams();
  const dispatch = useDispatch();
  const {artist} = useSelector(store => store);

  useEffect(() => {
    const allLi = document.querySelector(".menuList").querySelectorAll("li")

    function changeManeuActive() {
      allLi.forEach((n) => n.classList.remove("active"))
      this.classList.add("active")
    }

    allLi.forEach((n) => n.addEventListener("click", changeManeuActive))
  }, [])

  useEffect(() => {
    dispatch(findArtistById(artistId))
  }, [artistId])

  useEffect(() => {
    dispatch(getArtistTracks(artistId))
  }, [artistId]);

  return (
    <div className='mainBox'>
      <Banner artist={artist} />

      <div className="menuList">
        <ul>
          <li><Link to={`/artist/${artistId}/popular`}>Popular</Link></li>
          <li><Link to={`/artist/${artistId}/albums`}>Albums</Link></li>
          <li><Link to={`/artist/${artistId}/songs`}>Songs</Link></li>
          <li><Link to={`/artist/${artistId}/fans`}>Fans</Link></li>
          <li><Link to={`/artist/${artistId}/about`}>About</Link></li>
        </ul>
        <p><i><FaUsers/></i>12.3M <span>Followers</span></p>
      </div>
      <AudioList volume={volume} onTrackChange={onTrackChange} initialSongs={artist.songs}/>
    </div>
  )
}

export {Middle}