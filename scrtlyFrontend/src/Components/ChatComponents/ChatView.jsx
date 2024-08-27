import React, {useEffect, useState} from 'react'
import '../../Styles/ChatView.css'
import { ChatList } from './ChatList'
import { Chat } from './Chat'
import {useDispatch, useSelector} from "react-redux";
import {currentUser} from "../../Redux/Auth/Action.js";
import {getUsersChat} from "../../Redux/Chat/Action.js";

function ChatView() {
    const [currentChat, setCurrentChat] = useState(null);

    const dispatch = useDispatch();

    const { auth, chat, message } = useSelector(store => store);
    const token = localStorage.getItem('token')

    useEffect(() => {
        if (token)dispatch(currentUser(token))
    }, [dispatch, token])

    useEffect(() => {
        dispatch(getUsersChat({token}))
    }, [chat.createdChat])

    const handleCurrentChatRoom = (chatItem) => {
        setCurrentChat(chatItem)
    };

    useEffect(() => {
        if (chat.chats && chat.chats.length > 0 && !currentChat) {
            setCurrentChat(chat.chats[0])
        }
    }, [chat.chats, currentChat])

    return (
        <div className='chatView'>
            <ChatList chat={chat} auth={auth} onChatSelect={handleCurrentChatRoom}/>
            {currentChat ? <Chat chat={currentChat} auth={auth}/> : <p>Select a chat to start messaging</p>
            }
        </div>
    )
}

export { ChatView }