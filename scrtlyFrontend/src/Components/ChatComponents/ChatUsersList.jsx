import React, { useState} from 'react'
import { RiUserSearchFill } from "react-icons/ri";
import { BsPlus } from "react-icons/bs";
import { FaMinus } from "react-icons/fa";
import { AddUser } from './AddUser';

// eslint-disable-next-line react/prop-types
function ChatUsersList({chat, auth, onChatSelect }) {
    const [addMode, setAddMode] = useState(false)
    const [searchQuery, setSearchQuery] = useState('');

    // eslint-disable-next-line react/prop-types
    const filteredChats = chat.chats.filter(chatItem => {
        // eslint-disable-next-line react/prop-types
        const isUserFirstPerson = chatItem.firstPerson.id === auth.reqUser.id
        const otherPerson = isUserFirstPerson ? chatItem.secondPerson : chatItem.firstPerson

        return otherPerson.fullName.toLowerCase().includes(searchQuery.toLowerCase())
    })

    return (
        <div className='chatUserList'>
            <div className="search">
                <div className="searchBar">
                    <i><RiUserSearchFill/></i>
                    <input type="text" placeholder='Search User...' value={searchQuery}
                           onChange={(e) => setSearchQuery(e.target.value)}/>
                </div>

                <i className='addUserBtn' onClick={() => setAddMode(((prev) => !prev))}>{addMode ? <FaMinus/> :
                    <BsPlus/>}</i>
            </div>

            <div className="userList">
                {filteredChats.length > 0 ? (
                    filteredChats.map((chatItem) => {
                        // eslint-disable-next-line react/prop-types
                        const isUserFirstPerson = chatItem.firstPerson.id === auth.reqUser.id
                        const otherPerson = isUserFirstPerson ? chatItem.secondPerson : chatItem.firstPerson

                        return (
                            <div className="userItem" key={chatItem.id} onClick={()=> onChatSelect(chatItem)}>
                                <img src="#" alt=""/>
                                <div className="text">
                                    <span>{otherPerson.fullName}</span>
                                    <p>afdsafdsgad</p>
                                </div>
                            </div>
                        )
                    })
                ) : (
                    <p>No users found</p>
                )}
            </div>
            {addMode && <AddUser/>}
        </div>
    )
}

export {ChatUsersList}