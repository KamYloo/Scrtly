import React from 'react'
import { FaPhoneAlt, FaInfoCircle } from "react-icons/fa";
import { BsCameraVideoFill } from "react-icons/bs";

function Chat() {
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
            <div className="center"></div>
            <div className="bottom">
                <div className="icons"></div>
                <input type="text" placeholder='Write message...'/>
                <div className="emoji">
                    
                </div>
            </div>
        </div>
    )
}

export { Chat }