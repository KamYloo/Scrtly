import React, {useEffect, useState} from 'react'
import toast from 'react-hot-toast';
import "../../Styles/Album&&PlayList.css"
import { AudioList } from '../Song/AudioList.jsx'
import {useNavigate, useParams} from "react-router-dom";
import {AddSong} from "../Song/addSong.jsx";
import Spinner from "../../Components/Spinner.jsx";
import ErrorOverlay from "../../Components/ErrorOverlay.jsx";
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";
import {useDeleteAlbumMutation, useGetAlbumQuery, useGetAlbumTracksQuery} from "../../Redux/services/albumApi.js";

// eslint-disable-next-line react/prop-types
function Album({ volume, onTrackChange}) {
    const {albumId} = useParams()
    const { data: reqUser } = useGetCurrentUserQuery(null, {
        skip: !localStorage.getItem('isLoggedIn'),
    });
    const [addSong, setAddSong] = useState(false)
    const navigate = useNavigate()
    const [pollInterval, setPollInterval] = useState(4000);
    const {
        data: albumData,
        isLoading: isAlbumLoading,
        isError: isAlbumError,
        error: albumError,
    } = useGetAlbumQuery(albumId)

    const {
        data: tracks = [],
        isLoading: isTracksLoading,
        isError: isTracksError,
        error: tracksError,
    } = useGetAlbumTracksQuery(albumId, {
        pollingInterval: pollInterval,
    })
    const [deleteAlbum, { isLoading: isDeleting }] = useDeleteAlbumMutation()

    const handleDelete = async () => {
        if (!window.confirm('Are you sure you want to delete this album?')) return
        try {
            await deleteAlbum(albumId).unwrap()
            toast.success('Album deleted successfully.')
            navigate('/albums')
        } catch (err) {
            toast.error(err.data.businessErrornDescription);
        }
    };

    function formatTime(seconds) {
        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const remainingSeconds = seconds % 60;

        const hoursDisplay = hours > 0 ? `${hours}h ` : '';
        const minutesDisplay = minutes > 0 ? `${minutes}min ` : '';
        const secondsDisplay = remainingSeconds > 0 ? `${remainingSeconds}s` : '';

        return `${hoursDisplay}${minutesDisplay}${secondsDisplay}`.trim();
    }

    useEffect(() => {
        if (!tracks) return;
        const missing = tracks.some(t => !t.hlsManifestUrl);
        setPollInterval(missing ? 4000 : 0);
    }, [tracks]);

    if (isAlbumLoading || isTracksLoading) {
        return <Spinner />;
    }
    if (isAlbumError || isTracksError) {
        const message = albumError?.status
            ? `Error ${albumError.status}: ${albumError.error}`
            : tracksError?.status
                ? `Error ${tracksError.status}: ${tracksError.error}`
                : 'Unknown error'
        return <ErrorOverlay error={message} />
    }
    const { title, albumImage, artist, tracksCount, releaseDate, totalDuration } = albumData
    return (
        <div className='albumDetail'>
            <div className="topSection">
                <img src={albumImage || ''} alt="" />
                <div className="albumData">
                    <p>Album</p>
                    <h1 className='albumName'>{title}</h1>
                    <p className='stats'>{artist.pseudonym} • {tracksCount} Songs <span>• {releaseDate} • {formatTime(totalDuration)}</span> </p>
                </div>
                {artist.id === reqUser?.id && (
                <div className="buttons">
                    <button className="addSongBtn" onClick={() =>
                        setAddSong(((prev) => !prev))}>Add Song</button>
                    <button className="deleteAlbumBtn" onClick={handleDelete}  disabled={isDeleting} > {isDeleting ? 'Deleting…' : 'Delete Album'}</button>
                </div>)}

            </div>
            <AudioList volume={volume} onTrackChange={onTrackChange} initialSongs={tracks} req_artist={artist.id === reqUser?.id}  isplayListSongs={false}/>
            {addSong && <AddSong onClose={() => setAddSong(((prev) => !prev))} albumId={albumId} />}
        </div>
    )
}
export { Album }