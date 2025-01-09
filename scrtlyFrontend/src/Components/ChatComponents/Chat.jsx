import React, { useEffect, useRef, useState } from 'react'
import EmojiPicker from 'emoji-picker-react'
import { FaPhoneAlt, FaInfoCircle, FaImage, FaCamera, FaMicrophone } from "react-icons/fa";
import { BsCameraVideoFill, BsEmojiSmileFill } from "react-icons/bs";
import {useDispatch, useSelector} from "react-redux";
import {createChatMessage, deleteChatMessage, getAllMessages} from "../../Redux/ChatMessage/Action.js";
import { formatDistanceToNow } from 'date-fns'
import SockJs from "sockjs-client/dist/sockjs"
import {over} from "stompjs"
import {useNavigate} from "react-router-dom";

// eslint-disable-next-line react/prop-types
function Chat({chat}) {

    const [open, setOpen] = useState(false)
    const [text, setText] = useState("")
    const [stompClient, setStompClient] = useState()
    const [isConnected, setConnected] = useState(false)
    const [messages, setMessages] = useState([])

    const {chatMessage } = useSelector(store => store);
    const userData = (() => { try { return JSON.parse(localStorage.getItem("user")) || null; } catch { return null; } })();
    const endRef = useRef(null)
    const dispatch = useDispatch();
    const navigate = useNavigate()

    useEffect(() => {
        endRef.current?.scrollIntoView({ behaviour: "smooth" })
    })

    const formatTimeAgo = (timestamp) => {
        return formatDistanceToNow(new Date(timestamp), { addSuffix: true });
    };

    const handleEmoji = (e) => {
        setText((prev) => prev + e.emoji)
        setOpen(false)
    }

    const connect = () => {
        const sock = new SockJs("http://localhost:8080/api/ws")
        const temp = over(sock)
        setStompClient(temp)

        const headers = {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            "X-XSRF-TOKEN": getCookie("XSRF-TOKEN"),
        }

        temp.connect(headers, onConnect, onError)
    }

    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2)
            return parts.pop().split(";").shift();
    }

    const onError = (err) => {
        console.log("no error ", err)
    }

    const onConnect = (frame) => {
        console.log("Connected to server", frame);
        setConnected(true)
        subscribeToChat();
    }

    const subscribeToChat = () => {
        if (stompClient && stompClient.connected && chat && isConnected) {
            const user1Id = chat.firstPerson.id;
            const user2Id = chat.secondPerson.id;

            if (!stompClient.subscriptions || !stompClient.subscriptions["/queue/private/" + user1Id]) {
                stompClient.subscribe("/queue/private/" + user1Id, onMessageReceive);
            }

            if (!stompClient.subscriptions || !stompClient.subscriptions["/queue/private/" + user2Id]) {
                stompClient.subscribe("/queue/private/" + user2Id, onMessageReceive);
            }
        } else {
            console.log("Cannot subscribe, connection not established yet.");
        }
    };




    const unsubscribeFromChat = () => {
        if (stompClient && stompClient.connected && chat) {
            const user1Id = chat.firstPerson.id;
            const user2Id = chat.secondPerson.id;

            if (stompClient.subscriptions["/queue/private/" + user1Id]) {
                stompClient.unsubscribe("/queue/private/" + user1Id);
            }

            if (stompClient.subscriptions["/queue/private/" + user2Id]) {
                stompClient.unsubscribe("/queue/private/" + user2Id);
            }
        }
    };


    const onMessageReceive = (payload) => {
        try {
            const receivedMessage = JSON.parse(payload.body);

            setMessages((prevMessages) => {
                const messageExists = prevMessages.some(msg => msg.id === receivedMessage.id);
                if (!messageExists) {
                    return [...prevMessages, receivedMessage];
                }
                return prevMessages;
            });
        } catch (error) {
            console.error("Failed to parse incoming message:", payload.body, error);
        }
    }


    const handleCreateNewMessage = () => {
        if (text.trim() === "") return;
        if (stompClient && stompClient.connected) {
            dispatch(createChatMessage({ data: { chatId: chat.id, message: text } }));
            setText("");
        }
    }

    const deletedChatMessageHandler = (messageId) => {
        const confirmDelete = window.confirm('Are you sure you want to delete this Message?');
        if (confirmDelete) {
            dispatch(deleteChatMessage(messageId))
        }
    }

    useEffect(() => {
        const connectAndSubscribe = async () => {
            await connect();


            if (isConnected && chat) {
                subscribeToChat();
            }
        };

        connectAndSubscribe();

        return () => {
            unsubscribeFromChat();
        };
    }, [isConnected, chat, chatMessage.deletedMessage]);


    useEffect(() => {
        if (chatMessage.newMessage && stompClient && isConnected) {
            setMessages([...messages, chatMessage.newMessage])
            stompClient?.send("/app/message", {}, JSON.stringify(chatMessage.newMessage))
        }
    }, [chatMessage.newMessage, chatMessage.deletedMessage])

    useEffect(() => {
        setMessages(chatMessage.messages)
    },[ chatMessage.messages, chatMessage.deletedMessage]);


    useEffect(() => {
        // eslint-disable-next-line react/prop-types
        if(chat.id)
            dispatch(getAllMessages(chat.id))
    }, [chat, chatMessage.newMessage, dispatch , chatMessage.deletedMessage]);

    // eslint-disable-next-line react/prop-types
    const otherPerson = chat.firstPerson.id !== userData?.id ? chat.firstPerson : chat.secondPerson;
    return (
        <div className='chat'>
            <div className="top">
                <div className="user">
                    <img src={otherPerson?.profilePicture} alt=""  onClick={() => navigate(`/profile/${otherPerson.nickName}`)}/>
                    <div className="userData">
                        <span>{otherPerson.fullName}</span>
                        <p>{otherPerson?.description || ''}</p>
                    </div>
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
                        <img src={otherPerson?.profilePicture} alt="" onClick={() => navigate(`/profile/${item.user.nickName}`)}/>
                        <div className="text">
                            <p>{item.messageText}</p>
                            <div className="info">
                                <span>{formatTimeAgo(item.timestamp)}</span>
                                <button onClick={()=>deletedChatMessageHandler(item.id)}>Delete</button>
                            </div>
                        </div>
                    </div>))}


                <div ref={endRef}></div>
            </div>
            <div className="bottom">
                <div className="icons">
                    <i><FaImage /></i>
                    <i><FaCamera /></i>
                    <i><FaMicrophone /></i>
                </div>
                <input type="text" placeholder='Write message...' value={text} onChange={(e) => setText(e.target.value)}
                       onKeyPress={(e)=> {
                           if (e.key === 'Enter')
                               handleCreateNewMessage()

                       }}/>
                <div className="emoji">
                    <i onClick={() => setOpen((prev) => !prev)}><BsEmojiSmileFill /></i>
                    <div className="picker">
                        <EmojiPicker open={open} onEmojiClick={handleEmoji} />
                    </div>
                </div>
                <button className='sendButton' onClick={handleCreateNewMessage}>Send</button>
            </div>
        </div>
    )
}

export { Chat }