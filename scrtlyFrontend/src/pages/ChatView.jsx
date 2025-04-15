import React, {useEffect, useState} from 'react'
import '../Styles/ChatView.css'
import { ChatList } from '../features/Chat/ChatList.jsx'
import { Chat } from '../features/Chat/Chat.jsx'
import {useDispatch, useSelector} from "react-redux";
import {getUserChats} from "../Redux/Chat/Action.js";

function ChatView() {
    const [currentChat, setCurrentChat] = useState(null);
    const dispatch = useDispatch();
    const { chat } = useSelector(store => store);

    useEffect(() => {
        dispatch(getUserChats())
    }, [chat.createdChat, chat.deletedChat, dispatch]);

    const handleCurrentChatRoom = (chatItem) => {
        setCurrentChat(chatItem)
    };

    /*useEffect(() => {
        if (chat.chats && chat.chats.length > 0 && !currentChat) {
            setCurrentChat(chat.chats[0])
        }
    }, [chat.chats, currentChat])*/

    return (
        <div className='chatView'>
            <ChatList chat={chat} onChatSelect={handleCurrentChatRoom}/>
            {currentChat ? <Chat chat={currentChat} /> : <p className='chatNone'>Select a chat to start messaging</p>
            }
        </div>
    )
}

export { ChatView }