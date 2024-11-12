import React, {useState} from 'react'
import { IoMdPhotos } from "react-icons/io";
import { BsEmojiSmileFill , BsCameraVideoFill} from "react-icons/bs";
import EmojiPicker from 'emoji-picker-react'
import {useDispatch, useSelector} from "react-redux";
import {createPost} from "../../Redux/Post/Action.js";
import {BASE_API_URL} from "../../config/api.js";
import {useNavigate} from "react-router-dom";
import toast from "react-hot-toast";


function AddPost() {
  const dispatch = useDispatch()
  const [descriptionText, setDescriptionText] = useState('')
  const [filePic, setFilePic] = useState(null)
  const [openEmoji, setOpenEmoji] = useState(false)
  const { auth } = useSelector(store => store);
  const navigate = useNavigate();

  const handleFileChange = (e) => {
    setFilePic(e.target.files[0])
  }

  const handleEmoji = (e) => {
    setDescriptionText((prev) => prev + e.emoji)
    setOpenEmoji(false)
  }

  const handlePostCreation = () => {
    const formData = new FormData()
    formData.append('file', filePic)
    formData.append('description', descriptionText)

    dispatch(createPost(formData))
        .then(() => {
          toast.success('Post created successfully.');
        })
        .catch(() => {
          toast.error('Failed to create post. Please try again.');
        })
        .finally(() => {
          setDescriptionText('')
          setFilePic(null)
        });
  }

  return (
    <div className='addPost'>
      <div className="context">
        <img src={`${BASE_API_URL}/${auth.reqUser?.profilePicture || ''}`} alt="" onClick={() => navigate(`/profile/${auth.reqUser.id}`)}/>
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
            <i onClick={() => setOpenEmoji((prev) => !prev)}><BsEmojiSmileFill /></i>
            <div className="picker">
               <EmojiPicker open={openEmoji} onEmojiClick={handleEmoji}/>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export { AddPost }