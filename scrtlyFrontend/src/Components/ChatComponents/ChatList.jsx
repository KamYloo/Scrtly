import React from 'react'
import { UserInfo } from './UserInfo'
import { ChatUsersList } from './ChatUsersList'

function ChatList() {
    return (
        <div className='chatList'>
            <UserInfo />
            <ChatUsersList />
        </div>
    )
}

export { ChatList }