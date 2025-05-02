// Chat.jsx
import React, { useEffect, useRef, useState } from 'react';
import EmojiPicker from 'emoji-picker-react';
import { FaPhoneAlt, FaInfoCircle, FaImage, FaCamera, FaMicrophone } from "react-icons/fa";
import { BsCameraVideoFill, BsEmojiSmileFill } from "react-icons/bs";
import { useDispatch, useSelector } from "react-redux";
import { getAllMessages } from "../../Redux/ChatMessage/Action.js";
import { formatDistanceToNow } from 'date-fns';
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client/dist/sockjs";
import { useNavigate } from "react-router-dom";

function Chat({ chat, onBack }) {
    const [open, setOpen] = useState(false);
    const [text, setText] = useState("");
    const [stompClient, setStompClient] = useState(null);
    const [editingMessageId, setEditingMessageId] = useState(null);
    const [editingMessageText, setEditingMessageText] = useState("");

    const { chatMessage } = useSelector(store => store);
    const userData = (() => {
        try {
            return JSON.parse(localStorage.getItem("user")) || null;
        } catch {
            return null;
        }
    })();

    const messagesEndRef = useRef(null);
    const messagesContainerRef = useRef(null);
    const prevScrollHeightRef = useRef(0);

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        const container = messagesContainerRef.current;
        if (!container) return;
        if (chatMessage.page === 0) {
            messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
        } else {
            const newScrollHeight = container.scrollHeight;
            container.scrollTop = newScrollHeight - prevScrollHeightRef.current;
        }
    }, [chatMessage.messages, chatMessage.page]);

    const formatTimeAgo = (timestamp) => {
        return formatDistanceToNow(new Date(timestamp), { addSuffix: true });
    };

    const onMessageReceive = (message) => {
        try {
            const receivedMessage = JSON.parse(message.body);
            if (receivedMessage.status === "EDITED") {
                dispatch({ type: 'EDIT_MESSAGE', payload: receivedMessage });
            } else if (receivedMessage.status === "DELETED") {
                dispatch({ type: 'DELETE_MESSAGE', payload: receivedMessage });
            } else {
                dispatch({ type: 'ADD_NEW_MESSAGE', payload: receivedMessage });
            }

            setTimeout(() => {
                messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
            }, 100);
        } catch (error) {
            console.error("Błąd parsowania odebranej wiadomości:", error);
        }
    };

    useEffect(() => {
        const client = new Client({
            webSocketFactory: () => new SockJS("http://localhost:8080/api/ws"),
            reconnectDelay: 5000,
            onConnect: (frame) => {
                console.log("Połączono STOMP:", frame);
                if (chat?.id) {
                    client.subscribe(`/topic/room/${chat.id}`, onMessageReceive);
                }
            },
            onStompError: (frame) => {
                console.error("Błąd STOMP:", frame.headers['message'], frame.body);
            }
        });

        client.activate();
        setStompClient(client);

        if (chat?.id) {
            dispatch(getAllMessages(chat.id, 0));
        }

        return () => {
            client.deactivate();
            setStompClient(null);
        };
    }, [chat?.id, dispatch]);

    const handleScroll = () => {
        const container = messagesContainerRef.current;
        if (container.scrollTop === 0 && !chatMessage.last && !chatMessage.loading) {
            prevScrollHeightRef.current = container.scrollHeight;
            dispatch(getAllMessages(chat.id, chatMessage.page + 1));
        }
    };

    const handleCreateNewMessage = () => {
        if (text.trim() === "" || !stompClient || !stompClient.connected) return;
        const newMessage = { message: text };
        stompClient.publish({
            destination: `/app/chat/sendMessage/${chat.id}`,
            body: JSON.stringify(newMessage)
        });
        setText("");

        setTimeout(() => {
            messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
        }, 100);
    };

    const startEditing = (message) => {
        setEditingMessageId(message.id);
        setEditingMessageText(message.messageText);
    };

    const handleEditMessage = () => {
        if (editingMessageText.trim() === "" || !stompClient || !stompClient.connected) return;
        const payload = { id: editingMessageId, message: editingMessageText };
        stompClient.publish({
            destination: `/app/chat/editMessage/${chat.id}`,
            body: JSON.stringify(payload)
        });
        setEditingMessageId(null);
        setEditingMessageText("");
    };

    const handleDeleteMessage = (messageId) => {
        if (window.confirm('Czy na pewno chcesz usunąć tę wiadomość?')) {
            stompClient.publish({
                destination: `/app/chat/deleteMessage/${chat.id}`,
                body: JSON.stringify({ messageId })
            });
        }
    };

    const otherPerson = chat.participants.find(user => user.id !== userData?.id);
    return (
        <div className='chat'>
            <div className="top">
                <div className="user">
                    {onBack && (
                        <button className="backBtn" onClick={onBack}>Back</button>
                    )}
                    {chat?.participants && chat.participants.length === 2 ? (
                        <>
                            <img
                                src={otherPerson?.profilePicture}
                                alt={otherPerson?.fullName}
                                onClick={() => navigate(`/profile/${otherPerson?.nickName}`)}
                            />
                            <div className="userData">
                                <span>{otherPerson?.fullName}</span>
                                <p>{otherPerson?.description || ''}</p>
                            </div>
                        </>
                    ) : (
                        <div className="userData">
                            <span>{chat.chatRoomName}</span>
                        </div>
                    )}
                </div>

                <div className="icons">
                    <i><FaPhoneAlt /></i>
                    <i><BsCameraVideoFill /></i>
                    <i><FaInfoCircle /></i>
                </div>
            </div>
            <div className="center" ref={messagesContainerRef} onScroll={handleScroll}>
                {chatMessage.messages?.map((item, index) => (
                    <div
                        className={item.user.id === userData?.id ? "messageOwn" : "message"}
                        key={`${item.id}-${index}`}
                    >
                        <img
                            src={otherPerson?.profilePicture}
                            alt={otherPerson?.fullName}
                            onClick={() => navigate(`/profile/${otherPerson?.nickName}`)}
                        />
                        <div className="text">
                            <p>
                                <strong>{item.user.nickName}:</strong>
                                {editingMessageId === item.id ? (
                                    <input
                                        type="text"
                                        className="editMessageInput"
                                        value={editingMessageText}
                                        onChange={(e) => setEditingMessageText(e.target.value)}
                                        onKeyPress={(e) => e.key === "Enter" && handleEditMessage()}
                                    />
                                ) : (
                                    <> {item.messageText}</>
                                )}
                            </p>
                            <div className="info">
                                <span>{item.lastModifiedDate ? formatTimeAgo(item.lastModifiedDate) : formatTimeAgo(item.createDate)}</span>
                                {item.user.id === userData?.id && (
                                    <>
                                        {editingMessageId === item.id ? (
                                            <>
                                                <button onClick={handleEditMessage}>Zapisz</button>
                                                <button onClick={() => setEditingMessageId(null)}>Anuluj</button>
                                            </>
                                        ) : (
                                            <>
                                                <button onClick={() => startEditing(item)}>Edytuj</button>
                                                <button onClick={() => handleDeleteMessage(item.id)}>Usuń</button>
                                            </>
                                        )}
                                    </>
                                )}
                            </div>
                        </div>
                    </div>
                ))}
                <div ref={messagesEndRef}></div>
            </div>
            <div className="bottom">
                <div className="icons">
                    <i><FaImage/></i>
                    <i><FaCamera/></i>
                    <i><FaMicrophone /></i>
                </div>
                <input
                    type="text"
                    placeholder='Write message...'
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleCreateNewMessage()}
                />
                <div className="emoji">
                    <i onClick={() => setOpen(prev => !prev)}><BsEmojiSmileFill /></i>
                    {open && (
                        <div className="emojiPickerWrapper">
                            <EmojiPicker
                                onEmojiClick={(e) => {
                                    setText(prev => prev + e.emoji);
                                    setOpen(false);
                                }}
                            />
                        </div>
                    )}
                </div>
                <button className='sendButton' onClick={handleCreateNewMessage}>Send</button>
            </div>
        </div>
    );
}

export { Chat };
