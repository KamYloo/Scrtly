import React, { useState } from 'react';
import { RiUserSearchFill } from "react-icons/ri";
import { MdDeleteSweep } from "react-icons/md";
import { BsPlus } from "react-icons/bs";
import { FaMinus } from "react-icons/fa";
import { AddUser } from './AddUser.jsx';
import { useNavigate } from "react-router-dom";
import toast from "react-hot-toast";
import defaultAvatar from "../../assets/user.jpg";
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";
import {useDeleteChatMutation} from "../../Redux/services/chatApi.js";


function ChatUsersList({ chats, onChatSelect }) {
    const [addMode, setAddMode] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const { data: reqUser } = useGetCurrentUserQuery(null, {
        skip: !localStorage.getItem('isLoggedIn'),
    });
    const navigate = useNavigate();
    const [deleteChat, { isLoading: isDeleting }] = useDeleteChatMutation();

    const filteredChats = chats?.filter(chatItem => {
        if (!chatItem.participants || chatItem.participants.length === 0) return false;
        const otherParticipants = chatItem.participants.filter(user => user.id !== reqUser?.id);
        const displayName = chatItem.chatRoomName || (otherParticipants[0]?.fullName || '');
        return displayName.toLowerCase().includes(searchQuery.toLowerCase());
    });

    const toggleAddUser = () => {
        setAddMode(prev => !prev);
    };

    const handleDelete = async (chatRoomId) => {
        if (!window.confirm('Are you sure you want to delete this chat?')) return;
        try {
            await deleteChat(chatRoomId).unwrap();
            toast.success('Chat deleted successfully.');
            onChatSelect(null);
            navigate('/chat');
        } catch (err) {
            toast.error(toast.error(err.data.businessErrornDescription));
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
                                    src={otherParticipants[0]?.profilePicture || defaultAvatar}
                                    alt="User Avatar"
                                />
                                <div className="text">
                                    <span>{displayName}</span>
                                    <p>Last message...</p>
                                </div>
                                <i onClick={(e) => {
                                    e.stopPropagation();
                                    handleDelete(chatItem.id);
                                }}>
                                    {isDeleting ? <span>…</span> : <MdDeleteSweep />}
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
