import React, { useEffect, useRef, useState } from 'react'
import { AiOutlineLike, AiFillLike, AiOutlineDelete } from "react-icons/ai";
import { AddComment } from './AddComment.jsx';
import { useDispatch, useSelector } from "react-redux";
import { getAllPostComments, likeComment, deleteComment, getReplies } from "../../Redux/Comment/Action.js";
import { formatDistanceToNow } from "date-fns";
import { useNavigate } from "react-router-dom";
import Spinner from "../../Components/Spinner.jsx";

function Comments({ post }) {
    const { comment, auth } = useSelector(store => store)
    const dispatch = useDispatch()
    const navigate = useNavigate();

    const [activeReplyInputId, setActiveReplyInputId] = useState(null);
    const [activeRepliesId, setActiveRepliesId] = useState(null);
    const [repliesData, setRepliesData] = useState([]);
    const [currentReplyPage, setCurrentReplyPage] = useState(0);
    const [replyTotalPages, setReplyTotalPages] = useState(0);
    const [isLoadingReplies, setIsLoadingReplies] = useState(false);

    const repliesRefs = useRef({});

    const formatTimeAgo = (timestamp) => formatDistanceToNow(new Date(timestamp), { addSuffix: true });

    const likeCommentHandler = (commentId) => dispatch(likeComment(commentId));

    const handleDeleteComment = (commentId) => {
        if (window.confirm("Are you sure you want to delete this comment?")) {
            dispatch(deleteComment(commentId));
        }
    };

    const loadReplies = async (commentId, page = 0) => {
        setIsLoadingReplies(true);
        const container = repliesRefs.current[commentId];
        const prevScrollHeight = container?.scrollHeight || 0;
        const prevScrollTop = container?.scrollTop || 0;
        const bottomOffset = prevScrollHeight - prevScrollTop;

        const result = await dispatch(getReplies(commentId, page, 10));
        if (result) {
            setRepliesData(prev => page === 0 ? result.content : [...prev, ...result.content]);
            setCurrentReplyPage(result.pageNumber);
            setReplyTotalPages(result.totalPages);
            if (container && page !== 0) {
                setTimeout(() => {
                    const newScrollHeight = container.scrollHeight;
                    container.scrollTop = newScrollHeight - bottomOffset;
                }, 0);
            }
        }
        setIsLoadingReplies(false);
    }

    const toggleReplies = (parentId) => {
        if (activeRepliesId === parentId) {
            setActiveRepliesId(null);
            setRepliesData([]);
            setCurrentReplyPage(0);
            setReplyTotalPages(0);
        } else {
            setActiveRepliesId(parentId);
            loadReplies(parentId, 0);
        }
    }

    const toggleReplyInput = (parentId) => setActiveReplyInputId(prev => prev === parentId ? null : parentId);

    const handleRepliesScroll = (e) => {
        const { scrollTop, scrollHeight, clientHeight } = e.currentTarget;
        if (!isLoadingReplies && currentReplyPage < replyTotalPages - 1 && scrollTop + clientHeight >= scrollHeight - 10) {
            loadReplies(activeRepliesId, currentReplyPage + 1);
        }
    };

    useEffect(() => { dispatch(getAllPostComments(post.id)); }, [dispatch, post.id]);

    useEffect(() => {
        if (activeRepliesId && comment.likeComment) {
            const container = repliesRefs.current[activeRepliesId];
            const prevScrollTop = container?.scrollTop || 0;
            setRepliesData(prev =>
                prev.map(reply => reply.id === comment.likeComment.commentId
                    ? { ...reply, likedByUser: comment.likeComment.likedByUser, likeCount: comment.likeComment.likeCount }
                    : reply
                )
            );
            setTimeout(() => { if (container) container.scrollTop = prevScrollTop; }, 0);
        }
    }, [comment.likeComment, activeRepliesId]);

    useEffect(() => {
        if (comment.createdComment && comment.createdComment.parentCommentId === activeRepliesId) {
            setRepliesData(prev => [comment.createdComment, ...prev]);
        }
    }, [comment.createdComment, activeRepliesId]);

    useEffect(() => {
        if (comment.deletedComment && activeRepliesId) {
            // Filter out the deleted reply from the repliesData list
            setRepliesData(prev => prev.filter(reply => reply.id !== comment.deletedComment));
        }
    }, [comment.deletedComment, activeRepliesId]);

    return (
        <div className='commentsSection'>
            <div className="up">
                <img src={post?.user.profilePicture} alt="" onClick={() => navigate(`/profile/${post?.user.nickName}`)} />
                <p>{post?.user.fullName}</p>
            </div>
            <hr className="line" />

            {comment.loading && (
                <Spinner />
            )}
            {comment.error && (
                <p>Błąd: {comment.error}</p>
            )}

            <div className="comments">
                <div className="comment Own">
                    <img src={post?.user.profilePicture} alt="" onClick={() => navigate(`/profile/${post?.user.nickName}`)} />
                    <div className="context">
                        <p>{post.user.fullName}</p>
                        <span>{post.description}</span>
                        <div className="info"><span>{formatTimeAgo(post.creationDate)}</span></div>
                    </div>
                </div>
                {comment.comments.content.filter(c => !c.parentCommentId).map(item => (
                    <div key={item.id} className="commentContainer">
                        <div className="comment">
                            <img src={item.user?.profilePicture} alt="" onClick={() => navigate(`/profile/${item.user.nickName}`)} />
                            <div className="context">
                                <p>{item.user.fullName}</p>
                                <span>{item.comment}</span>
                                <div className="info">
                                    <span>{formatTimeAgo(item.creationDate)}</span>
                                    <span>{item.likeCount} likes</span>
                                </div>
                            </div>
                            <div className="commentIcons">
                                <i onClick={() => likeCommentHandler(item.id)}>
                                    {item.likedByUser ? <AiFillLike /> : <AiOutlineLike />}
                                </i>
                                {item.user?.nickName === auth.reqUser?.nickName && (
                                    <i onClick={() => handleDeleteComment(item.id)} style={{ marginLeft: '10px' }}>
                                        <AiOutlineDelete />
                                    </i>
                                )}
                            </div>
                        </div>
                        <div className="commentActions">
                            <button className="toggleRepliesBtn" onClick={() => toggleReplies(item.id)}>
                                {activeRepliesId === item.id ? 'Hide Replies' : 'Show Replies'}
                            </button>
                            <button className="replyBtn" onClick={() => toggleReplyInput(item.id)}>Reply</button>
                        </div>
                        {activeRepliesId === item.id && (
                            <div className="repliesContainer" onScroll={handleRepliesScroll} ref={el => { repliesRefs.current[item.id] = el }} style={{ maxHeight: '300px', overflowY: 'auto', overflowAnchor: 'none' }}>
                                {repliesData.map(reply => (
                                    <div key={reply.id} className="comment reply">
                                        <img src={reply.user?.profilePicture} alt="" onClick={() => navigate(`/profile/${reply.user.nickName}`)} />
                                        <div className="context">
                                            <p>{reply.user.fullName}</p>
                                            <span>{reply.comment}</span>
                                            <div className="info">
                                                <span>{formatTimeAgo(reply.creationDate)}</span>
                                                <span>{reply.likeCount} likes</span>
                                            </div>
                                        </div>
                                        <div className="commentIcons">
                                            <i onClick={() => likeCommentHandler(reply.id)}>
                                                {reply.likedByUser ? <AiFillLike /> : <AiOutlineLike />}
                                            </i>
                                            {reply.user?.nickName === auth.reqUser?.nickName && (
                                                <i onClick={() => handleDeleteComment(reply.id)} style={{ marginLeft: '10px' }}>
                                                    <AiOutlineDelete />
                                                </i>
                                            )}
                                        </div>
                                    </div>
                                ))}
                                {isLoadingReplies && <Spinner />}
                            </div>
                        )}
                    </div>
                ))}
            </div>

            <hr className="line" />
            <div className="addCommentWrapper">
                {activeReplyInputId && (
                    <div className="replyingTo">
                        <span>Replying to comment</span>
                        <button onClick={() => setActiveReplyInputId(null)}>Cancel</button>
                    </div>
                )}
                <AddComment post={post} parentCommentId={activeReplyInputId} />
            </div>
        </div>
    );
}

export { Comments };
