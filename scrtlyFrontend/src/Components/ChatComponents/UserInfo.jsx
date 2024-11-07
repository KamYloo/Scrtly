import React from 'react'
import { FaUserEdit, FaEllipsisH, FaAngleRight } from "react-icons/fa";
import { BsCameraVideoFill } from "react-icons/bs";
import {BASE_API_URL} from "../../config/api.js";
import {useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";

// eslint-disable-next-line react/prop-types
function UserInfo({ toggleChatListView }) {

    const navigate = useNavigate();
    const {auth} = useSelector(store => store);

    return (
        <div className='userInfo'>
            <i className='chatListBtn' onClick={toggleChatListView}><FaAngleRight /></i>
            <div className="user">
                <img src={`${BASE_API_URL}/${auth.reqUser?.profilePicture || ''}`} alt="" onClick={() => navigate(`/profile/${auth.reqUser.id}`)}/>
                {/* eslint-disable-next-line react/prop-types */}
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