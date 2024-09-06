import React, {useState} from 'react'
import EmojiPicker from 'emoji-picker-react'
import { BsEmojiSmileFill} from "react-icons/bs";
import {useDispatch} from "react-redux";
import {createComment} from "../../Redux/Comment/Action.js";


function AddComment({post}) {
    const dispatch = useDispatch()
    const [commentText, setCommentText] = useState('')

    const handleCommentCreation = () => {
        dispatch(createComment({postId: post.id, comment: commentText}))
        setCommentText('')
    }

  return (
    <div className='addComment'>
        <div className="emoji">
            <i><BsEmojiSmileFill /></i>
            <div className="picker">
              {/* <EmojiPicker /> */}
            </div>
          </div>
          <textarea type="text" value={commentText}
                    onChange={(e) => setCommentText(e.target.value)} placeholder='Add Comment...'></textarea>
          <button onClick={handleCommentCreation}>Send</button>
    </div>
  )
}

export  {AddComment}