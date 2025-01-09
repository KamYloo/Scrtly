import React, {useEffect, useState} from 'react'
import "../../Styles/Profile.css"
import {useDispatch} from "react-redux";
import {updateUser} from "../../Redux/AuthService/Action.js";
import {useNavigate} from "react-router-dom";
import toast from "react-hot-toast";

function ProfileEdit() {
  const dispatch = useDispatch()
  const userData = (() => { try { return JSON.parse(localStorage.getItem("user")) || null; } catch { return null; } })();
  const [fullName, setFullName] = useState(userData?.fullName || "")
  const [description, setDescription] = useState(userData?.description || "")
  const [profilePicture, setProfilePicture] = useState(null)
  const [preview, setPreview] = useState(userData?.profilePicture ? userData.profilePicture : '');
  const navigate = useNavigate()



  const handleFileChange = (e) => {
    setProfilePicture(e.target.files[0])
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const formData = new FormData();
    const jsonBlob = new Blob([JSON.stringify({ fullName, description })], {
      type: 'application/json',
    });
    formData.append('userDetails', jsonBlob);
    if (profilePicture) {
      formData.append("profilePicture", profilePicture);
    }

    dispatch(updateUser(formData))
        .then((res) => {
          const updatedUserData = { ...userData, profilePicture: res.profilePicture, description: res.description, fullName: res.fullName };
          localStorage.setItem("user", JSON.stringify(updatedUserData));
          toast.success('User updated successfully.');
          navigate(`/profile/${userData?.nickName}?reload=true`);
        })
        .catch(() => {
          toast.error('Failed to update user. Please try again.');
        });
  };

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
                <p>{userData?.fullName || 'Name Surname'}</p>
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