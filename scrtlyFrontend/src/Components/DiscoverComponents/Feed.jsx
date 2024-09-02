import React, { useState } from 'react'
import { AiOutlineLike } from "react-icons/ai";
import { TfiCommentAlt } from "react-icons/tfi";
import { FaEllipsisH } from "react-icons/fa";
import { Post } from './Post';

function Feed() {
  const [post, setPost] = useState(false)
  const [menuPost, setMenuPost] = useState(false)

  const togglePost = () => {
    setPost((prev) => !prev);
  };

  return (
    <div className='feed'>
      <div className="posts">
        <div className="post">
          <div className="up">
            <img src="" alt="" />
            <div className="userData">
              <p>Name Surname</p>
              <span>1 day ago</span>
            </div>
            <i onClick={() => setMenuPost(((prev) => !prev))}><FaEllipsisH /></i>
            {menuPost && <ul class="list">
              <li class="option">
                <span>Edit</span>
              </li>
              <li class="option">
                <span>Delete</span>
              </li>
            </ul>}
          </div>
          <div className="middle">
            <img src="https://img.freepik.com/free-photo/view-beautiful-rainbow-nature-landscape_23-2151597605.jpg?ga=GA1.1.635648649.1725025924&semt=ais_hybrid" alt="" />
          </div>
          <div className="description">
            <p>jfaosdjoafhjodsafjsodlmcvnossssssssssssssssssssssssssssssssssssssssaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaadddddddddddddsaddddddddddddddddddddd</p>
          </div>
          <div className="bottom">
            <div className="likes">
              <i><AiOutlineLike /></i>
              <span>78 likes</span>
            </div>
            <div className="comments" onClick={togglePost}>
              <i><TfiCommentAlt /></i>
              <span>28 Comments</span>
            </div>
          </div>
        </div>
        <div className="post">
          <div className="up">
            <img src="" alt="" />
            <div className="userData">
              <p>Name Surname</p>
              <span>1 day ago</span>
            </div>
          </div>
          <div className="middle">
            <img src="https://img.freepik.com/free-photo/view-spectacular-nature-landscape_23-2150763636.jpg?ga=GA1.1.635648649.1725025924&semt=ais_hybrid" alt="" />

          </div>
          <div className="bottom">
            <div className="likes">
              <i><AiOutlineLike /></i>
              <span>78 likes</span>
            </div>
            <div className="comments" onClick={() => setPost(((prev) => !prev))}>
              <i><TfiCommentAlt /></i>
              <span>28 Comments</span>
            </div>
          </div>
        </div>
        <div className="post">
          <div className="up">
            <img src="" alt="" />
            <div className="userData">
              <p>Name Surname</p>
              <span>1 day ago</span>
            </div>
          </div>
          <div className="middle">
            <img src="https://img.freepik.com/premium-photo/scenic-view-mountains-against-sky-sunset_1048944-5186449.jpg?ga=GA1.1.635648649.1725025924&semt=ais_hybrid" alt="" />
          </div>
          <div className="bottom">
            <div className="likes">
              <i><AiOutlineLike /></i>
              <span>78 likes</span>
            </div>
            <div className="comments" onClick={() => setPost(((prev) => !prev))}>
              <i><TfiCommentAlt /></i>
              <span>28 Comments</span>
            </div>
          </div>
        </div>

      </div>
      {post && <Post onClose={togglePost} />}
    </div>
  )
}

export { Feed }