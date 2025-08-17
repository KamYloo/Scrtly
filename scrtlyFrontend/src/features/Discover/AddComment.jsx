import React, { useState } from 'react'
import EmojiPicker from 'emoji-picker-react'
import { BsEmojiSmileFill } from "react-icons/bs";
import toast from "react-hot-toast";
import {useCreateCommentMutation} from "../../Redux/services/commentApi.js";

function AddComment({ post, parentCommentId }) {
    const [commentText, setCommentText] = useState('')
    const [openEmoji, setOpenEmoji] = useState(false)
    const [createComment, { isLoading }] = useCreateCommentMutation();

    const handleEmoji = (e) => {
        setCommentText(prev => prev + e.emoji)
        setOpenEmoji(false)
    }

    const handleSend = async () => {
        try {
            await createComment({ postId: post.id, parentCommentId, comment: commentText }).unwrap();
            toast.success('Comment created successfully.');
            setCommentText('');
        } catch (err) {
            toast.error(err.data.businessErrornDescription);
        }

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
                placeholder={parentCommentId ? 'Reply to comment...' : 'Add a comment...'}
            ></textarea>
            <button onClick={handleSend} disabled={isLoading}>
                {isLoading ? "Sending..." : "Send"}
            </button>
        </div>
    )
}

export { AddComment }