import React, {useEffect, useState} from 'react'
import {MdCancel} from "react-icons/md";
import {updateArtist} from "../../Redux/Artist/Action.js";
import {useDispatch} from "react-redux";
import toast from "react-hot-toast";

function EditArtist({onClose}) {
    const [artistBio, setArtistBio] = useState()
    const [bannerImg, setBannerImg] = useState(null)
    const [preview, setPreview] = useState('');
    const dispatch = useDispatch()

    const handleFileChange = (e) => {
        setBannerImg(e.target.files[0])
    };

    const updateArtistHandler = (e) => {
        e.preventDefault()
        const formData = new FormData()
        formData.append('bannerImg', bannerImg)
        formData.append('artistBio', artistBio)

        dispatch(updateArtist(formData))
            .then(() => {
                toast.success('Artist profile updated successfully.');
            })
            .catch(() => {
                toast.error('Failed to update artist profile. Please try again.');
            })
            .finally(() => {
                setBannerImg(null)
                onClose();
            });
    }

    useEffect(() => {
        if (bannerImg) {
            const previewUrl = URL.createObjectURL(bannerImg)
            setPreview(previewUrl)
            return () => {
                URL.revokeObjectURL(previewUrl)
            }
        }
    }, [bannerImg])

    return (
        <div className='editArtist'>
            <i className='cancel' onClick={onClose}><MdCancel/></i>
            <div className="title">
                <h2>Edit Artist</h2>
            </div>
            <form onSubmit={updateArtistHandler}>
                <div className="editPic">
                    <div className="left">
                        <img src={preview} alt=""/>
                        <span>{bannerImg?.name}</span>
                    </div>
                    <div className="right">
                        <input type="file" onChange={handleFileChange}/>
                        <button type="button"
                                onClick={() => document.querySelector('input[type="file"]').click()}>
                            Select Banner Img
                        </button>
                    </div>
                </div>
                <div className="editLongText">
                    <h4>Artist Bio</h4>
                    <textarea value={artistBio} onChange={(e) => setArtistBio(e.target.value)}></textarea>
                </div>
                <button type="submit" className='submit'>Send</button>
            </form>
        </div>
    )
}

export {EditArtist}