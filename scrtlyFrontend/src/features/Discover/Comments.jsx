import React, { useRef, useState } from 'react'
import { AiOutlineLike, AiFillLike, AiOutlineDelete } from "react-icons/ai";
import { AddComment } from './AddComment.jsx';
import { formatDistanceToNow } from "date-fns";
import { useNavigate } from "react-router-dom";
import Spinner from "../../Components/Spinner.jsx";
import defaultAvatar from "../../assets/user.jpg";
import toast from "react-hot-toast";
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";
import {
    useDeleteCommentMutation, useGetCommentsByPostQuery, useGetRepliesQuery
    , useLikeCommentMutation
} from "../../Redux/services/commentApi.js";


function Comments({ post }) {
    const { data: reqUser } = useGetCurrentUserQuery(null, {
        skip: !localStorage.getItem('isLoggedIn'),
    });

    const navigate = useNavigate();
    const [currentPage, setCurrentPage] = useState(0);
    const commentsContainerRef = useRef(null);

    const {
        data: commentsResult,
        isFetching: fetchingComments,
        error: commentsError,
    } = useGetCommentsByPostQuery(
        { postId: post.id, page: currentPage, size: 20 },
        { refetchOnMountOrArgChange: true }
    );

    const allComments = commentsResult?.content || [];
    const totalPages = commentsResult?.totalPages ?? 0;

    const [activeReplyInputId, setActiveReplyInputId] = useState(null);
    const [activeRepliesId, setActiveRepliesId] = useState(null);
    const [currentReplyPage, setCurrentReplyPage] = useState(0);
    const repliesContainerRef = useRef({});

    const {
        data: repliesResult,
        isFetching: fetchingReplies,
    } = useGetRepliesQuery(
        { parentCommentId: activeRepliesId, page: currentReplyPage, size: 20 },
        { skip: activeRepliesId == null, refetchOnMountOrArgChange: true }
    );
    const allReplies = repliesResult?.content || [];
    const totalReplyPages = repliesResult?.totalPages ?? 0;

    const [likeComment] = useLikeCommentMutation();
    const [deleteComment] = useDeleteCommentMutation();

    const commentRefs = useRef({});

    const onScroll = () => {
        const el = commentsContainerRef.current;
        const { scrollTop, clientHeight, scrollHeight } = el;

        let firstVisibleIdx = allComments.findIndex((c, i) => {
            const node = commentRefs.current[c.id];
            if (!node) return false;
            const offset = node.offsetTop;
            return offset + node.clientHeight > scrollTop;
        });

        if (firstVisibleIdx >= 0) {
            const pageOfFirst = Math.floor(firstVisibleIdx / 20);
            if (pageOfFirst !== currentPage) {
                setCurrentPage(pageOfFirst);
            }
        }

        if (
            !fetchingComments &&
            scrollTop + clientHeight >= scrollHeight - 10 &&
            currentPage < totalPages - 1
        ) {
            setCurrentPage(p => p + 1);
        }
    };

    const handleRepliesScroll = (e) => {
        const el = e.currentTarget;
        const { scrollTop, clientHeight, scrollHeight } = el;

        let firstVisibleIdx = allReplies.findIndex((r, i) => {
            const node = repliesContainerRef.current[r.id];
            if (!node) return false;
            return node.offsetTop + node.clientHeight > scrollTop;
        });

        if (firstVisibleIdx >= 0) {
            const pageOfFirst = Math.floor(firstVisibleIdx / 20);
            if (pageOfFirst !== currentReplyPage) setCurrentReplyPage(pageOfFirst);
        }

        if (!fetchingReplies && scrollTop + clientHeight >= scrollHeight - 10 && currentReplyPage < totalReplyPages - 1) {
            setCurrentReplyPage(p => p + 1);
        }
    };

    const toggleReplies = (id) => {
        if (activeRepliesId === id) {
            setActiveRepliesId(null);
            setCurrentReplyPage(0);
        } else {
            setActiveRepliesId(id);
            setCurrentReplyPage(0);
        }
    };

    const formatTimeAgo = (timestamp) => formatDistanceToNow(new Date(timestamp), { addSuffix: true });

    const handleLike = async (commentId, parentCommentId = null) => {
        try {
            await likeComment({ commentId, postId: post.id, parentCommentId }).unwrap();
        } catch (err) {
            const msg = (err.data && (err.data.message || err.data.error)) || err.error;
            toast.error(msg);
        }
    };

    const handleDelete = async (commentId, parentCommentId = null) => {
        if (!window.confirm('Are you sure you want to delete this comment?')) return;
        try {
            await deleteComment({ commentId, postId: post.id, parentCommentId }).unwrap();
            toast.success('Comment deleted successfully.');
        } catch (err) {
            const msg = (err.data && (err.data.message || err.data.error)) || err.error;
            toast.error(msg);
        }
    };

    const toggleReplyInput = (parentId) => setActiveReplyInputId(prev => prev === parentId ? null : parentId);

    return (
        <div className='commentsSection'>
            <div className="up">
                <img src={post?.user.profilePicture || defaultAvatar} alt="" onClick={() => navigate(`/profile/${post?.user.nickName}`)} />
                <p>{post?.user.fullName}</p>
            </div>
            <hr className="line" />

            {fetchingComments && <Spinner />}
            {commentsError && <p>Error: {commentsError.toString()}</p>}

            <div className="comments"
                 ref={commentsContainerRef}
                 onScroll={onScroll}>
                <div className="comment Own">
                    <img src={post?.user.profilePicture || defaultAvatar} alt="" onClick={() => navigate(`/profile/${post?.user.nickName}`)} />
                    <div className="context">
                        <p>{post.user.fullName}</p>
                        <span>{post.description}</span>
                        <div className="info"><span>{formatTimeAgo(post.creationDate)}</span></div>
                    </div>
                </div>
                {allComments.map((item) => (
                    <div key={item.id} className="commentContainer"  ref={node => {
                        if (node) commentRefs.current[item.id] = node;
                    }}>
                        <div className="comment">
                            <img src={item.user?.profilePicture || defaultAvatar} alt="" onClick={() => navigate(`/profile/${item.user.nickName}`)} />
                            <div className="context">
                                <p>{item.user.fullName}</p>
                                <span>{item.comment}</span>
                                <div className="info">
                                    <span>{formatTimeAgo(item.creationDate)}</span>
                                    <span>{item.likeCount} likes</span>
                                </div>
                            </div>
                            <div className="commentIcons">
                                <i onClick={() => handleLike(item.id, null)}>
                                    {item.likedByUser ? <AiFillLike /> : <AiOutlineLike />}
                                </i>
                                {item.user?.nickName === reqUser?.nickName && (
                                    <i onClick={() => handleDelete(item.id, null)} style={{ marginLeft: '10px' }}>
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
                            <div className="repliesContainer"  onScroll={handleRepliesScroll}
                                 ref={el => (repliesContainerRef.current[item.id] = el)} style={{ maxHeight: '300px', overflowY: 'auto', overflowAnchor: 'none' }}>
                                {allReplies.map(reply => (
                                    <div key={reply.id} className="comment reply">
                                        <img src={reply.user?.profilePicture || defaultAvatar} alt="" onClick={() => navigate(`/profile/${reply.user.nickName}`)} />
                                        <div className="context">
                                            <p>{reply.user.fullName}</p>
                                            <span>{reply.comment}</span>
                                            <div className="info">
                                                <span>{formatTimeAgo(reply.creationDate)}</span>
                                                <span>{reply.likeCount} likes</span>
                                            </div>
                                        </div>
                                        <div className="commentIcons">
                                            <i onClick={() => handleLike(reply.id, reply.parentCommentId)}>
                                                {reply.likedByUser ? <AiFillLike /> : <AiOutlineLike />}
                                            </i>
                                            {reply.user?.nickName === reqUser?.nickName && (
                                                <i onClick={() => handleDelete(reply.id, reply.parentCommentId)} style={{ marginLeft: '10px' }}>
                                                    <AiOutlineDelete />
                                                </i>
                                            )}
                                        </div>
                                    </div>
                                ))}
                                {fetchingReplies  && <Spinner />}
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
