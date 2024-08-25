import React, { useEffect, useState } from 'react'
import "../../Styles/Profile.css"
import { IoIosSettings } from "react-icons/io";
import {currentUser} from "../../Redux/Auth/Action.js";
import {useDispatch, useSelector} from "react-redux";

function ProfileInfo() {
    const dispatch = useDispatch();

    const {auth} = useSelector(store => store)
    const token = localStorage.getItem('token')

    useEffect(() => {
        if (token)dispatch(currentUser(token))
    }, [dispatch, token])

    return (
        <div className='profileInfo'>
            <div className="userData">
                <img src="" alt="" />
                <div className="right">
                    <div className="top">
                        <p>{auth.reqUser?.fullName || 'Name Surname'}</p>
                        <button>Edit Profile</button>
                        <i><IoIosSettings /></i>
                    </div>
                    <div className="stats">
                        <p>Posts: 45</p>
                        <p>99 followers</p>
                        <p>Following: 155</p>
                    </div>
                    <div className="description">
                        <p>Name</p>
                        <span>gafgadhgdahfhgadvfavafdaerv</span>
                    </div>
                </div>
            </div>
        </div>
    )
}

export { ProfileInfo }