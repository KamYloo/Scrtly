import React, { useEffect, useRef, useState } from 'react'
import EmojiPicker from 'emoji-picker-react'
import { FaPhoneAlt, FaInfoCircle, FaImage, FaCamera, FaMicrophone } from "react-icons/fa";
import { BsCameraVideoFill, BsEmojiSmileFill } from "react-icons/bs";
import {useDispatch, useSelector} from "react-redux";
import {createChatMessage, getAllMessages} from "../../Redux/ChatMessage/Action.js";
import { formatDistanceToNow } from 'date-fns'

// eslint-disable-next-line react/prop-types
function Chat({chat, auth, token}) {

    const [open, setOpen] = useState(false)
    const [text, setText] = useState("")

    const {chatMessage } = useSelector(store => store);

    const endRef = useRef(null)
    const dispatch = useDispatch();

    useEffect(() => {
        endRef.current?.scrollIntoView({ behaviour: "smooth" })
    })

    useEffect(() => {
        // eslint-disable-next-line react/prop-types
        if(chat.id)
            dispatch(getAllMessages({chatId:chat.id, token}))
    }, [chat, chatMessage.newMessage]);

    const handleCreateNewMessage = () => {
        // eslint-disable-next-line react/prop-types
        dispatch(createChatMessage({token, data:{chatId: chat.id, message:text}}))
        setText("")
    }

    const formatTimeAgo = (timestamp) => {
        return formatDistanceToNow(new Date(timestamp), { addSuffix: true });
    };

    const handleEmoji = (e) => {
        setText((prev) => prev + e.emoji)
        setOpen(false)
    }


    // eslint-disable-next-line react/prop-types
    const otherPerson = chat.firstPerson.id !== auth.reqUser.id ? chat.firstPerson : chat.secondPerson;
    return (
        <div className='chat'>
            <div className="top">
                <div className="user">
                    <img src="" alt="" />
                    <div className="userData">
                        <span>{otherPerson.fullName}</span>
                        <p>gdafhdatgheavoriufhafjhushgebviaevbdafiukfbllyvbaelidai</p>
                    </div>
                </div>
                <div className="icons">
                    <i><FaPhoneAlt /></i>
                    <i><BsCameraVideoFill /></i>
                    <i><FaInfoCircle /></i>
                </div>
            </div>
            <div className="center">
                { chatMessage.messages?.map((item) => (
                    <div className={item.user.id === auth.reqUser.id ? "messageOwn" : "message"} key={item.id}>
                        <img src="#" alt=""/>
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