import React, {useEffect, useState} from 'react'
import toast from 'react-hot-toast';
import "../../Styles/Album&&PlayList.css"
import { AudioList } from '../SongComponents/AudioList.jsx'
import {useNavigate, useParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {deleteAlbum, getAlbum, getAlbumTracks} from "../../Redux/Album/Action.js";
import {AddSong} from "../SongComponents/addSong.jsx";
import Spinner from "../Spinner.jsx";
import ErrorAlert from "../ErrorAlert.jsx";

// eslint-disable-next-line react/prop-types
function Album({ volume, onTrackChange}) {
    const {albumId} = useParams();
    const dispatch = useDispatch();
    const {album, song} = useSelector(state => state);
    const [addSong, setAddSong] = useState(false)
    const navigate = useNavigate();
    const userData = (() => { try { return JSON.parse(localStorage.getItem("user")) || null; } catch { return null; } })();

    const albumDeleteHandler = () => {
        const confirmDelete = window.confirm('Are you sure you want to delete this album?');
        if (confirmDelete) {
            dispatch(deleteAlbum(albumId)).then(() => {
                toast.success('Album deleted successfully.');
                navigate("/albums");
            }).catch(() => {
                toast.error('Failed to delete album. Please try again.');
            });
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
        dispatch(getAlbum(albumId))
    }, [albumId, album.uploadSong, song.deletedSong,]);

    useEffect(() => {
        dispatch(getAlbumTracks(albumId))
    }, [albumId, album.uploadSong, song.deletedSong, song.likedSong]);


    if (album.loading) {
        return <Spinner />;
    }
    if (album.error) {
        return <ErrorAlert message={album.error} />;
    }

    return (
        <div className='albumDetail'>
            <div className="topSection">
                <img src={album.findAlbum?.albumImage || ''} alt="" />
                <div className="albumData">
                    <p>Album</p>
                    <h1 className='albumName'>{album.findAlbum?.title}</h1>
                    <p className='stats'>{album.findAlbum?.artist.pseudonym} • {album.findAlbum?.tracksCount} Songs <span>• {album.findAlbum?.releaseDate} • {formatTime(album.findAlbum?.totalDuration)}</span> </p>
                </div>
                {album.findAlbum?.artist.id === userData?.id && (
                <div className="buttons">
                    <button className="addSongBtn" onClick={() =>
                        setAddSong(((prev) => !prev))}>Add Song</button>
                    <button className="deleteAlbumBtn" onClick={() => albumDeleteHandler()
                    }>Delete Album</button>
                </div>)}

            </div>
            <AudioList volume={volume} onTrackChange={onTrackChange} initialSongs={album?.songs} req_artist={album.findAlbum?.artist.id === userData?.id}  isplayListSongs={false}/>
            {addSong && <AddSong onClose={() => setAddSong(((prev) => !prev))} albumId={albumId} />}
        </div>
    )
}

export { Album }