import React, {useEffect, useState} from 'react'
import {MdCancel} from "react-icons/md";
import toast from "react-hot-toast";
import {useCreateAlbumMutation} from "../../Redux/services/albumApi.js";

function CreateAlbum({onClose}) {
    const [title, setTitle] = useState()
    const [albumImg, setAlbumImg] = useState(null)
    const [preview, setPreview] = useState('');
    const [createAlbum, { isLoading }] = useCreateAlbumMutation()

    const handleFileChange = (e) => {
        setAlbumImg(e.target.files[0])
    };

    const handleSubmit = async (event) => {
        event.preventDefault()
        const formData = new FormData();
        formData.append('file', albumImg);
        formData.append('title', title);

        try {
            await createAlbum(formData).unwrap()
            toast.success('Album created successfully.')
            onClose()
        } catch (err) {
            const message =
                err?.status ? `Error ${err.status}: ${err.data || err.error}` : 'Failed to create album.'
            toast.error(message)
        } finally {
            setAlbumImg(null);
            setTitle(null);
        }
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
            <form onSubmit={handleSubmit}>
                <div className="editPic">
                    <div className="left">
                        <img className="trackImg" src={preview} alt=""/>
                        <span>{albumImg?.name}</span>
                    </div>
                    <div className="right">
                        <input type="file" accept="image/*"  onChange={handleFileChange}/>
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
                <button type="submit" className='submit' disabled={isLoading}>
                    {isLoading ? "Creating..." : "Send"}
                </button>
            </form>
        </div>
    )
}

export {CreateAlbum}