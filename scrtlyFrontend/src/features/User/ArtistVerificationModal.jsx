import React, {useEffect, useState} from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {verifyArtistAction} from "../../Redux/UserService/Action.js";
import toast from "react-hot-toast";


function ArtistVerificationModal({ onClose }) {
    const [artistName, setArtistName] = useState('');
    const dispatch = useDispatch();
    const {error } = useSelector(state => state.userService);

    const handleSubmit = (e) => {
        e.preventDefault();
        dispatch(verifyArtistAction(artistName))
            .then(() => {
                toast.success('Verification has been sent for review');
                onClose();
            })
    };

    useEffect(() => {
        if (error) {
            toast.error(error);
        }
    }, [error]);

    return (
        <div className="verifyArtist">
            <div className="title">
                <h2>Artist Verification</h2>
            </div>
            <form onSubmit={handleSubmit}>
                <div className="editShortText">
                    <h4>Artist Name</h4>
                    <input
                        type="text"
                        value={artistName}
                        onChange={(e) => setArtistName(e.target.value)}
                        placeholder="Enter the artist's name..."
                        required
                    />
                </div>
                <button type="submit" className="submit">Send application</button>
                <button type="button" className="submit" onClick={onClose} style={{ marginTop: '10px' }}>
                    Cancel
                </button>
            </form>
        </div>
    );
}

export default ArtistVerificationModal;