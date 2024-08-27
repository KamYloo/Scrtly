import React, { useState } from 'react'
import { UserInfo } from './UserInfo'
import { ChatUsersList } from './ChatUsersList'


// eslint-disable-next-line react/prop-types
function ChatList({chat, auth, onChatSelect }) {
    const [chatListViewVisible, setChatListViewVisible] = useState(true);
    const toggleChatListView = () => {
        setChatListViewVisible(!chatListViewVisible);
    }
    return (
        <div className={`chatList ${chatListViewVisible ? 'visible' : 'hidden'}`}>
            <UserInfo toggleChatListView={toggleChatListView} auth={auth}/>
            <ChatUsersList chat = {chat} auth={auth} onChatSelect = {onChatSelect}/>
        </div>
    )
}

export { ChatList }