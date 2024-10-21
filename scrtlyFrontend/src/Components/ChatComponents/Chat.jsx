import React, { useEffect, useRef, useState } from 'react'
import EmojiPicker from 'emoji-picker-react'
import { FaPhoneAlt, FaInfoCircle, FaImage, FaCamera, FaMicrophone } from "react-icons/fa";
import { BsCameraVideoFill, BsEmojiSmileFill } from "react-icons/bs";
import {useDispatch, useSelector} from "react-redux";
import {createChatMessage, getAllMessages} from "../../Redux/ChatMessage/Action.js";
import { formatDistanceToNow } from 'date-fns'
import {BASE_API_URL} from "../../config/api.js";
import SockJs from "sockjs-client/dist/sockjs"
import {over} from "stompjs"

// eslint-disable-next-line react/prop-types
function Chat({chat}) {

    const [open, setOpen] = useState(false)
    const [text, setText] = useState("")
    const [stompClient, setStompClient] = useState()
    const [isConnected, setConnected] = useState(false)
    const [messages, setMessages] = useState([])

    const {auth, chatMessage } = useSelector(store => store);

    const endRef = useRef(null)
    const dispatch = useDispatch();

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
        const sock = new SockJs("http://localhost:8080/ws")
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

            // Sprawdź, czy subskrypcje istnieją, zanim je usuniesz
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

            // Sprawdź, czy wiadomość już nie została dodana
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
        if (text.trim() === "") return; // Sprawdzenie, czy wiadomość nie jest pusta
        if (stompClient && stompClient.connected) {
            dispatch(createChatMessage({ data: { chatId: chat.id, message: text } }));
            setText(""); // Resetowanie pola tekstowego
        }
    }

    useEffect(() => {
        const connectAndSubscribe = async () => {
            await connect(); // Połączenie ze stomClient

            // Subskrypcja jest możliwa dopiero po nawiązaniu połączenia
            if (isConnected && chat) {
                subscribeToChat();  // Subskrybuj wiadomości po nawiązaniu połączenia
            }
        };

        connectAndSubscribe(); // Wywołaj funkcję nawiązywania połączenia i subskrypcji

        return () => {
            unsubscribeFromChat(); // Czyścimy subskrypcje na unmount
        };
    }, [isConnected, chat]);


    useEffect(() => {
        if (chatMessage.newMessage && stompClient && isConnected) {
            setMessages([...messages, chatMessage.newMessage])
            stompClient?.send("/app/message", {}, JSON.stringify(chatMessage.newMessage))
        }
    }, [chatMessage.newMessage])

    useEffect(() => {
        setMessages(chatMessage.messages)
    },[ chatMessage.messages]);


    useEffect(() => {
        // eslint-disable-next-line react/prop-types
        if(chat.id)
            dispatch(getAllMessages({chatId:chat.id}))
    }, [chat, chatMessage.newMessage, dispatch]);

    // eslint-disable-next-line react/prop-types
    const otherPerson = chat.firstPerson.id !== auth.reqUser.id ? chat.firstPerson : chat.secondPerson;
    return (
        <div className='chat'>
            <div className="top">
                <div className="user">
                    <img src={`${BASE_API_URL}/${otherPerson?.profilePicture || ''}`} alt="" />
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
                    <div className={item.user.id === auth.reqUser.id ? "messageOwn" : "message"} key={`${item.id}-${index}`}>
                        <img src={`${BASE_API_URL}/${otherPerson?.profilePicture || ''}`} alt=""/>
                        <div className="text">
                            <p>{item.messageText}</p>
                            <span>{formatTimeAgo(item.timestamp)}</span>
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