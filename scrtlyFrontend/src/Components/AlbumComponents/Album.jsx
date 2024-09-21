import React, {useEffect} from 'react'
import "../../Styles/Album.css"
import AlbumPic from '../../img/album.jpg'
import { AudioList } from '../AudioList'
import {useParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {getAlbum} from "../../Redux/Album/Action.js";
import {BASE_API_URL} from "../../config/api.js";

// eslint-disable-next-line react/prop-types
function Album({ volume, onTrackChange}) {
    const {albumId} = useParams();
    const dispatch = useDispatch();
    const {album} = useSelector(store => store);

    useEffect(() => {
        dispatch(getAlbum(albumId))
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
            </div>
            <AudioList volume={volume} onTrackChange={onTrackChange}/>
        </div>
    )
}

export { Album }