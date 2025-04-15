import React, { useState } from 'react'
import { UserInfo } from './UserInfo.jsx'
import { ChatUsersList } from './ChatUsersList.jsx'


// eslint-disable-next-line react/prop-types
function ChatList({chat, onChatSelect }) {
    const [chatListViewVisible, setChatListViewVisible] = useState(true);
    const toggleChatListView = () => {
        setChatListViewVisible(!chatListViewVisible);
    }
    return (
        <div className={`chatList ${chatListViewVisible ? 'visible' : 'hidden'}`}>
            <UserInfo toggleChatListView={toggleChatListView} />
            <ChatUsersList chat = {chat} onChatSelect = {onChatSelect}/>
        </div>
    )
}

export { ChatList }