import React, { useState } from 'react'
import { RiUserSearchFill } from "react-icons/ri";
import { BsLine, BsPlus } from "react-icons/bs";
import { FaMinus } from "react-icons/fa";

function ChatUsersList() {
    const [addMode, setAddMode] = useState(false)
    return (
        <div className='chatUserList'>
            <div className="search">
                <div className="searchBar">
                    <i><RiUserSearchFill /></i>
                    <input type="text" placeholder='Search User...' />
                </div>

                <i className='addUser' onClick={() => setAddMode(((prev) => !prev))}>{addMode ? <FaMinus /> : <BsPlus />}</i>
            </div>
            <div className="userItem">
                <img src="#" alt="" />
                <div className="text">
                    <span>Name Surname</span>
                    <p>halohalohalo</p>
                </div>
            </div>
            <div className="userItem">
                <img src="#" alt="" />
                <div className="text">
                    <span>Name Surname</span>
                    <p>halohalohalo</p>
                </div>
            </div>
            <div className="userItem">
                <img src="#" alt="" />
                <div className="text">
                    <span>Name Surname</span>
                    <p>halohalohalo</p>
                </div>
            </div>
            <div className="userItem">
                <img src="#" alt="" />
                <div className="text">
                    <span>Name Surname</span>
                    <p>halohalohalo</p>
                </div>
            </div>
            <div className="userItem">
                <img src="#" alt="" />
                <div className="text">
                    <span>Name Surname</span>
                    <p>halohalohalo</p>
                </div>
            </div>
            <div className="userItem">
                <img src="#" alt="" />
                <div className="text">
                    <span>Name Surname</span>
                    <p>halohalohalo</p>
                </div>
            </div>
            <div className="userItem">
                <img src="#" alt="" />
                <div className="text">
                    <span>Name Surname</span>
                    <p>halohalohalo</p>
                </div>
            </div>
            <div className="userItem">
                <img src="#" alt="" />
                <div className="text">
                    <span>Name Surname</span>
                    <p>halohalohalo</p>
                </div>
            </div>
            <div className="userItem">
                <img src="#" alt="" />
                <div className="text">
                    <span>Name Surname</span>
                    <p>halohalohalo</p>
                </div>
            </div>
            <div className="userItem">
                <img src="#" alt="" />
                <div className="text">
                    <span>Name Surname</span>
                    <p>halohalohalo</p>
                </div>
            </div>
        </div>
    )
}

export { ChatUsersList }