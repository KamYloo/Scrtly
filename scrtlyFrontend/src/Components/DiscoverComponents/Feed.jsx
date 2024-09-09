import React, {useEffect, useState} from 'react'
import {AiFillLike, AiOutlineLike} from "react-icons/ai";
import { TfiCommentAlt } from "react-icons/tfi";
import { FaEllipsisH } from "react-icons/fa";
import { Post } from './Post';
import {useDispatch, useSelector} from "react-redux";
import {deletePost, getAllPosts, likePost} from "../../Redux/Post/Action.js";
import {BASE_API_URL} from "../../config/api.js";
import { formatDistanceToNow } from 'date-fns'

function Feed() {
  const [selectedPost, setSelectedPost] = useState(false)
  const [postDetail, setPostDetail] = useState(null)
  const [menuPost, setMenuPost] = useState(false)
  const [allPosts, setAllPosts] = useState([])

  const dispatch = useDispatch()
  const {auth, post, comment } = useSelector(store => store);

  const togglePost = (post = null) => {
    setPostDetail(post)
    setSelectedPost((prev) => !prev);
  };

  const handleMenuToggle = (postId) => {
    setMenuPost((prev) => (prev === postId ? null : postId))
  };

  const formatTimeAgo = (timestamp) => {
    return formatDistanceToNow(new Date(timestamp), { addSuffix: true });
  };

  const handleDeletePost = (postId) => {
    const confirmDelete = window.confirm('Are you sure you want to delete this post?');
    if (confirmDelete) {
      dispatch(deletePost(postId));
    }
  };

  const likePostHandler = (postId) => {
    dispatch(likePost(postId));
  }

  useEffect(() => {
      dispatch(getAllPosts())
  }, [dispatch, post.likedPost, post.createdPost, post.deletedPost, comment.createdComment,comment.deletedComment])

  return (
    <div className='feed'>
      <div className="posts">
        { post.posts.map((item) => (
            <div className="post" key={item.id}>
          <div className="up">
            <img src={`${BASE_API_URL}/${item.user?.profilePicture || ''}`} alt=""/>
            <div className="userData">
              <p>{item.user.fullName}</p>
              <span>{formatTimeAgo(item.creationDate)}</span>
            </div>
            {auth.reqUser.id === item.user.id && (
                <>
                  <i onClick={() => handleMenuToggle(item.id)}><FaEllipsisH /></i>
                  {menuPost === item.id && (
                      <ul className="list">
                        <li className="option">
                          <span>Edit</span>
                        </li>
                        <li className="option" onClick={() => handleDeletePost(item.id)}>
                          <span>Delete</span>
                        </li>
                      </ul>
                  )}
                </>
            )}
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
              <i onClick={() => likePostHandler(item.id)}>{item.liked ? <AiFillLike /> : <AiOutlineLike />}</i>
              <span>{item.totalLikes}</span>
            </div>
            <div className="comments" onClick={() => togglePost(item)}>
              <i><TfiCommentAlt/></i>
              <span>{item?.totalComments| 0} comments</span>
            </div>
          </div>
        </div>))}

      </div>
      {selectedPost && <Post post={postDetail} onClose={togglePost} />}
    </div>
  )
}

export { Feed }