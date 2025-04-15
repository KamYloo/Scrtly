import React, {useEffect, useState} from 'react'
import {MdCancel} from "react-icons/md";
import {useDispatch, useSelector} from "react-redux";
import {uploadSong} from "../../Redux/Album/Action.js";
import "../../Styles/form.css"
import toast from "react-hot-toast";

// eslint-disable-next-line react/prop-types
function AddSong({onClose, albumId}) {
    const [title, setTitle] = useState()
    const [songImg, setSongImg] = useState(null)
    const [audio, setAudio] = useState(null)
    const [preview, setPreview] = useState('');
    const { loading } = useSelector(state => state.album);

    const dispatch = useDispatch()

    const createSongHandler = (e) => {
        e.preventDefault();
        const formData = new FormData()
        formData.append("songDetails", new Blob([JSON.stringify({ title, albumId })], { type: "application/json" }));
        formData.append("imageSong", songImg);
        formData.append("audioFile", audio);

        dispatch(uploadSong(formData))
            .then(() => {
                toast.success('Song upload successfully.');
            })
            .catch(() => {
                toast.error('Failed to upload song. Please try again.');
            })
            .finally(() => {
                setSongImg(null);
                setAudio(null);
                setTitle('');
                onClose();
            });
    }

    useEffect(() => {
        if (songImg) {
            const previewUrl = URL.createObjectURL(songImg)
            setPreview(previewUrl)
            return () => {
                URL.revokeObjectURL(previewUrl)
            }
        }
    }, [songImg])

    return (
        <div className='addSong'>
            <i className='cancel' onClick={onClose}><MdCancel/></i>
            <div className="title">
                <h2>Create Song</h2>
            </div>
            <form onSubmit={createSongHandler}>
                <div className="editPic">
                    <div className="left">
                        <img className="trackImg" src={preview} alt=""/>
                        <span>{songImg?.name}</span>
                    </div>
                    <div className="right">
                        <input type="file" accept="image/*" onChange={(e) => setSongImg(e.target.files[0])}/>
                        <button type="button"
                                onClick={() => document.querySelector('input[type="file"][accept="image/*"]').click()}>
                            Select Img
                        </button>
                    </div>
                </div>
                <div className="editAudio">
                    <div className="left">
                        <span>{audio?.name}</span>
                    </div>
                    <div className="right">
                        <input type="file" accept="audio/*" onChange={(e) => setAudio(e.target.files[0])}/>
                        <button type="button"
                                onClick={() => document.querySelector('input[type="file"][accept="audio/*"]').click()}>
                            Select Audio
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

export {AddSong}