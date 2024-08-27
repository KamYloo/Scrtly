import React, { useEffect, useRef, useState } from 'react'
import EmojiPicker from 'emoji-picker-react'
import { FaPhoneAlt, FaInfoCircle, FaImage, FaCamera, FaMicrophone } from "react-icons/fa";
import { BsCameraVideoFill, BsEmojiSmileFill } from "react-icons/bs";
import {useDispatch} from "react-redux";
import {createChatMessage} from "../../Redux/ChatMessage/Action.js";

// eslint-disable-next-line react/prop-types
function Chat({chat, auth, token}) {

    const [open, setOpen] = useState(false)
    const [text, setText] = useState("")

    const endRef = useRef(null)
    const dispatch = useDispatch();

    useEffect(() => {
        endRef.current?.scrollIntoView({ behaviour: "smooth" })
    })

    const handleCreateNewMessage = () => {
        console.log(chat.id)
        dispatch(createChatMessage({token, data:{chatId: chat.id, message:text}}))
        console.log("cretae new message")
    }

    const handleEmoji = (e) => {
        setText((prev) => prev + e.emoji)
        setOpen(false)
    }


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
                <div className="message">
                    <img src="#" alt="" />
                    <div className="text">
                        <p>How are you? fdhgafdgdafvdadraedrvdfvdafgdagdfbdafvvevadvcdagacvda</p>
                        <span>34 min ago</span>
                    </div>
                </div>
                <div className="messageOwn">
                    <div className="text">
                        <p>How are you? fdhgafdgdafvdadraedrvdfvdafgdagdfbdafvvevadvcdagacvda</p>
                        <span>34 min ago</span>
                    </div>
                </div>
                <div className="message">
                    <img src="#" alt="" />
                    <div className="text">
                        <p>How are you? fdhgafdgdafvdadraedrvdfvdafgdagdfbdafvvevadvcdagacvda</p>
                        <span>34 min ago</span>
                    </div>
                </div>
                <div className="messageOwn">
                    <div className="text">
                        <p>How are you? fdhgafdgdafvdadraedrvdfvdafgdagdfbdafvvevadvcdagacvda</p>
                        <span>34 min ago</span>
                    </div>
                </div>
                <div className="message">
                    <img src="#" alt="" />
                    <div className="text">
                        <p>How are you? fdhgafdgdafvdadraedrvdfvdafgdagdfbdafvvevadvcdagacvda</p>
                        <span>34 min ago</span>
                    </div>
                </div>
                <div className="messageOwn">
                    <div className="text">
                        <p>How are you? fdhgafdgdafvdadraedrvdfvdafgdagdfbdafvvevadvcdagacvda</p>
                        <span>34 min ago</span>
                    </div>
                </div>
                <div className="message">
                    <img src="#" alt="" />
                    <div className="text">
                        <p>How are you? fdhgafdgdafvdadraedrvdfvdafgdagdfbdafvvevadvcdagacvda</p>
                        <span>34 min ago</span>
                    </div>
                </div>
                <div className="messageOwn">
                    <div className="text">
                        <p>How are you? fdhgafdgdafvdadraedrvdfvdafgdagdfbdafvvevadvcdagacvda</p>
                        <span>34 min ago</span>
                    </div>
                </div>
                <div className="message">
                    <img src="#" alt="" />
                    <div className="text">
                        <p>How are you? fdhgafdgdafvdadraedrvdfvdafgdagdfbdafvvevadvcdagacvda</p>
                        <span>34 min ago</span>
                    </div>
                </div>
                <div className="messageOwn">
                    <div className="text">
                        <p>How are you? fdhgafdgdafvdadraedrvdfvdafgdagdfbdafvvevadvcdagacvda</p>
                        <span>34 min ago</span>
                    </div>
                </div>
                <div className="message">
                    <img src="#" alt="" />
                    <div className="text">
                        <p>How are you? fdhgafdgdafvdadraedrvdfvdafgdagdfbdafvvevadvcdagacvda</p>
                        <span>34 min ago</span>
                    </div>
                </div>
                <div className="messageOwn">
                    <div className="text">
                        <p>How are you? fdhgafdgdafvdadraedrvdfvdafgdagdfbdafvvevadvcdagacvda</p>
                        <span>34 min ago</span>
                    </div>
                </div>
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
                           if (e.key === 'Enter') {
                               handleCreateNewMessage()
                               setText("")
                           }
                }}/>
                <div className="emoji">
                    <i onClick={() => setOpen((prev) => !prev)}><BsEmojiSmileFill /></i>
                    <div className="picker">
                        <EmojiPicker open={open} onEmojiClick={handleEmoji} />
                    </div>
                </div>
                <button className='sendButton'>Send</button>
            </div>
        </div>
    )
}

export { Chat }