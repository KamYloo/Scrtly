import React from 'react'
import { FaUserEdit, FaEllipsisH, FaAngleRight } from "react-icons/fa";
import { BsCameraVideoFill } from "react-icons/bs";

// eslint-disable-next-line react/prop-types
function UserInfo({ toggleChatListView, auth}) {

    return (
        <div className='userInfo'>
            <i className='chatListBtn' onClick={toggleChatListView}><FaAngleRight /></i>
            <div className="user">
                <img src="#" alt="" />
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