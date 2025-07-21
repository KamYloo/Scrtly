import React, {useEffect, useState} from 'react';
import toast from "react-hot-toast";
import {useVerifyArtistMutation} from "../../Redux/services/userApi.js";


function ArtistVerificationModal({ onClose }) {
    const [artistName, setArtistName] = useState('');
    const [verifyArtist, { isLoading, error }] = useVerifyArtistMutation();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await verifyArtist(artistName).unwrap();
            toast.success('Verification has been sent for review');
            onClose();
        } catch (err) {
            const msg = err?.data?.message || err?.error || 'Verification request failed';
            toast.error(msg);
        }
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
                <button type="submit" className="submit" disabled={isLoading}>
                    {isLoading ? 'Sending…' : 'Send application'}
                </button>
                <button type="button" className="submit" onClick={onClose} style={{marginTop: '10px'}}>
                    Cancel
                </button>
            </form>
        </div>
    );
}

export default ArtistVerificationModal;