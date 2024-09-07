import React, {useEffect} from 'react'
import { AiOutlineLike,  AiFillLike } from "react-icons/ai";
import { AddComment } from './AddComment';
import {BASE_API_URL} from "../../config/api.js";
import {useDispatch, useSelector} from "react-redux";
import {getAllPostComments, likeComment} from "../../Redux/Comment/Action.js";
import {formatDistanceToNow} from "date-fns";

// eslint-disable-next-line react/prop-types
function Comments({post}) {
    const { comment } = useSelector(store => store)
    const dispatch = useDispatch()

    const formatTimeAgo = (timestamp) => {
        return formatDistanceToNow(new Date(timestamp), { addSuffix: true });
    };

    const likeCommentHandler = (commentId) => {
        dispatch(likeComment(commentId))
    }

    useEffect(() => {

            dispatch(getAllPostComments({postId: post.id}))

    }, [dispatch, post])

  return (
    <div className='commentsSection'>
        <div className="up">
            <img src={`${BASE_API_URL}/${post.user?.profilePicture || ''}`} alt="" />
            <p>{post.user.fullName}</p>
        </div>
        <hr />
        <div className="comments">
            <div className="comment Own">
            <img src={`${BASE_API_URL}/${post.user?.profilePicture || ''}`} alt="" />
                <div className="context">
                    <p>{post.user.fullName}</p>
                    <span>{post.description}</span>
                    <div className="info">
                        <span>{formatTimeAgo(post.creationDate)}</span>
                    </div>
                </div>
            </div>
            {comment.comments.map((item) => (
                <div className="comment" key={item.id}>
                <img src={`${BASE_API_URL}/${item.user?.profilePicture || ''}`} alt=""/>
                <div className="context">
                    <p>{item.user.fullName}</p>
                    <span>{item.comment}</span>
                    <div className="info">
                        <span>{formatTimeAgo(item.creationDate)}</span>
                        <span>{item.likes?.length| 0} likes</span>
                    </div>
                </div>
                <i onClick={() => likeCommentHandler(item.id)}><AiOutlineLike/></i>
            </div>))}
        </div>
        <hr />
        <AddComment post={post}/>
    </div>
  )
}

export  {Comments}