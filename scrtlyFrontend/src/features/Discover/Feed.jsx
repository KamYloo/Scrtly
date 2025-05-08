import React, { useEffect, useState } from 'react'
import toast from 'react-hot-toast';
import { AiFillLike, AiOutlineLike } from "react-icons/ai";
import { TfiCommentAlt } from "react-icons/tfi";
import { FaEllipsisH } from "react-icons/fa";
import { MdOutlineMenuOpen } from "react-icons/md";
import { Post } from './Post.jsx';
import { useDispatch, useSelector } from "react-redux";
import { deletePost, getAllPosts, likePost } from "../../Redux/Post/Action.js";
import { formatDistanceToNow } from 'date-fns'
import { useNavigate } from "react-router-dom";
import { EditPost } from "./EditPost.jsx";

function Feed() {
  const [selectedPost, setSelectedPost] = useState(false);
  const [postDetail, setPostDetail] = useState(null);
  const [menuPost, setMenuPost] = useState(false);
  const [postsSettings, setPostsSettings] = useState(false);
  const [editPost, setEditPost] = useState(false);
  const [minLikesFilter, setMinLikesFilter] = useState('');
  const [maxLikesFilter, setMaxLikesFilter] = useState('');
  const [sortOrder, setSortOrder] = useState('date-desc');

  const dispatch = useDispatch();
  const { post, comment } = useSelector(store => store);
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

  const handleDeletePost = (postId) => {
    const confirmDelete = window.confirm('Are you sure you want to delete this post?');
    if (confirmDelete) {
      dispatch(deletePost(postId))
      .then(() => {
        toast.success('Post deleted successfully.');
      })
      .catch(() => {
        toast.error('Failed to delete post. Please try again.');
      });
    }
  };

  const likePostHandler = (postId) => {
    dispatch(likePost(postId))
  };

  const loadPosts = () => {
    const sortDir = sortOrder === 'date-asc' ? 'ASC' : 'DESC';
    dispatch(getAllPosts({
      sortDir,
      minLikes: minLikesFilter !== '' ? Number(minLikesFilter) : null,
      maxLikes: maxLikesFilter !== '' ? Number(maxLikesFilter) : null,
      page: 0,
      size: 10,
    }));
  };

  useEffect(() => {
    loadPosts();
  }, [dispatch, minLikesFilter, maxLikesFilter, sortOrder]);

  if (post.loading) {
    return (
      <div className="feed">
        <div className="spinner"></div>
      </div>
    );
  }

  if (post.error) {
    return (
      <div className="feed">
        <p>Błąd: {post.error}</p>
      </div>
    );
  }

  return (
    <div className='feed'>
      <div className="posts">
        <i className="postsSettings" onClick={() => setPostsSettings(prev => !prev)}><MdOutlineMenuOpen/></i>
        {postsSettings && (
          <ul className="filtredMenu">
            <li className="filter">
              <input
                type="number"
                placeholder="Filter by minimum likes..."
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
                placeholder="Filter by maximum likes..."
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
                <option value="date-desc">Sort by date (descending)</option>
                <option value="date-asc">Sort by date (ascending)</option>
              </select>
            </li>
          </ul>
        )}
        {post.posts.content.map((item) => (
          <div className="post" key={item.id}>
            <div className="up">
              <img src={item.user?.profilePicture} alt=""
                   onClick={() => handleProfileClick(item.user.nickName)}/>
              <div className="userData">
                <p>{item.user.fullName}</p>
                <span>{formatTimeAgo(item.creationDate)}</span>
              </div>
              <>
                <i onClick={() => handleMenuToggle(item.id)}><FaEllipsisH/></i>
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
            </div>
            <div className="middle">
              <img src={item?.image} alt=""/>
            </div>
            <div className="description">
              <p>{item.description}</p>
            </div>
            <div className="bottom">
              <div className="likes">
                <i onClick={() => likePostHandler(item.id)}>
                  {item.likedByUser ? <AiFillLike/> : <AiOutlineLike/>}
                </i>
                <span>{item.likeCount || 0}</span>
              </div>
              <div className="comments" onClick={() => togglePost(item)}>
                <i><TfiCommentAlt/></i>
                <span>{item.commentCount || 0} comments</span>
              </div>
            </div>
          </div>
        ))}
      </div>
      {editPost && <EditPost post={postDetail} onClose={() => setEditPost(prev => !prev)}/>}
      {selectedPost && <Post post={postDetail} onClose={togglePost}/>}
    </div>
  );
}

export { Feed };