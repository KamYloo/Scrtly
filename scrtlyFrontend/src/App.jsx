
import './App.css'
import { LeftMenu } from './Components/LeftMenu'
import { Middle } from './Components/Middle'
import { RightMenu } from './Components/RightMenu'

function App() {
  return (
    <div className='App'>
      <LeftMenu />
      <Middle />
      <RightMenu />


      <div className="background"></div>
    </div>
  )
}

export default App
