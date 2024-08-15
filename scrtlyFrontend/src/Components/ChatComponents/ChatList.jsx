import React, { useState } from 'react'
import { UserInfo } from './UserInfo'
import { ChatUsersList } from './ChatUsersList'


function ChatList() {
    const [chatListViewVisible, setChatListViewVisible] = useState(true);
    const toggleChatListView = () => {
        setChatListViewVisible(!chatListViewVisible);
    }
    return (
        <div className={`chatList ${chatListViewVisible ? 'visible' : 'hidden'}`}>
            <UserInfo toggleChatListView={toggleChatListView} />
            <ChatUsersList />
        </div>
    )
}

export { ChatList }