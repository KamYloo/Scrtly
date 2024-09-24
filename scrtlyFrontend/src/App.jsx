
import './App.css'
import React, { useEffect, useState } from 'react'
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import { LeftMenu } from './Components/LeftMenu'
import { Artist } from './Components/Artists/Artist.jsx'
import { RightMenu } from './Components/RightMenu'
import { ChatView } from './Components/ChatComponents/ChatView'
import { Login } from './Components/AuthComponents/Login';
import { Register } from './Components/AuthComponents/Register';
import { Profile } from './Components/AuthComponents/Profile';
import { ArtistsView } from './Components/Artists/ArtistsView.jsx'
import { Discover } from './Components/DiscoverComponents/Discover.jsx'
import { ProfileEdit } from "./Components/AuthComponents/ProfileEdit.jsx";
import { useDispatch, useSelector } from "react-redux";
import { currentUser } from "./Redux/Auth/Action.js";
import {AlbumsView} from "./Components/AlbumComponents/AlbumsView.jsx";
import {Album} from "./Components/AlbumComponents/Album.jsx";

function App() {
  const dispatch = useDispatch();
  const { auth } = useSelector(store => store);
  const token = localStorage.getItem('token')

  const [volume, setVolume] = useState(0.5)
  const [currentTrack, setCurrentTrack] = useState({ songName: 'Default Song', artist: 'Default Artist' })

  const handleTrackChange = (songName, artist) => {
    setCurrentTrack({ songName, artist })
  }

  useEffect(() => {
    if (token) dispatch(currentUser(token))
  }, [dispatch, token])

  const renderLayout = (Component, props) => {
    const { setVolume, currentTrack, volume, handleTrackChange, auth, token } = props

    return (
      <div className='App'>
        <LeftMenu onVolumeChange={setVolume} currentTrack={currentTrack} />
        <Component volume={volume} onTrackChange={handleTrackChange} auth={auth} token={token} />
        <RightMenu auth={auth} token={token} />
        <div className="background"></div>
      </div>
    )
  }

  return (
    <Router>
      <Routes>

        <Route path="/login" element={
          <div className='loginLayout'><Login />
            <div className="background"></div>
          </div>}
        />

        <Route path="/register" element={
          <div className="loginLayout"><Register />
            <div className="background"></div>
          </div>
        } />

        <Route path="/home" element={
          renderLayout(Artist, { setVolume, currentTrack, volume, handleTrackChange, auth, token })
        } />

        <Route path="/chat" element={
          renderLayout(ChatView, { setVolume, currentTrack, auth, token })
        } />

        <Route path="/artists" element={
          renderLayout(ArtistsView, { setVolume, currentTrack, auth, token })
        } />

        <Route path="/artist/:artistId/*" element={
          renderLayout(Artist, { setVolume, currentTrack, volume, handleTrackChange, auth, token })
        } />

        <Route path="/albums" element={
          renderLayout(AlbumsView, { setVolume, currentTrack, auth, token })
        } />

        <Route path="/album/:albumId" element={
          renderLayout(Album, { setVolume, currentTrack, volume, handleTrackChange, auth, token })
        } />

        <Route path="/discover" element={
          renderLayout(Discover, { setVolume, currentTrack, auth, token })
        } />

        <Route path="/profile/:userId" element={
          renderLayout(Profile, { setVolume, currentTrack, auth, token })
        } />

        <Route path="/profile/edit" element={
          renderLayout(ProfileEdit, { setVolume, currentTrack, auth, token })
        } />

        {/* Default Route */}
        <Route path="/" element={<Navigate to="/home" />} />
      </Routes>
    </Router>
  )
}

export default App
