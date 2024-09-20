import React, {useEffect} from 'react'
import "../../Styles/AlbumsView&&ArtistsView.css"
import AlbumBanner from '../../img/albumBanner.png'
import { BiSearchAlt } from "react-icons/bi";
import AlbumPic from '../../img/album.jpg'
import { FaCirclePlay } from "react-icons/fa6";
import {useDispatch, useSelector} from "react-redux";
import {getAllAlbums} from "../../Redux/Album/Action.js";
import {BASE_API_URL} from "../../config/api.js";

function AlbumsView() {
    const dispatch = useDispatch()
    const {album} = useSelector(store => store);

    useEffect(() => {
        dispatch(getAllAlbums())
    }, [dispatch])

    return (
        <div className='albumsView'>
            <div className="banner">
                <img src={AlbumBanner} alt="" />
                <div className="bottom">
                </div>
            </div>

            <div className="searchBox">
                <div className="search">
                    <input type="text" placeholder='Search Album...' />
                    <i className='searchIcon'><BiSearchAlt /></i>
                </div>
                <button>Search</button>
            </div>
            <div className="albums">
                { album.albums.map((item) => (
                <div className="album" key={item.id}>
                    <i className="play"><FaCirclePlay/></i>
                    <img src={`${BASE_API_URL}${item?.albumImage || ''}`} alt=""/>
                    <span>{item?.artist.artistName}</span>
                    <p>{item?.title}</p>
                </div>
                ))}
            </div>
        </div>
    )
}

export {AlbumsView}