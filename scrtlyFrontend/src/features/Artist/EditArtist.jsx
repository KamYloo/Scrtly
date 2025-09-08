import React, {useEffect, useState} from 'react'
import {MdCancel} from "react-icons/md";
import toast from "react-hot-toast";
import {useUpdateArtistMutation} from "../../Redux/services/artistApi.js";

function EditArtist({artist, onClose}) {
    const [artistBio, setArtistBio] = useState(artist?.artistBio)
    const [bannerImg, setBannerImg] = useState(null)
    const [preview, setPreview] = useState(artist?.bannerImg);
    const [updateArtist, { isLoading }] = useUpdateArtistMutation();

    const handleFileChange = (e) => {
        setBannerImg(e.target.files[0])
    };

    const handleSubmit  = async (e) => {
        e.preventDefault()
        const formData = new FormData()
        if (bannerImg) {
            formData.append('bannerImg', bannerImg);
        }
        formData.append('artistBio', artistBio)

        try {
            await updateArtist(formData).unwrap();
            toast.success('Artist profile updated successfully.');
            onClose();
        } catch (err) {
            toast.error(err.data.businessErrornDescription);
        } finally {
            setBannerImg(null)
        }
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
            <form onSubmit={handleSubmit}>
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
                <button type="submit" className='submit' disabled={isLoading }>
                    {isLoading  ? "Editing..." : "Send"}
                </button>
            </form>
        </div>
    )
}

export {EditArtist}