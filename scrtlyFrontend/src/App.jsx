
import './App.css'
import React, { useState } from 'react'
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import { LeftMenu } from './Components/LeftMenu'
import { Middle } from './Components/Middle'
import { RightMenu } from './Components/RightMenu'
import { ChatView } from './Components/ChatComponents/ChatView'
import { Login } from './Components/AuthComponents/Login';
import { Register } from './Components/AuthComponents/Register';
import { Profile } from './Components/AuthComponents/Profile';

function App() {
  const [volume, setVolume] = useState(0.5)
  const [currentTrack, setCurrentTrack] = useState({ songName: 'Default Song', artist: 'Default Artist' })

  const handleTrackChange = (songName, artist) => {
    setCurrentTrack({ songName, artist })
  }


  const renderLayout = (Component, props) => {
    const { setVolume, currentTrack, volume, handleTrackChange } = props

    return (
      <div className='App'>
        <LeftMenu onVolumeChange={setVolume} currentTrack={currentTrack} />
        <Component volume={volume} onTrackChange={handleTrackChange} />
        <RightMenu />
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
          renderLayout(Middle, { setVolume, currentTrack, volume, handleTrackChange })
        } />

        <Route path="/chat" element={
          renderLayout(ChatView, { setVolume, currentTrack })
        } />

        <Route path="/artists" element={
          renderLayout(() => <div>Artists Content</div>, { setVolume, currentTrack })
        } />

        <Route path="/profile" element={
          renderLayout(Profile, { setVolume, currentTrack })
        } />


        {/* Default Route */}
        <Route path="/" element={<Navigate to="/home" />} />
      </Routes>
    </Router>
  )
}

export default App
