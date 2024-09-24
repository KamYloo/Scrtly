import React, { useEffect } from 'react'
import '../../Styles/Middle.css'
import '../../Styles/form.css'
import { Banner } from './Banner.jsx'
import { FaUsers } from 'react-icons/fa'
import { AudioList } from '../SongComponents/AudioList.jsx'
import {useDispatch, useSelector} from "react-redux";
import {Link, Route, Routes, useParams} from "react-router-dom";
import {findArtistById, getArtistTracks} from "../../Redux/Artist/Action.js";
import {ArtistAlbums} from "./ArtistAlbums.jsx";
import {Fans} from "./Fans.jsx";
import {AboutArtist} from "./AboutArtist.jsx";


function Artist({ volume, onTrackChange}) {
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
      <Routes>
        <Route path="popular" element={<AudioList volume={volume} onTrackChange={onTrackChange} initialSongs={artist.songs} />} />
        <Route path="albums" element={<ArtistAlbums artistId={artistId} />} />
        <Route path="songs" element={<AudioList volume={volume} onTrackChange={onTrackChange} initialSongs={artist.songs} />} />
        <Route path="fans" element={<Fans artistId={artistId}  fans={artist.findArtist?.fans}/>} />
        <Route path="about" element={<AboutArtist artist={artist} artistBio={artist.findArtist?.artistBio} />} />
      </Routes>
    </div>
  )
}

export {Artist}