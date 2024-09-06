import React from 'react'
import { Comments } from './Comments'
import { MdCancel } from "react-icons/md";
import {BASE_API_URL} from "../../config/api.js";

function Post({post, onClose }) {
  return (
    <div className='postView'>
      <i className='cancel' onClick={onClose} ><MdCancel /></i>
      <div className="imgPost">
        <img src={`${BASE_API_URL}${post.image}`} alt="" />
      </div>
      <Comments post={post}/>
    </div>
  )
}

export { Post }