import React, {useState} from 'react'
import { IoMdPhotos } from "react-icons/io";
import { BsEmojiSmileFill , BsCameraVideoFill} from "react-icons/bs";
import EmojiPicker from 'emoji-picker-react'
import {useDispatch} from "react-redux";
import {createPost} from "../../Redux/Post/Action.js";


function AddPost({auth, token}) {
  const dispatch = useDispatch()
  const [descriptionText, setDescriptionText] = useState('')
  const [filePic, setFilePic] = useState(null)

  const handleFileChange = (e) => {
    setFilePic(e.target.files[0])
  }

  const handlePostCreation = () => {
    const formData = new FormData()
    formData.append('file', filePic)
    formData.append('description', descriptionText)
    formData.append('token', token)
    dispatch(createPost(formData))

    setDescriptionText('')
    setFilePic(null)
  }

  return (
    <div className='addPost'>
      <div className="context">
        <img src="" alt="" />
        <div className="middle">
          <textarea type="text" value={descriptionText}
                    onChange={(e) => setDescriptionText(e.target.value)} />
          <div className="attachments">
            <div className="attachment">
              <i><IoMdPhotos/></i>
              <span onClick={() => document.querySelector('input[type="file"]').click()}>Photo</span>
              <input
                  type="file"
                  accept="image/*"
                  style={{display: 'none'}}
                  onChange={handleFileChange}
              />
            </div>
            <div className="attachment">
              <i><BsCameraVideoFill/></i>
              <span>Video</span>
            </div>
          </div>
        </div>
        <div className="right">
          <button onClick={handlePostCreation}>Send</button>
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