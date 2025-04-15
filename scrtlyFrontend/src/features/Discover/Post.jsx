import React from 'react'
import { Comments } from './Comments.jsx'
import { MdCancel } from "react-icons/md";

function Post({post, onClose }) {
  return (
    <div className='postView'>
      <i className='cancel' onClick={onClose} ><MdCancel /></i>
      <div className="imgPost">
        <img src={post.image} alt="" />
      </div>
      <Comments post={post}/>
    </div>
  )
}

export { Post }