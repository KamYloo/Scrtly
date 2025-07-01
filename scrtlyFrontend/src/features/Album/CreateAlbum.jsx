import React, {useEffect, useState} from 'react'
import {MdCancel} from "react-icons/md";
import {useDispatch, useSelector} from "react-redux";
import {createAlbum} from "../../Redux/Album/Action.js";
import toast from "react-hot-toast";

function CreateAlbum({onClose}) {
    const [title, setTitle] = useState()
    const [albumImg, setAlbumImg] = useState(null)
    const [preview, setPreview] = useState('');
    const dispatch = useDispatch()
    const { loading, error } = useSelector(state => state.album);

    const handleFileChange = (e) => {
        setAlbumImg(e.target.files[0])
    };

    const createAlbumHandler = (event) => {
        event.preventDefault()
        const formData = new FormData();
        formData.append('file', albumImg);
        formData.append('title', title);

        dispatch(createAlbum(formData))
            .then(() => {
                toast.success('Album created successfully.');
            })
            .catch(() => {
                toast.error(error);
            })
            .finally(() => {
                setAlbumImg(null);
                setTitle(null);
                onClose();
            });
    };


    useEffect(() => {
        if (albumImg) {
            const previewUrl = URL.createObjectURL(albumImg)
            setPreview(previewUrl)
            return () => {
                URL.revokeObjectURL(previewUrl)
            }
        }
    }, [albumImg])

    return (
        <div className='createAlbum'>
            <i className='cancel' onClick={onClose}><MdCancel/></i>
            <div className="title">
                <h2>Create Album</h2>
            </div>
            <form onSubmit={createAlbumHandler}>
                <div className="editPic">
                    <div className="left">
                        <img className="trackImg" src={preview} alt=""/>
                        <span>{albumImg?.name}</span>
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
                    {loading ? "Creating..." : "Send"}
                </button>
            </form>
        </div>
    )
}

export {CreateAlbum}