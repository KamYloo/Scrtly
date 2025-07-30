import React, {useEffect, useState} from 'react'
import {MdCancel} from "react-icons/md";
import toast from "react-hot-toast";
import {useCreatePlaylistMutation, useUpdatePlaylistMutation} from "../../Redux/services/playlistApi.js";

function PlayListForm({onClose, isEdit}) {
    const [title, setTitle] = useState(isEdit?.title || "")
    const [playListImg, setPlayListImg] = useState(null)
    const [preview, setPreview] = useState(isEdit?.playListImage ? `${isEdit.playListImage}` : '');
    const [createPlaylist, { isLoading: creating }] = useCreatePlaylistMutation();
    const [updatePlaylist, { isLoading: updating }] = useUpdatePlaylistMutation();
    const loading = creating || updating;


    const handleFileChange = (e) => {
        setPlayListImg(e.target.files[0])
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!title.trim()) {
            toast.error('Title is required');
            return;
        }
        const form = new FormData();
        form.append('title', title);
        if (playListImg) form.append('file', playListImg);
        try {
            if (isEdit) {
                form.append('playListId', isEdit.id);
                await updatePlaylist(form).unwrap();
                toast.success('Playlist updated successfully.');
            } else {
                await createPlaylist(form).unwrap();
                toast.success('Playlist created successfully.');
            }
            onClose();
        } catch (err) {
            toast.error(err.data.businessErrornDescription);
        }
    }

    useEffect(() => {
        if (playListImg) {
            const previewUrl = URL.createObjectURL(playListImg)
            setPreview(previewUrl)
            return () => {
                URL.revokeObjectURL(previewUrl)
            }
        }
    }, [playListImg])


    return (
        <div className='createPlayList'>
            <i className='cancel' onClick={onClose}><MdCancel/></i>
            <div className="title">
                <h2>{isEdit ? "Edit PlayList" : "Create PlayList"}</h2>
            </div>
            <form onSubmit={handleSubmit}>
                <div className="editPic">
                    <div className="left">
                        <img className="trackImg" src={preview} alt=""/>
                        <span>{playListImg?.name}</span>
                    </div>
                    <div className="right">
                        <input type="file" onChange={handleFileChange}/>
                        <button type="button"
                                onClick={() => document.querySelector('input[type="file"]').click()}>
                            Select Img
                        </button>
                    </div>
                </div>
                <div className="editShortText">
                    <h4>Title</h4>
                    <input type="text" value={title} onChange={(e) => setTitle(e.target.value)}></input>
                </div>
                <button type="submit" className='submit' disabled={loading}>
                    {loading ? "Sending..." : "Send"}
                </button>
            </form>
        </div>
    )
}

export {PlayListForm}