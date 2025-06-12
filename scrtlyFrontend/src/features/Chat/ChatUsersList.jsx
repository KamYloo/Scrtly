import React, { useState } from 'react';
import { RiUserSearchFill } from "react-icons/ri";
import { MdDeleteSweep } from "react-icons/md";
import { BsPlus } from "react-icons/bs";
import { FaMinus } from "react-icons/fa";
import { AddUser } from './AddUser.jsx';
import {useDispatch, useSelector} from "react-redux";
import { deleteChat } from "../../Redux/Chat/Action.js";
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";

function ChatUsersList({ chat, onChatSelect }) {
    const [addMode, setAddMode] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const { reqUser } = useSelector(state => state.auth);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const filteredChats = chat?.chats?.filter(chatItem => {
        if (!chatItem.participants || chatItem.participants.length === 0) return false;
        const otherParticipants = chatItem.participants.filter(user => user.id !== reqUser?.id);
        const displayName = chatItem.chatRoomName || (otherParticipants[0]?.fullName || '');
        return displayName.toLowerCase().includes(searchQuery.toLowerCase());
    });

    const toggleAddUser = () => {
        setAddMode(prev => !prev);
    };

    const deleteChatRoomHandler = (chatRoomId) => {
        const confirmDelete = window.confirm('Are you sure you want to delete this ChatRoom?');
        if (confirmDelete) {
            dispatch(deleteChat(chatRoomId))
                .then(() => {
                    toast.success('ChatRoom deleted successfully.');
                    navigate('/chat');
                })
                .catch(() => {
                    toast.error('Failed to delete chatRoom. Please try again.');
                });
        }
    };

    return (
        <div className='chatUserList'>
            <div className="search">
                <div className="searchBar">
                    <i><RiUserSearchFill /></i>
                    <input
                        type="text"
                        placeholder='Search User...'
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                    />
                </div>
                <i className='addUserBtn' onClick={toggleAddUser}>
                    {addMode ? <FaMinus /> : <BsPlus />}
                </i>
            </div>

            <div className="userList">
                {filteredChats && filteredChats.length > 0 ? (
                    filteredChats.map((chatItem) => {
                        const otherParticipants = chatItem.participants.filter(user => user.id !== reqUser?.id);
                        const displayName = chatItem.chatRoomName || (otherParticipants[0]?.fullName || "Unknown User");

                        return (
                            <div className="userItem" key={chatItem.id} onClick={() => onChatSelect(chatItem)}>
                                <img
                                    src={otherParticipants[0]?.profilePicture || "/default-avatar.png"}
                                    alt="User Avatar"
                                />
                                <div className="text">
                                    <span>{displayName}</span>
                                    <p>Last message...</p>
                                </div>
                                <i onClick={(e) => {
                                    e.stopPropagation();
                                    deleteChatRoomHandler(chatItem.id);
                                    onChatSelect(null);
                                }}>
                                    <MdDeleteSweep />
                                </i>
                            </div>
                        );
                    })
                ) : (
                    <p>No users found</p>
                )}
            </div>

            {addMode && <AddUser onClose={toggleAddUser} />}
        </div>
    );
}

export { ChatUsersList };
