import React, {useEffect, useState} from 'react'
import "../../Styles/Profile.css"
import {useNavigate} from "react-router-dom";
import toast from "react-hot-toast";
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";
import {useUpdateUserMutation} from "../../Redux/services/userApi.js";

function ProfileEdit() {
  const { data: reqUser } = useGetCurrentUserQuery(null, {
    skip: !localStorage.getItem('isLoggedIn'),
  });
  const [updateUser, { isLoading: isUpdating }] = useUpdateUserMutation();
  const [fullName, setFullName] = useState(reqUser?.fullName || "")
  const [description, setDescription] = useState(reqUser?.description || "")
  const [profilePicture, setProfilePicture] = useState(null)
  const [preview, setPreview] = useState(reqUser?.profilePicture ? reqUser.profilePicture : '');
  const navigate = useNavigate()

  useEffect(() => {
    if (reqUser) {
      setFullName(reqUser.fullName);
      setDescription(reqUser.description || '');
      setPreview(reqUser.profilePicture || '');
    }
  }, [reqUser]);

  useEffect(() => {
    if (profilePicture) {
      const previewUrl = URL.createObjectURL(profilePicture)
      setPreview(previewUrl)
      return () => {
        URL.revokeObjectURL(previewUrl)
      }
    }
  }, [profilePicture])

  const handleFileChange = (e) => {
    setProfilePicture(e.target.files[0])
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();
    const jsonBlob = new Blob([JSON.stringify({ fullName, description })], {
      type: 'application/json',
    });
    formData.append('userDetails', jsonBlob);
    if (profilePicture) {
      formData.append("profilePicture", profilePicture);
    }

    try {
      const result = await updateUser(formData).unwrap();
      toast.success('Profile updated successfully.');
      navigate(`/profile/${result.nickName}`);
    } catch (err) {
      toast.error(err.data.businessErrornDescription);
    }
  };

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
                <p>{reqUser?.fullName || 'Name Surname'}</p>
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
            <button type="submit" className='submit' disabled={isUpdating}>
              {isUpdating ? "Editing..." : "Send"}
            </button>
          </form>

        </div>
      </div>
    </div>
  )
}

export { ProfileEdit }