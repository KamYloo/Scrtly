
import './App.css'
import React, { useState } from 'react'
import { LeftMenu } from './Components/LeftMenu'
import { Middle } from './Components/Middle'
import { RightMenu } from './Components/RightMenu'

function App() {
  const [volume, setVolume] = useState(0.5)
  const [currentTrack, setCurrentTrack] = useState({ songName: 'Default Song', artist: 'Default Artist' });

  const handleTrackChange = (songName, artist) => {
    setCurrentTrack({ songName, artist });
  };

  return (
    <div className='App'>
      <LeftMenu onVolumeChange={setVolume} currentTrack={currentTrack} />
      <Middle volume={volume} onTrackChange={handleTrackChange}/>
      <RightMenu />


      <div className="background"></div>
    </div>
  )
}

export default App
