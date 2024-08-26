import React, {useEffect, useState} from 'react'
import { RiUserSearchFill } from "react-icons/ri";
import { BsPlus } from "react-icons/bs";
import { FaMinus } from "react-icons/fa";
import { AddUser } from './AddUser';
import {useDispatch, useSelector} from "react-redux";
import {getUsersChat} from "../../Redux/Chat/Action.js";
import {currentUser} from "../../Redux/Auth/Action.js";

function ChatUsersList() {
    const [addMode, setAddMode] = useState(false)
    const dispatch = useDispatch();

    const { auth, chat, message } = useSelector(store => store);
    const token = localStorage.getItem('token')

    useEffect(() => {
        if (token)dispatch(currentUser(token))
    }, [dispatch, token])

    useEffect(() => {
        dispatch(getUsersChat({token}))
    }, [chat.createdChat])

    return (
        <div className='chatUserList'>
            <div className="search">
                <div className="searchBar">
                    <i><RiUserSearchFill /></i>
                    <input type="text" placeholder='Search User...' />
                </div>

                <i className='addUserBtn' onClick={() => setAddMode(((prev) => !prev))}>{addMode ? <FaMinus /> : <BsPlus />}</i>
            </div>

            {chat.chats && chat.chats.length > 0 ? (
                <div className="userList">
                    {chat.chats.map((user) => (
                        <div className="userItem" key={user.id}>
                            <img src="#" alt=""/>
                            <div className="text">
                                <span>{
                                    auth.reqUser.fullName.toLowerCase() !== user.firstPerson.fullName.toLowerCase()
                                    ? user.firstPerson.fullName
                                    : user.secondPerson.fullName}</span>
                                <p>sadasdasd</p>
                            </div>
                        </div>
                    ))}
                </div>
            ) : (
                <p>No users found</p> // Informacja, gdy nie ma wyników
            )}
            {addMode && <AddUser />}
        </div>
    )
}

export { ChatUsersList }