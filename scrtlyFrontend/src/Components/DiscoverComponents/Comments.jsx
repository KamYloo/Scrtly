import React from 'react'
import { AiOutlineLike } from "react-icons/ai";
import { AddComment } from './AddComment';

function Comments() {
    
  return (
    <div className='commentsSection'>
        <div className="up">
            <img src="" alt="" />
            <p>Name Surname</p>
        </div>
        <hr />
        <div className="comments">
            <div className="comment Own">
            <img src="" alt="" />
                <div className="context">
                    <p>Name Surname</p>
                    <span>fdsgadddgfdgdafgdabdabddfffffffbabkkkkkkkkkkkkfbdffffffffffffffffffffddddddddddddddddbdabdfffffffffdfffgfbdaafdddddddddddddf</span>
                    <div className="info">
                        <span>23 min</span>
                    </div>
                </div>
               
            </div>
            <div className="comment">
                <img src="" alt="" />
                <div className="context">
                    <p>Name Surname</p>
                    <span>fdsgadddgfdgdafgdabdabddfffffffbabkkkkkkkkkkkkfbdffffffffffffffffffffddddddddddddddddbdabdfffffffffdfffgfbdaafdddddddddddddf</span>
                    <div className="info">
                        <span>23 min</span>
                        <span>12 likes</span>
                    </div>
                </div>
                <i><AiOutlineLike/></i>
            </div>
            <div className="comment">
                <img src="" alt="" />
                <div className="context">
                    <p>Name Surname</p>
                    <span>fdsgadddgfdgdafgdabdabddfffffffbabkkkkkkkkkkkkfbdffffffffffffffffffffddddddddddddddddbdabdfffffffffdfffgfbdaafdddddddddddddf</span>
                    <div className="info">
                        <span>23 min</span>
                        <span>12 likes</span>
                    </div>
                </div>
                <i><AiOutlineLike/></i>
            </div>
            <div className="comment">
                <img src="" alt="" />
                <div className="context">
                    <p>Name Surname</p>
                    <span>fdsgadddgfdgdafgdabdabddfffffffbabkkkkkkkkkkkkfbdffffffffffffffffffffddddddddddddddddbdabdfffffffffdfffgfbdaafdddddddddddddf</span>
                    <div className="info">
                        <span>23 min</span>
                        <span>12 likes</span>
                    </div>
                </div>
                <i><AiOutlineLike/></i>
            </div>
            <div className="comment">
                <img src="" alt="" />
                <div className="context">
                    <p>Name Surname</p>
                    <span>fdsgadddgfdgdafgdabdabddfffffffbabkkkkkkkkkkkkfbdffffffffffffffffffffddddddddddddddddbdabdfffffffffdfffgfbdaafdddddddddddddf</span>
                    <div className="info">
                        <span>23 min</span>
                        <span>12 likes</span>
                    </div>
                </div>
                <i><AiOutlineLike/></i>
            </div>
            <div className="comment">
                <img src="" alt="" />
                <div className="context">
                    <p>Name Surname</p>
                    <span>fdsgadddgfdgdafgdabdabddfffffffbabkkkkkkkkkkkkfbdffffffffffffffffffffddddddddddddddddbdabdfffffffffdfffgfbdaafdddddddddddddf</span>
                    <div className="info">
                        <span>23 min</span>
                        <span>12 likes</span>
                    </div>
                </div>
                <i><AiOutlineLike/></i>
            </div>
        </div>
        <hr />
        <AddComment/>
    </div>
  )
}

export  {Comments}