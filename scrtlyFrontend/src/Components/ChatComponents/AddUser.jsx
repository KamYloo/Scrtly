import React, {useEffect, useState} from 'react'
import {useDispatch, useSelector} from "react-redux";
import {currentUser, searchUser} from "../../Redux/Auth/Action.js";
import {createChat} from "../../Redux/Chat/Action.js";
import { MdCancel } from "react-icons/md";

function AddUser({onClose}) {
    const [keyword, setKeyword] = useState('');
    const dispatch = useDispatch();

    const { auth } = useSelector(store => store);
    const token = localStorage.getItem('token')

    useEffect(() => {
        if (token)dispatch(currentUser(token))
    }, [dispatch, token])


    const handleSearch = async (e) => {
        e.preventDefault();
        console.log(keyword);
        if (keyword.trim() !== '') {
            dispatch(searchUser({ keyword, token }));
        }
    }

    const handleCreateChat = (user) => {
        console.log(user);
        dispatch(createChat({token, data: user}));
    }


    return (
        <div className='addUser'>
            <i className='cancel' onClick={onClose}><MdCancel/></i>
            <form onSubmit={handleSearch}>
                <input type="text" placeholder='Username...' onChange={(e) => setKeyword(e.target.value)}/>
                <button>Search</button>
            </form>
            {auth.searchResults && auth.searchResults.length > 0 ? (
                <div className="userList">
                    {auth.searchResults.map((user) => (
                        <div className="user" key={user.id}>
                            <div className="detail">
                                <img src='#' alt=""/>
                                <span>{user.fullName}</span>
                            </div>
                            <button onClick={() => handleCreateChat(user.id)}>Add User</button>
                        </div>
                    ))}
                </div>
            ) : (
                <p>No users found</p>
            )}

        </div>
    )
}

export {AddUser}