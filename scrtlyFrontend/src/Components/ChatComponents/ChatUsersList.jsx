import React, { useState} from 'react'
import { RiUserSearchFill } from "react-icons/ri";
import { MdDeleteSweep } from "react-icons/md";
import { BsPlus } from "react-icons/bs";
import { FaMinus } from "react-icons/fa";
import { AddUser } from './AddUser';
import {BASE_API_URL} from "../../config/api.js";
import {useDispatch, useSelector} from "react-redux";
import {deleteChat} from "../../Redux/Chat/Action.js";
import {useNavigate} from "react-router-dom";
import toast from "react-hot-toast";

// eslint-disable-next-line react/prop-types
function ChatUsersList({chat, onChatSelect }) {
    const [addMode, setAddMode] = useState(false)
    const [searchQuery, setSearchQuery] = useState('');
    const {auth } = useSelector(store => store);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    // eslint-disable-next-line react/prop-types
    const filteredChats = chat?.chats?.filter(chatItem => {
        // eslint-disable-next-line react/prop-types
        const isUserFirstPerson = chatItem?.firstPerson.id === auth?.reqUser.id
        const otherPerson = isUserFirstPerson ? chatItem.secondPerson : chatItem.firstPerson

        return otherPerson.fullName.toLowerCase().includes(searchQuery.toLowerCase())
    })

    const toggleAdUser = () => {
        setAddMode((prev) => !prev);
    };
    const deleteChatRoomHandler = (chatRoomId) => {
        const confirmDelete = window.confirm('Are you sure you want to delete this ChatRoom?');
        if (confirmDelete) {
            dispatch(deleteChat(chatRoomId)).then(() => {
                toast.success('ChatRoom deleted successfully.');
                navigate('/chat')
            }).catch(() => {
                toast.error('Failed to delete chatRoom. Please try again.');
            });
        }
    }

    return (
        <div className='chatUserList'>
            <div className="search">
                <div className="searchBar">
                    <i><RiUserSearchFill/></i>
                    <input type="text" placeholder='Search User...' value={searchQuery}
                           onChange={(e) => setSearchQuery(e.target.value)}/>
                </div>

                <i className='addUserBtn' onClick={toggleAdUser}>{addMode ? <FaMinus/> :
                    <BsPlus/>}</i>
            </div>

            <div className="userList">
                {filteredChats.length > 0 ? (
                    filteredChats.map((chatItem) => {
                        // eslint-disable-next-line react/prop-types
                        const isUserFirstPerson = chatItem?.firstPerson.id === auth?.reqUser.id
                        const otherPerson = isUserFirstPerson ? chatItem?.secondPerson : chatItem?.firstPerson

                        return (
                            <div className="userItem" key={chatItem.id} onClick={() => onChatSelect(chatItem)}>
                                <img src={`${BASE_API_URL}/${otherPerson?.profilePicture || ''}`} alt=""/>
                                <div className="text">
                                    <span>{otherPerson.fullName}</span>
                                    <p>afdsafdsgad</p>
                                </div>
                                <i onClick={(e) => {
                                    e.stopPropagation();
                                    deleteChatRoomHandler(chatItem.id);
                                    onChatSelect(null)
                                }}>
                                    <MdDeleteSweep/>
                                </i>
                            </div>

                        )
                    })
                ) : (
                    <p>No users found</p>
                )}
            </div>
            {addMode && <AddUser onClose={toggleAdUser}/>}
        </div>
    )
}

export {ChatUsersList}