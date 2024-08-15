import React, { useState } from 'react'
import EmojiPicker from 'emoji-picker-react'
import { FaPhoneAlt, FaInfoCircle, FaImage, FaCamera, FaMicrophone } from "react-icons/fa";
import { BsCameraVideoFill, BsEmojiSmileFill } from "react-icons/bs";

function Chat() {

    const [open, setOpen] = useState(false)
    const [text, setText] = useState("")

    const handleEmoji = (e) => {
        setText((prev) => prev + e.emoji)
        setOpen(false)
    }

    return (
        <div className='chat'>
            <div className="top">
                <div className="user">
                    <img src="" alt="" />
                    <div className="userData">
                        <span>Name Surname</span>
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
            </div>
            <div className="bottom">
                <div className="icons">
                    <i><FaImage /></i>
                    <i><FaCamera /></i>
                    <i><FaMicrophone /></i>
                </div>
                <input type="text" placeholder='Write message...' value={text} onChange={(e) => setText(e.target.value)} />
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