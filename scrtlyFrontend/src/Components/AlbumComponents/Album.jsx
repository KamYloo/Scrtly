import React, {useEffect, useState} from 'react'
import "../../Styles/Album.css"
import { AudioList } from '../SongComponents/AudioList.jsx'
import {useParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {deleteAlbum, getAlbum, getAlbumTracks} from "../../Redux/Album/Action.js";
import {BASE_API_URL} from "../../config/api.js";
import {AddSong} from "../SongComponents/addSong.jsx";

// eslint-disable-next-line react/prop-types
function Album({ volume, onTrackChange}) {
    const {albumId} = useParams();
    const dispatch = useDispatch();
    const {album} = useSelector(store => store);
    const [addSong, setAddSong] = useState(false)

    const albumDeleteHandler = () => {
        const confirmDelete = window.confirm('Are you sure you want to delete this album?');
        if (confirmDelete) {
            dispatch(deleteAlbum(albumId));
        }
    };

    useEffect(() => {
        dispatch(getAlbum(albumId))
    }, [albumId]);

    useEffect(() => {
        dispatch(getAlbumTracks(albumId))
    }, [albumId, album.uploadSong]);

    return (
        <div className='albumDetail'>
            <div className="topSection">
                <img src={`${BASE_API_URL}${album.findAlbum?.albumImage || ''}`} alt="" />
                <div className="albumData">
                    <p>Album</p>
                    <h1 className='albumName'>{album.findAlbum?.title}</h1>
                    <p className='stats'>{album.findAlbum?.artist.artistName} • 12 Songs <span>• {album.findAlbum?.releaseDate} • 1h 24min</span> </p>
                </div>
                <div className="buttons">
                    {album.findAlbum?.artist.req_artist && (<button className="addSongBtn" onClick={() =>
                        setAddSong(((prev) => !prev))}>Add Song</button>)}
                    <button className="delteAlbumBtn" onClick={() => albumDeleteHandler()}>Delete Album</button>
                </div>

            </div>
            <AudioList volume={volume} onTrackChange={onTrackChange} initialSongs={album.songs} />
            {addSong && <AddSong onClose={() => setAddSong(((prev) => !prev))} albumId={albumId} />}
        </div>
    )
}

export { Album }