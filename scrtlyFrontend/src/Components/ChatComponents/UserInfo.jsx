import React, { useEffect, useState } from 'react'
import { FaUserEdit, FaEllipsisH, FaAngleRight } from "react-icons/fa";
import { BsCameraVideoFill } from "react-icons/bs";
import {useDispatch, useSelector} from "react-redux";
import {currentUser} from "../../Redux/Auth/Action.js";

// eslint-disable-next-line react/prop-types
function UserInfo({ toggleChatListView }) {
    const dispatch = useDispatch();

    const {auth} = useSelector(store => store)
    const token = localStorage.getItem('token')

    useEffect(() => {
        if (token)dispatch(currentUser(token))
    }, [dispatch, token])

    return (
        <div className='userInfo'>
            <i className='chatListBtn' onClick={toggleChatListView}><FaAngleRight /></i>
            <div className="user">
                <img src="#" alt="" />
                <h2>{auth.reqUser?.fullName || 'Name Surname'}</h2>
            </div>
            <div className="icons">
                <i><FaEllipsisH /></i>
                <i><BsCameraVideoFill /></i>
                <i><FaUserEdit /></i>
            </div>
        </div>
    )
}

export { UserInfo }