import React, { } from 'react'
import '../../Styles/Middle.css'
import '../../Styles/form.css'
import { Banner } from './Banner.jsx'
import { FaUsers } from 'react-icons/fa'
import { AudioList } from '../Song/AudioList.jsx'
import {NavLink, Route, Routes, useLocation, useParams} from 'react-router-dom'
import { ArtistAlbums } from './ArtistAlbums.jsx'
import { Fans } from './Fans.jsx'
import { AboutArtist } from './AboutArtist.jsx'
import Spinner from '../../Components/Spinner.jsx'
import ErrorOverlay from '../../Components/ErrorOverlay.jsx'
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";
import {useFindArtistByIdQuery, useGetArtistTracksQuery} from "../../Redux/services/artistApi.js";

function Artist({ volume, onTrackChange }) {
  const { artistId } = useParams()
  const location = useLocation()
  const playSongId = location.state?.playSongId;
  const { data: reqUser } = useGetCurrentUserQuery(null, {
    skip: !localStorage.getItem('isLoggedIn'),
  });
  const {
    data: artistData,
    isLoading: artistLoading,
    isError: artistError,
    error: artistErrorData,
  } = useFindArtistByIdQuery(artistId);

  const {
    data: { content: tracks = [] } = { content: [] },
    isLoading: tracksLoading,
    isError: tracksError,
    error: tracksErrorData,
  } = useGetArtistTracksQuery(artistId, {
    refetchOnMountOrArgChange: true
  });

  if (artistLoading || tracksLoading) {
    return <Spinner />
  }
  if (artistError) {
    return <ErrorOverlay error={artistErrorData} />;
  }
  if (tracksError) {
    return <ErrorOverlay error={tracksErrorData} />;
  }

  return (
      <div className="mainBox">
        <Banner artist={artistData} />

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
            {artistData?.totalFans}
            <span>Followers</span>
          </p>
        </div>

        <Routes>
          <Route path="popular" element={<AudioList volume={volume} onTrackChange={onTrackChange} initialSongs={tracks} req_artist={artistData?.id === reqUser?.id} initialSongId={playSongId}/>}/>
          <Route path="albums" element={<ArtistAlbums artistId={artistId} />} />
          <Route path="songs" element={<AudioList volume={volume} onTrackChange={onTrackChange} initialSongs={tracks} req_artist={artistData?.id === reqUser?.id}/>}/>
          <Route path="fans" element={<Fans artistId={artistId} fans={artistData?.fans} />}/>
          <Route path="about" element={<AboutArtist artist={artistData} artistBio={artistData?.artistBio} />}/>
        </Routes>
      </div>
  )
}

export { Artist }
