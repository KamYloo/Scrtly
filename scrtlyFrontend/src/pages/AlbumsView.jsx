import React, {useEffect, useState} from 'react'
import "../Styles/AlbumsView&&ArtistsView.css"
import AlbumBanner from '../assets/albumBanner.png'
import { BiSearchAlt } from "react-icons/bi";
import { FaCirclePlay } from "react-icons/fa6";
import {useDispatch, useSelector} from "react-redux";
import {getAllAlbums} from "../Redux/Album/Action.js";
import {useNavigate} from "react-router-dom";
import Spinner from "../Components/Spinner.jsx";
import ErrorAlert from "../Components/ErrorAlert.jsx";

function AlbumsView() {
    const dispatch = useDispatch()
    const {album} = useSelector(state => state);
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');

    const filteredAlbums = album?.albums.content.filter(albumItem =>
        albumItem.title.toLowerCase().includes(searchQuery.toLowerCase())
    );

    useEffect(() => {
        dispatch(getAllAlbums())
    }, [dispatch, album.deleteAlbum])

    if (album.loading) {
        return <Spinner />;
    }
    if (album.error) {
        return <ErrorAlert message={album.error} />;
    }

    return (
        <div className='albumsView'>
            <div className="banner">
                <img src={AlbumBanner} alt="" />
                <div className="bottom">
                </div>
            </div>

            <div className="searchBox">
                <div className="search">
                    <input type="text" placeholder='Search Album...'
                           value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)}/>
                    <i className='searchIcon'><BiSearchAlt /></i>
                </div>
                <button>Search</button>
            </div>
            <div className="albums">
                {filteredAlbums.map((item) => (
                    <div className="album" key={item.id} onClick={() => navigate(`/album/${item.id}`)}>
                        <i className="play"><FaCirclePlay/></i>
                        <img src={item?.albumImage || ''} alt=""/>
                        <span>{item?.artist.pseudonym}</span>
                        <p>{item?.title}</p>
                    </div>
                ))}
            </div>
        </div>
    )
}

export {AlbumsView}