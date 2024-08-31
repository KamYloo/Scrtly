import React from 'react'
import EmojiPicker from 'emoji-picker-react'
import { BsEmojiSmileFill} from "react-icons/bs";
function AddComment() {
  return (
    <div className='addComment'>
        <div className="emoji">
            <i><BsEmojiSmileFill /></i>
            <div className="picker">
              {/* <EmojiPicker /> */}
            </div>
          </div>
          <textarea placeholder='Add Comment...' name="" id=""></textarea>
          <button>Send</button>
    </div>
  )
}

export  {AddComment}