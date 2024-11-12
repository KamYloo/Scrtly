import React, {useEffect, useState} from 'react'
import {MdCancel} from "react-icons/md";
import {useDispatch} from "react-redux";
import {createPlayList, updatePlayList} from "../../Redux/PlayList/Action.js";
import {BASE_API_URL} from "../../config/api.js";
import toast from "react-hot-toast";

function PlayListForm({onClose, isEdit}) {
    const [title, setTitle] = useState(isEdit?.title || "")
    const [playListImg, setPlayListImg] = useState(null)
    const [preview, setPreview] = useState(isEdit?.playListImage ? `${BASE_API_URL}${isEdit.playListImage}` : '');
    const dispatch = useDispatch()

    const handleFileChange = (e) => {
        setPlayListImg(e.target.files[0])
    };

    const playListHandler = () => {
        const formData = new FormData()
        formData.append('file', playListImg)
        formData.append('title', title)
        if (isEdit) {
            formData.append("playListId", isEdit?.id)
            dispatch(updatePlayList(formData))
                .then(() => {
                    toast.success('Playlist updated successfully.');
                })
                .catch(() => {
                    toast.error('Failed to update playlist. Please try again.');
                });
        } else {
            dispatch(createPlayList(formData))
                .then(() => {
                    toast.success('Playlist created successfully.');
                })
                .catch(() => {
                    toast.error('Failed to create playlist. Please try again.');
                });
        }
        setTitle('')
        setPlayListImg(null)
        onClose()
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
            <form onSubmit={playListHandler}>
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
                <button type="submit" className='submit'>Send</button>
            </form>
        </div>
    )
}

export {PlayListForm}