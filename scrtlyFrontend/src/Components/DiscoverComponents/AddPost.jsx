import React from 'react'
import { IoMdPhotos } from "react-icons/io";
import { BsEmojiSmileFill , BsCameraVideoFill} from "react-icons/bs";
import EmojiPicker from 'emoji-picker-react'


function AddPost() {
  return (
    <div className='addPost'>
      <div className="context">
        <img src="" alt="" />
        <div className="middle">
          <textarea type="text" />
          <div className="attachments">
            <div className="attachment">
              <i><IoMdPhotos /></i>
              <span>Photo</span>
            </div>
            <div className="attachment">
              <i><BsCameraVideoFill /></i>
              <span>Video</span>
            </div>
          </div>
        </div>
        <div className="right">
          <button>Send</button>
          <div className="emoji">
            <i><BsEmojiSmileFill /></i>
            <div className="picker">
              {/* <EmojiPicker /> */}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export { AddPost }