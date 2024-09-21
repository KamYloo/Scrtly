import React, {useEffect, useState} from 'react'
import {MdCancel} from "react-icons/md";
import {useDispatch} from "react-redux";
import {createAlbum} from "../../Redux/Album/Action.js";
import "../../Styles/form.css"

function AddSong({onClose}) {
    const [title, setTitle] = useState()
    const [songImg, setSongImg] = useState(null)
    const [preview, setPreview] = useState('');
    const dispatch = useDispatch()

    const handleFileChange = (e) => {
        setSongImg(e.target.files[0])
    };

    const createSongHandler = () => {
        const formData = new FormData()
        formData.append('file', songImg)
        formData.append('title', title)
        dispatch(createAlbum(formData))
        setSongImg(null)
        onClose()
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

export {AddSong}