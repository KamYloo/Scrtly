import React, {useEffect, useState} from 'react'
import toast from 'react-hot-toast';
import {AiFillLike, AiOutlineLike} from "react-icons/ai";
import { TfiCommentAlt } from "react-icons/tfi";
import { FaEllipsisH } from "react-icons/fa";
import { MdOutlineMenuOpen } from "react-icons/md";
import { Post } from './Post';
import {useDispatch, useSelector} from "react-redux";
import {deletePost, getAllPosts, likePost} from "../../Redux/Post/Action.js";
import {BASE_API_URL} from "../../config/api.js";
import { formatDistanceToNow } from 'date-fns'
import {useNavigate} from "react-router-dom";
import {EditPost} from "./EditPost.jsx";

function Feed() {
  const [selectedPost, setSelectedPost] = useState(false)
  const [postDetail, setPostDetail] = useState(null)
  const [menuPost, setMenuPost] = useState(false)
  const [postsSettings, setPostsSettings] = useState(false)
  const [editPost, setEditPost] = useState(false)
  const [filter, setFilter] = useState('')
  const [sortOrder, setSortOrder] = useState('date')

  const dispatch = useDispatch()
  const {auth, post, comment } = useSelector(store => store);
  const navigate = useNavigate();

  const handleProfileClick = (userId) => {
    navigate(`/profile/${userId}`)
  };

  const togglePost = (post = null) => {
    setPostDetail(post)
    setSelectedPost((prev) => !prev);
  };

  const toggleEditPost = (post = null) => {
    setPostDetail(post)
    setEditPost((prev) => !prev);
    setMenuPost(false)
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
      dispatch(deletePost(postId)).then(() => {
        toast.success('Post deleted successfully.');
      }).catch(() => {
        toast.error('Failed to delete post. Please try again.');
      });
    }
  };

  const likePostHandler = (postId) => {
    dispatch(likePost(postId));
  }

  const filteredPosts = post.posts
      .filter(item => filter === '' || item.user.fullName.toLowerCase().includes(filter.toLowerCase()))
      .sort((a, b) => {
        switch (sortOrder) {
          case 'likes':
            return b.totalLikes - a.totalLikes
          case 'date-asc':
            return new Date(a.creationDate) - new Date(b.creationDate)
          case 'date-desc':
          default:
            return new Date(b.creationDate) - new Date(a.creationDate)
        }
      })

  useEffect(() => {
      dispatch(getAllPosts())
  }, [dispatch, post.likedPost, post.createdPost, post.deletedPost, comment.createdComment,comment.deletedComment])

  return (
    <div className='feed'>

      <div className="posts">
        <i className="postsSettings" onClick={() => setPostsSettings(((prev) => !prev))}><MdOutlineMenuOpen/></i>
        {postsSettings && (<ul className="filtredMenu">
          <li className="filter">
            <input
                type="text"
                placeholder="Filter by user name..."
                value={filter}
                onChange={(e) => setFilter(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    setPostsSettings(false)
                  }
                }}
            />
          </li>

          <li className="sort">
            <select  value={sortOrder} onChange={(e) =>
            {setSortOrder(e.target.value)
              setPostsSettings(false)}}>
              <option value="likes">Sort by likes</option>
              <option value="date-asc">Sort by date (ascending)</option>
              <option value="date-desc">Sort by date (descending)</option>
            </select>
          </li>
        </ul>)}
        {filteredPosts.map((item) => (
            <div className="post" key={item.id}>
              <div className="up">
                <img src={`${BASE_API_URL}/${item.user?.profilePicture || ''}`} alt=""
                     onClick={() => handleProfileClick(item.user.id)}/>
                <div className="userData">
                  <p>{item.user.fullName}</p>
                  <span>{formatTimeAgo(item.creationDate)}</span>
                </div>
                {auth.reqUser.id === item.user.id && (
                    <>
                      <i onClick={() => handleMenuToggle(item.id)}><FaEllipsisH/></i>
                      {menuPost === item.id && (
                          <ul className="list">
                            <li className="option" onClick={()=> toggleEditPost(item)}>
                              <span>Edit</span>
                            </li>
                            <li className="option" onClick={() => {
                              handleDeletePost(item.id)
                              setMenuPost(false)
                            }}>
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
                  <i onClick={() => likePostHandler(item.id)}>{item.liked ? <AiFillLike/> : <AiOutlineLike/>}</i>
                  <span>{item.totalLikes}</span>
                </div>
                <div className="comments" onClick={() => togglePost(item)}>
                  <i><TfiCommentAlt/></i>
                  <span>{item?.totalComments | 0} comments</span>
                </div>
              </div>
            </div>))}

      </div>
      {editPost && <EditPost post={postDetail} onClose={() => setEditPost(((prev) => !prev))}/>}
      {selectedPost && <Post post={postDetail} onClose={togglePost}/>}
    </div>
  )
}

export {Feed}