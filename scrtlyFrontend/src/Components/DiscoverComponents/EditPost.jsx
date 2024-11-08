import React, { useEffect, useRef, useState } from 'react';
import { MdCancel } from "react-icons/md";
import { useDispatch } from "react-redux";
import "../../Styles/form.css";
import { BASE_API_URL } from "../../config/api.js";
import {updatePost} from "../../Redux/Post/Action.js";

function EditPost({ post, onClose }) {
    const [description, setDescription] = useState(post?.description || "");
    const [postImg, setPostImg] = useState(null);
    const [preview, setPreview] = useState(post?.image ? `${BASE_API_URL}${post.image}` : '');
    const fileInputRef = useRef(null);
    const dispatch = useDispatch();

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile) {
            setPostImg(selectedFile);
        }
    };

    const editPostHandler = (e) => {
        e.preventDefault(); // Prevents form default behavior
        const formData = new FormData();
        formData.append('postId', post?.id);
        formData.append('file', postImg);
        formData.append('description', description);
        dispatch(updatePost(formData))
        setPostImg(null);
        onClose();
    };

    useEffect(() => {
        if (postImg) {
            const previewUrl = URL.createObjectURL(postImg);
            setPreview(previewUrl);
            return () => URL.revokeObjectURL(previewUrl);
        }
    }, [postImg]);

    return (
        <div className='editPost'>
            <i className='cancel' onClick={onClose}><MdCancel /></i>
            <div className="title">
                <h2>Edit Post</h2>
            </div>
            <form onSubmit={editPostHandler}>
                <div className="editPic">
                    <div className="left">
                        <img className="postImg" src={preview} alt="" />
                        <span>{postImg?.name}</span>
                    </div>
                    <div className="right">
                        <input
                            type="file"
                            ref={fileInputRef}
                            onChange={handleFileChange}
                            style={{ display: 'none' }}
                        />
                        <button type="button" onClick={() => fileInputRef.current.click()}>
                            Select Img
                        </button>
                    </div>
                </div>
                <div className="editShortText">
                    <h4>Description</h4>
                    <input
                        type="text"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                    />
                </div>
                <button type="submit" className='submit'>Send</button>
            </form>
        </div>
    );
}

export { EditPost };
