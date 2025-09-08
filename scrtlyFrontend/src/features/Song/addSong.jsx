import React, {useEffect, useState} from 'react'
import {MdCancel} from "react-icons/md";
import "../../Styles/form.css"
import toast from "react-hot-toast";
import {useUploadSongMutation} from "../../Redux/services/albumApi.js";

// eslint-disable-next-line react/prop-types
function AddSong({onClose, albumId}) {
    const [title, setTitle] = useState()
    const [songImg, setSongImg] = useState(null)
    const [audio, setAudio] = useState(null)
    const [preview, setPreview] = useState('');
    const [uploadSong, { isLoading }] = useUploadSongMutation()

    const handleSubmit  = async (e) => {
        e.preventDefault();
        const formData = new FormData()
        formData.append('title', title || '');
        formData.append('albumId', String(albumId || ''));
        if (songImg)
            formData.append('imageSong', songImg);
        if (audio)
            formData.append('audioFile', audio);

        try {
            await uploadSong({ formData }).unwrap()
            toast.success('Song uploaded successfully.')
            setTitle('')
            setSongImg(null)
            setAudio(null)
            onClose()
        } catch (err) {
            toast.error(err.data.businessErrornDescription)
        }
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

            <form onSubmit={handleSubmit}>
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
                <button type="submit" className='submit' disabled={isLoading}>
                    {isLoading ? "Uploading...": "Send"}
                </button>
            </form>
        </div>
    )
}

export {AddSong}