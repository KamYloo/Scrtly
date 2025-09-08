import React, { useEffect, useRef, useState } from 'react';
import { MdCancel } from "react-icons/md";
import "../../Styles/form.css";
import toast from "react-hot-toast";
import {useUpdatePostMutation} from "../../Redux/services/postApi.js";

function EditPost({ post, onClose }) {
    const [description, setDescription] = useState(post?.description || "");
    const [postImg, setPostImg] = useState(null);
    const [preview, setPreview] = useState(post?.image ? `${post.image}` : '');
    const fileInputRef = useRef(null);
    const [updatePost, { isLoading }] = useUpdatePostMutation();

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile) {
            setPostImg(selectedFile);
        }
    };

    const editPostHandler = async (e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append('postId', post?.id);
        if (postImg)
            formData.append('file', postImg);
        formData.append('description', description);

        try {
            await updatePost(formData).unwrap();
            toast.success('Post updated successfully.');
        } catch (err) {
            toast.error(err.data.businessErrornDescription);
        } finally {
            setDescription('')
            setPostImg(null);
            onClose();
        }
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
                        <img className="postImg" src={preview} alt=""/>
                        <span>{postImg?.name}</span>
                    </div>
                    <div className="right">
                        <input
                            type="file"
                            ref={fileInputRef}
                            onChange={handleFileChange}
                            style={{display: 'none'}}
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
                <button type="submit" className='submit' disabled={isLoading}>
                    {isLoading ? "Sending..." : "Send"}
                </button>
            </form>
        </div>
    );
}

export {EditPost};
