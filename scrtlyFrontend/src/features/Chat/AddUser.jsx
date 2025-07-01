import React, { useState } from 'react';
import { useDispatch, useSelector } from "react-redux";
import { searchUser } from "../../Redux/AuthService/Action.js";
import { createChat } from "../../Redux/Chat/Action.js";
import { MdCancel } from "react-icons/md";
import defaultAvatar from "../../assets/user.jpg";


function AddUser({ onClose }) {
    const [keyword, setKeyword] = useState('');
    const [selectedUsers, setSelectedUsers] = useState([]);
    const [groupName, setGroupName] = useState('');
    const dispatch = useDispatch();
    const { auth } = useSelector(store => store);

    const handleSearch = (e) => {
        e.preventDefault();
        if (keyword.trim() !== '') {
            dispatch(searchUser({ keyword }));
        }
    };

    const toggleUserSelection = (user) => {
        if (selectedUsers.find(u => u.id === user.id)) {
            setSelectedUsers(selectedUsers.filter(u => u.id !== user.id));
        } else {
            setSelectedUsers([...selectedUsers, user]);
        }
    };

    const handleCreateChat = () => {
        if (selectedUsers.length === 0) return;
        if (selectedUsers.length > 1 && groupName.trim() === '') {
            alert('Please enter a group name.');
            return;
        }
        const userIds = selectedUsers.map(user => user.id);
        dispatch(createChat(userIds, selectedUsers.length > 1 ? groupName : ""));
        onClose();
    };

    return (
        <div className='addUser'>
            <i className='cancel' onClick={onClose}><MdCancel /></i>
            <form onSubmit={handleSearch}>
                <input
                    type="text"
                    placeholder='Username...'
                    onChange={(e) => setKeyword(e.target.value)}
                />
                <button type="submit" className="styledButton">Search</button>
            </form>
            {auth.searchResults && auth.searchResults.length > 0 ? (
                <div className="userList">
                    {auth.searchResults.map((user) => (
                        <div
                            key={user.id}
                            className={`user ${selectedUsers.find(u => u.id === user.id) ? 'selected' : ''}`}
                            onClick={() => toggleUserSelection(user)}
                        >
                            <div className="detail">
                                <img src={user?.profilePicture || defaultAvatar} alt={user.fullName} />
                                <span>{user.fullName}</span>
                            </div>
                        </div>
                    ))}
                </div>
            ) : (
                <p>No users found</p>
            )}
            {selectedUsers.length > 1 && (
                <div className="groupNameInput">
                    <input
                        type="text"
                        placeholder="Enter group name"
                        value={groupName}
                        onChange={(e) => setGroupName(e.target.value)}
                    />
                </div>
            )}
            <button
                onClick={handleCreateChat}
                className="styledButton"
                style={{ marginTop: selectedUsers.length === 1 ? '40px' : '20px', display: 'block', marginLeft: 'auto', marginRight: 'auto' }}
            >
                Create Chat
            </button>
        </div>
    );
}

export { AddUser };
