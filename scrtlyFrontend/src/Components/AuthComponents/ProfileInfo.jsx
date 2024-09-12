import React, { useEffect, useState } from 'react'
import "../../Styles/Profile.css"
import { IoIosSettings } from "react-icons/io";
import {useNavigate, useParams} from 'react-router-dom';
import {BASE_API_URL} from "../../config/api.js";
import {findUserById} from "../../Redux/Auth/Action.js";
import {useDispatch} from "react-redux";


function ProfileInfo({auth, token}) {
    const navigate = useNavigate()
    const dispatch = useDispatch()
    const {userId} = useParams();

    useEffect(() => {
        console.log(userId)
        dispatch(findUserById(userId))
    }, [userId]);
    return (
        <div className='profileInfo'>
            <div className="userData">
                <img src={`${BASE_API_URL}/${auth.findUser?.profilePicture || ''}`} alt="" />
                <div className="right">
                    <div className="top">
                        <p>{auth.findUser?.fullName || 'Name Surname'}</p>
                        <button className="followBtn" onClick={() => {
                            navigate("/profile/edit")
                        }}>Follow
                        </button>
                        <button onClick={() => {
                            navigate("/profile/edit")
                        }}>Edit Profile
                        </button>
                        <i><IoIosSettings/></i>
                    </div>
                    <div className="stats">
                        <p>Posts: 45</p>
                        <p>99 followers</p>
                        <p>Following: 155</p>
                    </div>
                    <div className="description">
                        <p>{auth.findUser?.fullName || 'Name'}</p>
                        <span>{auth.findUser?.description || ''}</span>
                    </div>
                </div>
            </div>
        </div>
    )
}

export { ProfileInfo }