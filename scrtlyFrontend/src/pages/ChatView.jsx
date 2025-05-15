import React, { useEffect, useState } from 'react'
import '../Styles/ChatView.css'
import { ChatList } from '../features/Chat/ChatList.jsx'
import { Chat } from '../features/Chat/Chat.jsx'
import { useDispatch, useSelector } from "react-redux";
import { getUserChats } from "../Redux/Chat/Action.js";

function ChatView() {
  const [currentChat, setCurrentChat] = useState(null);
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 1080);
  const dispatch = useDispatch();
  const { chat } = useSelector(store => store);

  useEffect(() => {
    dispatch(getUserChats())
  }, [dispatch]);

  useEffect(() => {
    const handleResize = () => {
      setIsMobile(window.innerWidth <= 1080);
    };
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const handleCurrentChatRoom = (chatItem) => {
    setCurrentChat(chatItem)
  };

  return (
    <div className='chatView'>
      {isMobile ? (
        <>
          {currentChat ? (
            <Chat chat={currentChat} onBack={() => setCurrentChat(null)} />
          ) : (
            <ChatList chat={chat} onChatSelect={handleCurrentChatRoom} />
          )}
        </>
      ) : (
        <>
          <ChatList chat={chat} onChatSelect={handleCurrentChatRoom} />
          {currentChat ? (
            <Chat chat={currentChat} />
          ) : (
            <p className='chatNone'>Select a chat to start messaging</p>
          )}
        </>
      )}
    </div>
  )
}

export { ChatView }