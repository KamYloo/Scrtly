import React, { useEffect } from 'react'
import '../../Styles/Middle.css'
import '../../Styles/form.css'
import { Banner } from './Banner.jsx'
import { FaUsers } from 'react-icons/fa'
import { AudioList } from '../Song/AudioList.jsx'
import { useDispatch, useSelector } from 'react-redux'
import {NavLink, Route, Routes, useLocation, useParams} from 'react-router-dom'
import { findArtistById, getArtistTracks } from '../../Redux/Artist/Action.js'
import { ArtistAlbums } from './ArtistAlbums.jsx'
import { Fans } from './Fans.jsx'
import { AboutArtist } from './AboutArtist.jsx'
import Spinner from '../../Components/Spinner.jsx'
import ErrorOverlay from '../../Components/ErrorOverlay.jsx'

function Artist({ volume, onTrackChange }) {
  const { artistId } = useParams()
  const location = useLocation()
  const playSongId = location.state?.playSongId;
  const dispatch = useDispatch()
  const { artist, song, auth } = useSelector(state => state)

  useEffect(() => {
    dispatch(findArtistById(artistId))
  }, [artistId, artist.follow])

  useEffect(() => {
    dispatch(getArtistTracks(artistId))
  }, [artistId])

  if (artist.loading || song.loading) {
    return <Spinner />
  }
  if (artist.error) {
    return <ErrorOverlay message={artist.error} />
  } else if (song.error) {
    return <ErrorOverlay message={song.error} />
  }

  return (
      <div className="mainBox">
        <Banner artist={artist} />

        <div className="menuList">
          <ul>
            <li>
              <NavLink to={`/artist/${artistId}/popular`} className={({ isActive }) => (isActive ? 'active' : undefined)}>
                Popular
              </NavLink>
            </li>
            <li>
              <NavLink to={`/artist/${artistId}/albums`} className={({ isActive }) => (isActive ? 'active' : undefined)}>
                Albums
              </NavLink>
            </li>
            <li>
              <NavLink to={`/artist/${artistId}/songs`} className={({ isActive }) => (isActive ? 'active' : undefined)}>
                Songs
              </NavLink>
            </li>
            <li>
              <NavLink to={`/artist/${artistId}/fans`} className={({ isActive }) => (isActive ? 'active' : undefined)}>
                Fans
              </NavLink>
            </li>
            <li>
              <NavLink to={`/artist/${artistId}/about`} className={({ isActive }) => (isActive ? 'active' : undefined)}>
                About
              </NavLink>
            </li>
          </ul>
          <p>
            <i>
              <FaUsers />
            </i>
            {artist.findArtist?.totalFans}
            <span>Followers</span>
          </p>
        </div>

        <Routes>
          <Route path="popular" element={<AudioList volume={volume} onTrackChange={onTrackChange} initialSongs={artist?.songs.content} req_artist={artist.findArtist?.id === auth.reqUser?.id} initialSongId={playSongId}/>}/>
          <Route path="albums" element={<ArtistAlbums artistId={artistId} />} />
          <Route path="songs" element={<AudioList volume={volume} onTrackChange={onTrackChange} initialSongs={artist?.songs.content} req_artist={artist.findArtist?.id === auth.reqUser?.id}/>}/>
          <Route path="fans" element={<Fans artistId={artistId} fans={artist.findArtist?.fans} />}/>
          <Route path="about" element={<AboutArtist artist={artist} artistBio={artist.findArtist?.artistBio} />}/>
        </Routes>
      </div>
  )
}

export { Artist }
