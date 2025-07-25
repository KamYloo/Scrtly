import React, {useState} from 'react'
import { IoMdPhotos } from "react-icons/io";
import { BsEmojiSmileFill , BsCameraVideoFill} from "react-icons/bs";
import EmojiPicker from 'emoji-picker-react'
import {useNavigate} from "react-router-dom";
import toast from "react-hot-toast";
import defaultAvatar from "../../assets/user.jpg";
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";
import {useCreatePostMutation} from "../../Redux/services/postApi.js";



function AddPost() {
  const navigate = useNavigate();
  const { data: reqUser } = useGetCurrentUserQuery(null, {
    skip: !localStorage.getItem('isLoggedIn'),
  });
  const [createPost, { isLoading: isPosting }] = useCreatePostMutation();
  const [descriptionText, setDescriptionText] = useState('')
  const [filePic, setFilePic] = useState(null)
  const [openEmoji, setOpenEmoji] = useState(false)

  const handleFileChange = (e) => {
    setFilePic(e.target.files[0])
  }

  const handleEmoji = (e) => {
    setDescriptionText((prev) => prev + e.emoji)
    setOpenEmoji(false)
  }

  const handlePostCreation = async () => {
    const formData = new FormData()
    formData.append('file', filePic)
    formData.append('description', descriptionText)
    try {
      await createPost(formData).unwrap();
      toast.success('Post created successfully.');
    } catch (err) {
      toast.error(err.data.businessErrornDescription);
    } finally {
      setDescriptionText('')
      setFilePic(null)
    }
  }

  return (
    <div className='addPost'>
      <div className="context">
        <img src={reqUser?.profilePicture || defaultAvatar} alt="" onClick={() => navigate(`/profile/${reqUser?.nickName}`)}/>
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
          <button onClick={handlePostCreation} disabled={isPosting }>
            {isPosting  ? "Sending..." : "Send"}
          </button>
          <div className="emoji">
            <i onClick={() => setOpenEmoji((prev) => !prev)}><BsEmojiSmileFill/></i>
            <div className="picker">
              <EmojiPicker open={openEmoji} onEmojiClick={handleEmoji}/>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export {AddPost}