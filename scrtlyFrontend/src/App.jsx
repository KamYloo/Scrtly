
import './App.css'
import React, { useState } from 'react'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { LeftMenu } from './Components/LeftMenu'
import { Middle } from './Components/Middle'
import { RightMenu } from './Components/RightMenu'
import { ChatView } from './Components/ChatComponents/ChatView'
import { Login } from './Components/AuthComponents/Login';
import { Register } from './Components/AuthComponents/Register';

function App() {
  const [volume, setVolume] = useState(0.5)
  const [currentTrack, setCurrentTrack] = useState({ songName: 'Default Song', artist: 'Default Artist' });
  const [currentView, setCurrentView] = useState('home')

  const handleViewChange = (view) => {
    setCurrentView(view);
  }

  const renderMiddleComponent = () => {
    switch (currentView) {
      case 'home':
        return <Middle volume={volume} onTrackChange={handleTrackChange} />
      case 'chat':
        return <ChatView />;
      default:
        return <Middle volume={volume} onTrackChange={handleTrackChange} />;
    }
  }

  const handleTrackChange = (songName, artist) => {
    setCurrentTrack({ songName, artist })
  };

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
          <div className='App'>
            <LeftMenu onVolumeChange={setVolume} currentTrack={currentTrack} onViewChange={handleViewChange} />
            {renderMiddleComponent()}
            <RightMenu />
            <div className="background"></div>
          </div>
        } />
      </Routes>
    </Router>
  )
}

export default App
