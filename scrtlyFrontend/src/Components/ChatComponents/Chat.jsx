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

function Chat({ chat }) {
    const [open, setOpen] = useState(false);
    const [text, setText] = useState("");
    const [stompClient, setStompClient] = useState(null);
    const [messages, setMessages] = useState([]);
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

    const endRef = useRef(null);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        endRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    const formatTimeAgo = (timestamp) => {
        return formatDistanceToNow(new Date(timestamp), { addSuffix: true });
    };

    const onMessageReceive = (message) => {
        try {
            const receivedMessage = JSON.parse(message.body);
            setMessages((prevMessages) => {
                if (receivedMessage.status === "EDITED") {
                    return prevMessages.map(msg =>
                        msg.id === receivedMessage.id ? { ...msg, ...receivedMessage } : msg
                    );
                }
                if (receivedMessage.status === "DELETED") {
                    return prevMessages.filter(msg => msg.id !== receivedMessage.id);
                }
                const exists = prevMessages.some(msg => msg.id === receivedMessage.id);
                return exists ? prevMessages : [...prevMessages, receivedMessage];
            });
        } catch (error) {
            console.error("Błąd parsowania odebranej wiadomości:", error);
        }
    };

    useEffect(() => {
        setMessages([]);

        const client = new Client({
            webSocketFactory: () => new SockJS("http://localhost:8080/api/ws"),
            reconnectDelay: 5000,
            debug: function(str) {
            },
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
            dispatch(getAllMessages(chat.id));
        }

        return () => {
            client.deactivate();
            setStompClient(null);
        };
    }, [chat?.id, dispatch]);

    useEffect(() => {
        if (chat?.id) {
            setMessages(chatMessage.messages || []);
        } else {
            setMessages([]);
        }
    }, [chat?.id, chatMessage.messages, chatMessage.deletedMessage]);

    const handleCreateNewMessage = () => {
        if (text.trim() === "" || !stompClient || !stompClient.connected) return;

        const newMessage = { chatId: chat.id, message: text };
        stompClient.publish({
            destination: `/app/chat/sendMessage/${chat.id}`,
            body: JSON.stringify(newMessage)
        });
        setText("");
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

    return (
        <div className='chat'>
            <div className="top">
                <div className="user">
                    <span>{chat.chatRoomName}</span>
                </div>
                <div className="icons">
                    <i><FaPhoneAlt /></i>
                    <i><BsCameraVideoFill /></i>
                    <i><FaInfoCircle /></i>
                </div>
            </div>
            <div className="center">
                {messages?.map((item, index) => (
                    <div
                        className={item.user.id === userData?.id ? "messageOwn" : "message"}
                        key={`${item.id}-${index}`}
                    >
                        <div className="text">
                            <p>
                                <strong>{item.user.nickName}:</strong>
                                {editingMessageId === item.id ? (
                                    <input
                                        type="text"
                                        value={editingMessageText}
                                        onChange={(e) => setEditingMessageText(e.target.value)}
                                        onKeyPress={(e) => e.key === "Enter" && handleEditMessage()}
                                    />
                                ) : (
                                    <> {item.messageText}</>
                                )}
                            </p>
                            <div className="info">
                                <span>{item.timestamp ? formatTimeAgo(item.timestamp) : "Unknown time"}</span>
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
                <div ref={endRef}></div>
            </div>
            <div className="bottom">
                <div className="icons">
                    <i><FaImage /></i>
                    <i><FaCamera /></i>
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
                        <EmojiPicker
                            onEmojiClick={(e) => {
                                setText(prev => prev + e.emoji);
                                setOpen(false);
                            }}
                        />
                    )}
                </div>
                <button className='sendButton' onClick={handleCreateNewMessage}>Send</button>
            </div>
        </div>
    );
}

export { Chat };
