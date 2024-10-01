
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
import {AddPlayList} from "./Components/PlayListComponents/AddPlayList.jsx";
import {getUserPlayLists} from "./Redux/PlayList/Action.js";
import {PlayList} from "./Components/PlayListComponents/PlayList.jsx";
import {Home} from "./Components/Home.jsx";

function App() {
  const dispatch = useDispatch();
  const { auth, playList } = useSelector(store => store);
  const [createPlayList, setCreatePlayList] = useState(false)

  const [volume, setVolume] = useState(0.5)
  const [currentTrack, setCurrentTrack] = useState({ songName: 'Default Song', artist: 'Default Artist' })

  const handleTrackChange = (songName, artist) => {
    setCurrentTrack({ songName, artist })
  }

  useEffect(() => {
    dispatch(currentUser())
  }, [dispatch])

  useEffect(() => {
    if (auth.reqUser?.req_user) {
      dispatch(getUserPlayLists());
    }
  }, [dispatch, auth.reqUser?.req_user, playList.createPlayList, playList.deletePlayList]);


  const renderLayout = (Component, props) => {
    const { setVolume, currentTrack, volume, handleTrackChange} = props

    return (
      <div className='App'>
        <LeftMenu onVolumeChange={setVolume} currentTrack={currentTrack} setCreatePlayList={setCreatePlayList} />
        <Component volume={volume} onTrackChange={handleTrackChange}/>
        <RightMenu/>
        {createPlayList && <AddPlayList onClose={() => setCreatePlayList(((prev) => !prev))} />}
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

        <Route path="/profile/:userId" element={
          renderLayout(Profile, { setVolume, currentTrack})
        } />

        <Route path="/profile/edit" element={
          renderLayout(ProfileEdit, { setVolume, currentTrack})
        } />

        {/* Default Route */}
        <Route path="/" element={<Navigate to="/home" />} />
      </Routes>
    </Router>
  )
}

export default App
