import React from 'react'
import "../Styles/Discover.css"
import { Stories } from '../features/Discover/Stories.jsx'
import { AddPost } from '../features/Discover/AddPost.jsx'
import { Feed } from '../features/Discover/Feed.jsx'

// eslint-disable-next-line react/prop-types
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