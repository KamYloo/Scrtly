import React, {useEffect, useState} from 'react'
import {MdCancel} from "react-icons/md";
import {useDispatch} from "react-redux";
import {createPlayList} from "../../Redux/PlayList/Action.js";

function AddPlayList({onClose}) {
    const [title, setTitle] = useState()
    const [playListImg, setPlayListImg] = useState(null)
    const [preview, setPreview] = useState('');
    const dispatch = useDispatch()


    const handleFileChange = (e) => {
        setPlayListImg(e.target.files[0])
    };

    const createPlayListHandler = () => {
        const formData = new FormData()
        formData.append('file', playListImg)
        formData.append('title', title)
        dispatch(createPlayList(formData))
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
                <h2>Create PlayList</h2>
            </div>
            <form onSubmit={createPlayListHandler}>
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

export {AddPlayList}