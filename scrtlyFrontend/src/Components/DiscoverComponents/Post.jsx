import React from 'react'
import { Comments } from './Comments'
import { MdCancel } from "react-icons/md";

function Post({ onClose }) {
  return (
    <div className='postView'>
      <i className='cancel' onClick={onClose} ><MdCancel /></i>
      <div className="imgPost">
        <img src="https://img.freepik.com/free-photo/view-beautiful-rainbow-nature-landscape_23-2151597605.jpg?ga=GA1.1.635648649.1725025924&semt=ais_hybrid" alt="" />
      </div>
      <Comments />
    </div>
  )
}

export { Post }