import React from 'react'
import { FaUserEdit, FaEllipsisH, FaAngleRight } from "react-icons/fa";
import { BsCameraVideoFill } from "react-icons/bs";
import {useNavigate} from "react-router-dom";
import defaultAvatar from "../../assets/user.jpg";
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";


// eslint-disable-next-line react/prop-types
function UserInfo() {
    const navigate = useNavigate();
    const { data: reqUser } = useGetCurrentUserQuery(null, {
        skip: !localStorage.getItem('isLoggedIn'),
    });

    return (
        <div className='userInfo'>
            <div className="user">
                <img src={reqUser?.profilePicture || defaultAvatar} alt="" onClick={() => navigate(`/profile/${reqUser.nickName}`)}/>
                {/* eslint-disable-next-line react/prop-types */}
                <h2>{reqUser?.fullName || 'Name Surname'}</h2>
            </div>
            {/*<div className="icons">*/}
            {/*    <i><FaEllipsisH /></i>*/}
            {/*    <i><BsCameraVideoFill /></i>*/}
            {/*    <i><FaUserEdit /></i>*/}
            {/*</div>*/}
        </div>
    )
}

export { UserInfo }