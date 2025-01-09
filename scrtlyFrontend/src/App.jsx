import './App.css'
import React, {useState } from 'react'
import {Toaster} from "react-hot-toast";
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import { LeftMenu } from './Components/LeftMenu'
import { Artist } from './Components/Artists/Artist.jsx'
import { RightMenu } from './Components/RightMenu'
import { ChatView } from './Components/ChatComponents/ChatView'
import { Login } from './pages/Login.jsx';
import { Register } from './pages/Register.jsx';
import { Profile } from './Components/AuthComponents/Profile';
import { ArtistsView } from './Components/Artists/ArtistsView.jsx'
import { Discover } from './Components/DiscoverComponents/Discover.jsx'
import { ProfileEdit } from "./Components/AuthComponents/ProfileEdit.jsx";
import {AlbumsView} from "./Components/AlbumComponents/AlbumsView.jsx";
import {Album} from "./Components/AlbumComponents/Album.jsx";
import {PlayListForm} from "./Components/PlayListComponents/PlayListForm.jsx";
import {PlayList} from "./Components/PlayListComponents/PlayList.jsx";
import {Home} from "./Components/Home.jsx";

function App() {
  const [createPlayList, setCreatePlayList] = useState(false)
  const [volume, setVolume] = useState(0.5)
  const [currentTrack, setCurrentTrack] = useState({ songName: 'Default Song', artist: 'Default Artist' })

  const handleTrackChange = (songName, artist) => {
    setCurrentTrack({ songName, artist })
  }

  const renderLayout = (Component, props) => {
    const { setVolume, currentTrack, volume, handleTrackChange} = props

    return (
        <div className='App'>
          <LeftMenu onVolumeChange={setVolume} currentTrack={currentTrack} setCreatePlayList={setCreatePlayList} />
          <Component volume={volume} onTrackChange={handleTrackChange}/>
          <RightMenu/>
          {createPlayList && <PlayListForm onClose={() => setCreatePlayList(((prev) => !prev))} />}
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
            renderLayout(Home, { setVolume, currentTrack, volume, handleTrackChange})
          } />

          <Route path="/chat" element={
            renderLayout(ChatView, { setVolume, currentTrack})
          } />

          <Route path="/artists" element={
            renderLayout(ArtistsView, { setVolume, currentTrack})
          } />

          <Route path="/artist/:artistId/*" element={
            renderLayout(Artist, { setVolume, currentTrack, volume, handleTrackChange})
          } />

          <Route path="/albums" element={
            renderLayout(AlbumsView, { setVolume, currentTrack})
          } />

          <Route path="/album/:albumId" element={
            renderLayout(Album, { setVolume, currentTrack, volume, handleTrackChange})
          } />

          <Route path="/playList/:playListId" element={
            renderLayout(PlayList, { setVolume, currentTrack, volume, handleTrackChange})
          } />

          <Route path="/discover" element={
            renderLayout(Discover, { setVolume, currentTrack})
          } />

          <Route path="/profile/:nickName" element={
            renderLayout(Profile, { setVolume, currentTrack})
          } />

          <Route path="/profile/edit" element={
            renderLayout(ProfileEdit, { setVolume, currentTrack})
          } />

          <Route path="/" element={<Navigate to="/home" />} />
        </Routes>
        <Toaster
            position="bottom-right"
            reverseOrder={false}
        />
      </Router>
  )
}

export default App