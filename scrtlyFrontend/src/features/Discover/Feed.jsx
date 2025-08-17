import React, { useState } from 'react'
import toast from 'react-hot-toast';
import { AiFillLike, AiOutlineLike } from "react-icons/ai";
import { TfiCommentAlt } from "react-icons/tfi";
import { FaEllipsisH } from "react-icons/fa";
import { MdOutlineMenuOpen } from "react-icons/md";
import { Post } from './Post.jsx';
import { formatDistanceToNow } from 'date-fns'
import { useNavigate } from "react-router-dom";
import { EditPost } from "./EditPost.jsx";
import Spinner from "../../Components/Spinner.jsx";
import defaultAvatar from "../../assets/user.jpg";
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";
import {useDeletePostMutation, useGetAllPostsQuery, useLikePostMutation} from "../../Redux/services/postApi.js";

function Feed() {
  const [selectedPost, setSelectedPost] = useState(false);
  const [postDetail, setPostDetail] = useState(null);
  const [menuPost, setMenuPost] = useState(false);
  const [postsSettings, setPostsSettings] = useState(false);
  const [editPost, setEditPost] = useState(false);
  const [minLikesFilter, setMinLikesFilter] = useState('');
  const [maxLikesFilter, setMaxLikesFilter] = useState('');
  const [sortOrder, setSortOrder] = useState('date-desc');
  const { data: reqUser } = useGetCurrentUserQuery(null, {
    skip: !localStorage.getItem('isLoggedIn'),
  });
  const sortDir = sortOrder === 'date-asc' ? 'ASC' : 'DESC';
  const { data: postsPage, isLoading, isError, error } = useGetAllPostsQuery({
    minLikes: minLikesFilter ? Number(minLikesFilter) : null,
    maxLikes: maxLikesFilter ? Number(maxLikesFilter) : null,
    sortDir,
    page: 0,
    size: 10,
  });
  const posts = postsPage?.content || [];
  const [likePost] = useLikePostMutation();
  const [deletePost] = useDeletePostMutation();
  const navigate = useNavigate();

  const handleProfileClick = (nickName) => {
    navigate(`/profile/${nickName}`);
  };

  const togglePost = (post = null) => {
    setPostDetail(post);
    setSelectedPost(prev => !prev);
  };

  const toggleEditPost = (post = null) => {
    setPostDetail(post);
    setEditPost(prev => !prev);
    setMenuPost(false);
  };

  const handleMenuToggle = (postId) => {
    setMenuPost(prev => (prev === postId ? null : postId));
  };

  const formatTimeAgo = (timestamp) => {
    return formatDistanceToNow(new Date(timestamp), { addSuffix: true });
  };

  const handleDeletePost = async (postId) => {
    if (!window.confirm('Are you sure you want to delete this post?')) return;
    try {
      await deletePost(postId).unwrap();
      toast.success('Post deleted');
    } catch (err) {
      toast.error(err.data.businessErrornDescription);
    }
  };

  const likePostHandler = async (postId) => {
    try {
      await likePost(postId).unwrap();
    } catch (err) {
      toast.error(err.data.businessErrornDescription);
    }
  };

  if (isLoading) {
    return (
      <Spinner />
    );
  }

  if (isError) {
    return (
      <div className="feed">
        <p>Error: {error?.data || error.error}</p>
      </div>
    );
  }

  return (
    <div className='feed'>
      <div className="posts">
        <i className="postsSettings" onClick={() => setPostsSettings(prev => !prev)}><MdOutlineMenuOpen /></i>
        {postsSettings && (
          <ul className="filtredMenu">
            <li className="filter">
              <input
                type="number"
                placeholder="Filter by min likes..."
                value={minLikesFilter}
                onChange={(e) => setMinLikesFilter(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    setPostsSettings(false);
                  }
                }}
              />
            </li>
            <li className="filter">
              <input
                type="number"
                placeholder="Filter by max likes..."
                value={maxLikesFilter}
                onChange={(e) => setMaxLikesFilter(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    setPostsSettings(false);
                  }
                }}
              />
            </li>
            <li className="sort">
              <select value={sortOrder} onChange={(e) => {
                setSortOrder(e.target.value);
                setPostsSettings(false);
              }}>
                <option value="date-desc">Data ↓</option>
                <option value="date-asc">Data ↑</option>
              </select>
            </li>
          </ul>
        )}
        {posts.map((item) => (
          <div className="post" key={item.id}>
            <div className="up">
              <img src={item.user?.profilePicture || defaultAvatar} alt=""
                onClick={() => handleProfileClick(item.user.nickName)} />
              <div className="userData">
                <p>{item.user.fullName}</p>
                <span>{formatTimeAgo(item.creationDate)}</span>
              </div>
              {reqUser && reqUser?.id === item.user.id && (
                <>
                  <i onClick={() => handleMenuToggle(item.id)}><FaEllipsisH /></i>
                  {menuPost === item.id && (
                    <ul className="list">
                      <li className="option" onClick={() => toggleEditPost(item)}>
                        <span>Edit</span>
                      </li>
                      <li className="option" onClick={() => {
                        handleDeletePost(item.id);
                        setMenuPost(false);
                      }}>
                        <span>Delete</span>
                      </li>
                    </ul>
                  )}
                </>
              )}
            </div>
            <div className="middle">
              <img src={item?.image} alt="" />
            </div>
            <div className="description">
              <p>{item.description}</p>
            </div>
            <div className="bottom">
              <div className="likes">
                <i onClick={() => likePostHandler(item.id)}>
                  {item.likedByUser ? <AiFillLike /> : <AiOutlineLike />}
                </i>
                <span>{item.likeCount || 0}</span>
              </div>
              <div className="comments" onClick={() => togglePost(item)}>
                <i><TfiCommentAlt /></i>
                <span>{item.commentCount || 0} comments</span>
              </div>
            </div>
          </div>
        ))}
      </div>
      {editPost && <EditPost post={postDetail} onClose={() => setEditPost(prev => !prev)} />}
      {selectedPost && <Post post={postDetail} onClose={togglePost} />}
    </div>
  );
}

export { Feed };