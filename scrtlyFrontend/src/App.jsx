
import './App.css'
import React, { useState } from 'react'
import { LeftMenu } from './Components/LeftMenu'
import { Middle } from './Components/Middle'
import { RightMenu } from './Components/RightMenu'

function App() {
  const [volume, setVolume] = useState(0.5)
  return (
    <div className='App'>
      <LeftMenu onVolumeChange={setVolume} />
      <Middle volume={volume} />
      <RightMenu />


      <div className="background"></div>
    </div>
  )
}

export default App
