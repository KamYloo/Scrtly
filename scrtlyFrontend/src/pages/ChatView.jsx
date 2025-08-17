import React, { useEffect, useState } from 'react'
import '../Styles/ChatView.css'
import { ChatList } from '../features/Chat/ChatList.jsx'
import { Chat } from '../features/Chat/Chat.jsx'
import {useGetUserChatsQuery} from "../Redux/services/chatApi.js";

function ChatView() {
  const [currentChat, setCurrentChat] = useState(null);
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 1080);
  const {
    data: chats = [],
  } = useGetUserChatsQuery();

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
            <ChatList chats={chats} onChatSelect={handleCurrentChatRoom} />
          )}
        </>
      ) : (
        <>
          <ChatList chats={chats} onChatSelect={handleCurrentChatRoom} />
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