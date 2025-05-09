import React, {useEffect} from 'react'
import { AiOutlineLike,  AiFillLike } from "react-icons/ai";
import { AddComment } from './AddComment.jsx';
import {useDispatch, useSelector} from "react-redux";
import {getAllPostComments, likeComment} from "../../Redux/Comment/Action.js";
import {formatDistanceToNow} from "date-fns";
import {useNavigate} from "react-router-dom";
import Spinner from "../../Components/Spinner.jsx";

// eslint-disable-next-line react/prop-types
function Comments({post}) {
    const { comment } = useSelector(store => store)
    const dispatch = useDispatch()
    const navigate = useNavigate();

    const formatTimeAgo = (timestamp) => {
        return formatDistanceToNow(new Date(timestamp), { addSuffix: true });
    };

    const likeCommentHandler = (commentId) => {
        dispatch(likeComment(commentId))
    }

    useEffect(() => {

            dispatch(getAllPostComments(post.id))

    }, [dispatch])

    if (comment.loading) {
        return (
            <div className="commentsSection">
                <Spinner />
            </div>)
    }

    if (comment.error) {
        return (
            <div className="comments">
                <p>Błąd: {comment.error}</p>
            </div>
        );
    }

  return (
    <div className='commentsSection'>
        <div className="up">
            <img src={post?.user.profilePicture} alt="" onClick={() => navigate(`/profile/${post?.user.nickName}`)}/>
            <p>{post?.user.fullName}</p>
        </div>
        <hr className="line" />
        <div className="comments">
            <div className="comment Own">
            <img src={post?.user.profilePicture} alt="" onClick={() => navigate(`/profile/${post?.user.nickName}`)}/>
                <div className="context">
                    <p>{post.user.fullName}</p>
                    <span>{post.description}</span>
                    <div className="info">
                        <span>{formatTimeAgo(post.creationDate)}</span>
                    </div>
                </div>
            </div>
            {comment?.comments.content.map((item) => (
                <div className="comment" key={item.id}>
                <img src={item.user?.profilePicture} alt="" onClick={() => navigate(`/profile/${item.user.nickName}`)}/>
                <div className="context">
                    <p>{item.user.fullName}</p>
                    <span>{item.comment}</span>
                    <div className="info">
                        <span>{formatTimeAgo(item.creationDate)}</span>
                        <span>{item.likeCount} likes</span>
                    </div>
                </div>
                <i onClick={() => likeCommentHandler(item.id)}>{item.likedByUser ? <AiFillLike /> : <AiOutlineLike />}</i>
            </div>))}
        </div>
        <hr className="line" />
        <AddComment post={post}/>
    </div>
  )
}

export  {Comments}