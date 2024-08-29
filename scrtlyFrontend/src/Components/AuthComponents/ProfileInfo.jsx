import React, { useEffect, useState } from 'react'
import "../../Styles/Profile.css"
import { IoIosSettings } from "react-icons/io";
import { useNavigate } from 'react-router-dom';
import {BASE_API_URL} from "../../config/api.js";

function ProfileInfo({auth, token}) {
    const navigate = useNavigate()

    return (
        <div className='profileInfo'>
            <div className="userData">
                <img src={`${BASE_API_URL}/${auth.reqUser?.profilePicture || ''}`} alt="" />
                <div className="right">
                    <div className="top">
                        <p>{auth.reqUser?.fullName || 'Name Surname'}</p>
                        <button onClick={()=>{navigate("/profile/edit")}}>Edit Profile</button>
                        <i><IoIosSettings /></i>
                    </div>
                    <div className="stats">
                        <p>Posts: 45</p>
                        <p>99 followers</p>
                        <p>Following: 155</p>
                    </div>
                    <div className="description">
                        <p>{auth.reqUser?.fullName || 'Name'}</p>
                        <span>{auth.reqUser?.description || ''}</span>
                    </div>
                </div>
            </div>
        </div>
    )
}

export { ProfileInfo }