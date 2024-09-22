import React, {useEffect, useState} from 'react'
import "../../Styles/Album.css"
import AlbumPic from '../../img/album.jpg'
import { AudioList } from '../AudioList'
import {useParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {getAlbum, getAlbumTracks} from "../../Redux/Album/Action.js";
import {BASE_API_URL} from "../../config/api.js";
import {AddSong} from "../SongComponents/addSong.jsx";

// eslint-disable-next-line react/prop-types
function Album({ volume, onTrackChange}) {
    const {albumId} = useParams();
    const dispatch = useDispatch();
    const {album} = useSelector(store => store);
    const [addSong, setAddSong] = useState(false)

    useEffect(() => {
        dispatch(getAlbum(albumId))
    }, [albumId]);

    useEffect(() => {
        dispatch(getAlbumTracks(albumId))
    }, [albumId]);

    return (
        <div className='albumDetail'>
            <div className="topSection">
                <img src={`${BASE_API_URL}${album.findAlbum?.albumImage || ''}`} alt="" />
                <div className="albumData">
                    <p>Album</p>
                    <h1 className='albumName'>{album.findAlbum?.title}</h1>
                    <p className='stats'>{album.findAlbum?.artist.artistName} • 12 Songs <span>• {album.findAlbum?.releaseDate} • 1h 24min</span> </p>
                </div>
                <button className="addSongBtn" onClick={() =>
                    setAddSong(((prev) => !prev))}>Add Song</button>
            </div>
            <AudioList volume={volume} onTrackChange={onTrackChange}/>
            {addSong && <AddSong onClose={() => setAddSong(((prev) => !prev))} albumId={albumId} />}
        </div>
    )
}

export { Album }