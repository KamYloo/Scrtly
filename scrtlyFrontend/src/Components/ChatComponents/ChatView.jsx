import React from 'react'
import '../../Styles/ChatView.css'
import { ChatList } from './ChatList'
import { Chat } from './Chat'

function ChatView() {
    return (
        <div className='chatView'>
            <ChatList />
            <Chat />
        </div>
    )
}

export { ChatView }