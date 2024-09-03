import React, {useEffect, useState} from 'react'
import { AiOutlineLike } from "react-icons/ai";
import { TfiCommentAlt } from "react-icons/tfi";
import { FaEllipsisH } from "react-icons/fa";
import { Post } from './Post';
import {useDispatch, useSelector} from "react-redux";
import {getAllPosts} from "../../Redux/Post/Action.js";
import {BASE_API_URL} from "../../config/api.js";
import { formatDistanceToNow } from 'date-fns'

function Feed() {
  const [postDetail, setPostDetail] = useState(false)
  const [menuPost, setMenuPost] = useState(false)
  const [allPosts, setAllPosts] = useState([])

  const dispatch = useDispatch()
  const { post } = useSelector(store => store);

  const togglePost = () => {
    setPostDetail((prev) => !prev);
  };

  const formatTimeAgo = (timestamp) => {
    return formatDistanceToNow(new Date(timestamp), { addSuffix: true });
  };

  useEffect(() => {
    const fetchPosts = () => {
      dispatch(getAllPosts())
    };

    fetchPosts()


    const interval = setInterval(fetchPosts, 10000)

    return () => clearInterval(interval)
  }, [dispatch])

  useEffect(() => {
    setAllPosts(post.posts)
  }, [post.posts])

  return (
    <div className='feed'>
      <div className="posts">
        { allPosts.map((item) => (
            <div className="post" key={item.id}>
          <div className="up">
            <img src={`${BASE_API_URL}/${item.user?.profilePicture || ''}`} alt=""/>
            <div className="userData">
              <p>{item.user.fullName}</p>
              <span>{formatTimeAgo(item.creationDate)}</span>
            </div>
            <i onClick={() => setMenuPost(((prev) => !prev))}><FaEllipsisH/></i>
            {menuPost && <ul className="list">
              <li className="option">
                <span>Edit</span>
              </li>
              <li className="option">
                <span>Delete</span>
              </li>
            </ul>}
          </div>
          <div className="middle">
            <img
                src={`${BASE_API_URL}${item.image}`}
                alt=""/>
          </div>
          <div className="description">
            <p>{item.description}</p>
          </div>
          <div className="bottom">
            <div className="likes">
              <i><AiOutlineLike/></i>
              <span>78 likes</span>
            </div>
            <div className="comments" onClick={togglePost}>
              <i><TfiCommentAlt/></i>
              <span>28 Comments</span>
            </div>
          </div>
        </div>))}

      </div>
      {postDetail && <Post onClose={togglePost} />}
    </div>
  )
}

export { Feed }