import React from 'react'
import "../../Styles/Discover.css"
import { Stories } from './Stories'
import { AddPost } from './AddPost'
import { Feed } from './Feed'

// eslint-disable-next-line react/prop-types
function Discover({auth, token}) {
  return (
    <div className='discoverView'>
        <div className="discover">
        <Stories/>
        <AddPost auth={auth} token={token} />
        <Feed/>
        </div>
    </div>
  )
}

export  {Discover}