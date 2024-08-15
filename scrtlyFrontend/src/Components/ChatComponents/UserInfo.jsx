import React from 'react'
import { FaUserEdit, FaEllipsisH } from "react-icons/fa";
import { BsCameraVideoFill } from "react-icons/bs";

function UserInfo() {
    return (
        <div className='userInfo'>
            <div className="user">
                <img src="#" alt="" />
                <h2>Name Surname</h2>
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