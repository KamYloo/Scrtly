import React from 'react'
import { FaUserEdit, FaEllipsisH, FaAngleRight } from "react-icons/fa";
import { BsCameraVideoFill } from "react-icons/bs";
import {useNavigate} from "react-router-dom";

// eslint-disable-next-line react/prop-types
function UserInfo() {

    const navigate = useNavigate();
    const userData = (() => { try { return JSON.parse(localStorage.getItem("user")) || null; } catch { return null; } })();


    return (
        <div className='userInfo'>
            <div className="user">
                <img src={userData?.profilePicture } alt="" onClick={() => navigate(`/profile/${userData.nickName}`)}/>
                {/* eslint-disable-next-line react/prop-types */}
                <h2>{userData?.fullName || 'Name Surname'}</h2>
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