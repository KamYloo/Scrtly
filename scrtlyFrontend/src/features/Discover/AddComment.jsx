import React, { useState } from 'react'
import EmojiPicker from 'emoji-picker-react'
import { BsEmojiSmileFill } from "react-icons/bs";
import { useDispatch, useSelector } from "react-redux";
import { createComment } from "../../Redux/Comment/Action.js";
import toast from "react-hot-toast";

function AddComment({ post, parentCommentId }) {
    const dispatch = useDispatch()
    const [commentText, setCommentText] = useState('')
    const [openEmoji, setOpenEmoji] = useState(false)
    const { createLoading, error } = useSelector(state => state.comment);

    const handleEmoji = (e) => {
        setCommentText(prev => prev + e.emoji)
        setOpenEmoji(false)
    }

    const handleCommentCreation = () => {
        dispatch(createComment({ postId: post.id, comment: commentText, parentCommentId }))
            .then(() => {
                toast.success('Comment created successfully.');
            })
            .catch(() => {
                toast.error(error);
            })
            .finally(() => {
                setCommentText('')
            });

    }

    return (
        <div className='addComment'>
            <div className="emoji">
                <i onClick={() => setOpenEmoji(prev => !prev)}><BsEmojiSmileFill /></i>
                <div className="picker">
                    <EmojiPicker open={openEmoji} onEmojiClick={handleEmoji} />
                </div>
            </div>
            <textarea 
                value={commentText}
                onChange={e => setCommentText(e.target.value)} 
                placeholder={parentCommentId ? 'Odpowiedz na komentarz...' : 'Dodaj komentarz...'}
            ></textarea>
            <button onClick={handleCommentCreation} disabled={createLoading}>
                {createLoading ? "Sending..." : "Send"}
            </button>
        </div>
    )
}

export { AddComment }