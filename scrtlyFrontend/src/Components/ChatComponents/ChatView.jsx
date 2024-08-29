import React, {useEffect, useState} from 'react'
import '../../Styles/ChatView.css'
import { ChatList } from './ChatList'
import { Chat } from './Chat'
import {useDispatch, useSelector} from "react-redux";
import {currentUser} from "../../Redux/Auth/Action.js";
import {getUsersChat} from "../../Redux/Chat/Action.js";

function ChatView({token, auth}) {
    const [currentChat, setCurrentChat] = useState(null);

    const dispatch = useDispatch();

    const { chat } = useSelector(store => store);




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
            {currentChat ? <Chat chat={currentChat} auth={auth} token={token}/> : <p>Select a chat to start messaging</p>
            }
        </div>
    )
}

export { ChatView }