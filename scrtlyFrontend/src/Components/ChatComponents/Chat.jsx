import React, { useEffect, useRef, useState } from 'react';
import EmojiPicker from 'emoji-picker-react';
import { FaPhoneAlt, FaInfoCircle, FaImage, FaCamera, FaMicrophone } from "react-icons/fa";
import { BsCameraVideoFill, BsEmojiSmileFill } from "react-icons/bs";
import { useDispatch, useSelector } from "react-redux";
import { createChatMessage, deleteChatMessage, getAllMessages } from "../../Redux/ChatMessage/Action.js";
import { formatDistanceToNow } from 'date-fns';
import SockJs from "sockjs-client/dist/sockjs";
import { over } from "stompjs";
import { useNavigate } from "react-router-dom";

function Chat({ chat }) {
    const [open, setOpen] = useState(false);
    const [text, setText] = useState("");
    const [stompClient, setStompClient] = useState(null);
    const [isConnected, setConnected] = useState(false);
    const [messages, setMessages] = useState([]);

    const { chatMessage } = useSelector(store => store);
    const userData = (() => { try { return JSON.parse(localStorage.getItem("user")) || null; } catch { return null; } })();
    const endRef = useRef(null);
    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        endRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    const formatTimeAgo = (timestamp) => {
        return formatDistanceToNow(new Date(timestamp), { addSuffix: true });
    };

    const handleEmoji = (e) => {
        setText((prev) => prev + e.emoji);
        setOpen(false);
    };

    const connect = () => {
        disconnectWebSocket();

        const sock = new SockJs("http://localhost:8080/api/ws");
        const temp = over(sock);

        temp.connect({}, () => onConnect(temp), onError);
    };

    const disconnectWebSocket = () => {
        if (stompClient) {
            try {
                if (stompClient.subscribedChats) {
                    stompClient.subscribedChats.forEach(subId => {
                        stompClient.unsubscribe(subId);
                    });
                    stompClient.subscribedChats.clear();
                }
            } catch (err) {
                console.error("Error while unsubscribing:", err);
            }

            stompClient.disconnect(() => {
                console.log("WebSocket disconnected");
                setStompClient(null);
                setConnected(false);
            });
        }
    };



    const onError = (err) => console.log("Connection error: ", err);

    const onConnect = (client) => {
        setStompClient(client);
        setConnected(true);

        if (client.subscribedChats) {
            client.subscribedChats.forEach(subId => {
                client.unsubscribe(subId);
            });
            client.subscribedChats.clear();
        } else {
            client.subscribedChats = new Set();
        }

        if (chat?.id) {
            const sub = client.subscribe(`/topic/room/${chat.id}`, onMessageReceive);
            client.subscribedChats.add(sub.id);
        }
    };

    const onMessageReceive = (payload) => {
        try {
            const receivedMessage = JSON.parse(payload.body);

            setMessages((prevMessages) => {
                const messageExists = prevMessages.some(msg => msg.id === receivedMessage.id);
                return messageExists ? prevMessages : [...prevMessages, receivedMessage];
            });
        } catch (error) {
            console.error("Failed to parse incoming message: ", error);
        }
    };

    const handleCreateNewMessage = () => {
        if (text.trim() === "" || !stompClient || !stompClient.connected) return;

        const newMessage = { chatId: chat.id, message: text };

        stompClient.send(`/app/sendMessage/${chat.id}`, {}, JSON.stringify(newMessage));

        setText("");
    };

    const deleteChatMessageHandler = (messageId) => {
        if (window.confirm('Are you sure you want to delete this message?')) {
            dispatch(deleteChatMessage(messageId));
        }
    };

    useEffect(() => {
        disconnectWebSocket();
        setMessages([]);

        if (chat?.id) {
            setTimeout(() => {
                connect();
                dispatch(getAllMessages(chat.id));
            }, 100);
        }

        return () => {
            disconnectWebSocket();
        };
    }, [chat?.id]);


    useEffect(() => {
        if (chat?.id) {
            setMessages(chatMessage.messages || []);
        } else {
            setMessages([]); // Wyczyść stare wiadomości
        }
    }, [chat?.id, chatMessage.messages, chatMessage.deletedMessage]);


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
                { messages?.map((item, index) => (
                    <div className={item.user.id === userData?.id ? "messageOwn" : "message"} key={`${item.id}-${index}`}>
                        <div className="text">
                            <p><strong>{item.user.nickName}:</strong> {item.messageText}</p>
                            <div className="info">
                                <span>{item.timestamp ? formatTimeAgo(item.timestamp) : "Unknown time"}</span>

                                {item.user.id === userData?.id && (
                                    <button onClick={() => deleteChatMessageHandler(item.id)}>Delete</button>
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
                    <i onClick={() => setOpen((prev) => !prev)}><BsEmojiSmileFill /></i>
                    {open && <EmojiPicker onEmojiClick={handleEmoji} />}
                </div>
                <button className='sendButton' onClick={handleCreateNewMessage}>Send</button>
            </div>
        </div>
    );
}

export { Chat };
