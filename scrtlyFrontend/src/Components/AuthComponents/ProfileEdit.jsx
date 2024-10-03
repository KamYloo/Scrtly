import React, {useEffect, useState} from 'react'
import { BASE_API_URL } from "../../config/api"
import "../../Styles/Profile.css"
import {useDispatch, useSelector} from "react-redux";
import {updateUser} from "../../Redux/Auth/Action.js";
import {useNavigate} from "react-router-dom";

function ProfileEdit() {
  const dispatch = useDispatch()
  const {auth} = useSelector(store => store);
  const [fullName, setFullName] = useState(auth.reqUser?.fullName || "")
  const [description, setDescription] = useState(auth.reqUser?.description || "")
  const [profilePicture, setProfilePicture] = useState(null)
  const [preview, setPreview] = useState('');
  const navigate = useNavigate()


  const handleFileChange = (e) => {
    setProfilePicture(e.target.files[0])
  };

  const handleSubmit = (e) => {
    e.preventDefault()

    dispatch(updateUser({data:{fullName: fullName, profilePicture:profilePicture, description:description.trim() || ""}}))
    navigate(`/profile/${auth.reqUser.id}`)
    window.location.reload()
  }

  useEffect(() => {
    if (profilePicture) {
      const previewUrl = URL.createObjectURL(profilePicture)
      setPreview(previewUrl)
      return () => {
        URL.revokeObjectURL(previewUrl)
      }
    }
  }, [profilePicture])

  return (
    <div className='profileEdit'>
      <div className="editDiv">
        <div className="edit">
          <div className="title">
            <h2>Edit Profile</h2>
          </div>
          <form onSubmit={handleSubmit}>
            <div className="editAvatar">
              <div className="left">
                <img src={preview} alt=""/>
                <p>{auth.reqUser?.fullName || 'Name Surname'}</p>
              </div>
              <div className="right">
                <input type="file" onChange={handleFileChange}/>
                <button type="button" onClick={() => document.querySelector('input[type="file"]').click()}>
                  Change Photo
                </button>
              </div>
            </div>
            <div className="editFullName">
              <h4>FullName</h4>
              <input type="text" value={fullName} onChange={(e) => setFullName(e.target.value)}></input>
            </div>
            <div className="editDescription">
              <h4>Description</h4>
              <textarea value={description} onChange={(e) => setDescription(e.target.value)}></textarea>
            </div>
            <button type="submit" className='submit'>Send</button>
          </form>

        </div>
      </div>
    </div>
  )
}

export { ProfileEdit }