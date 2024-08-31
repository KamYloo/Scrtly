import React from 'react'
import "../../Styles/Discover.css"
import { Stories } from './Stories'
import { AddPost } from './AddPost'
import { Feed } from './Feed'

function Discover() {
  return (
    <div className='discoverView'>
        <div className="discover">
        <Stories/>
        <AddPost/>
        <Feed/>
        </div>
    </div>
  )
}

export  {Discover}