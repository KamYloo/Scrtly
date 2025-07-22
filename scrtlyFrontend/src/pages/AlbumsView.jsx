import React, {useState} from 'react'
import "../Styles/AlbumsView&&ArtistsView.css"
import AlbumBanner from '../assets/albumBanner.png'
import { BiSearchAlt } from "react-icons/bi";
import { FaCirclePlay } from "react-icons/fa6";
import {useNavigate} from "react-router-dom";
import Spinner from "../Components/Spinner.jsx";
import ErrorOverlay from "../Components/ErrorOverlay.jsx";
import {useGetAllAlbumsQuery} from "../Redux/services/albumApi.js";

function AlbumsView() {
    const { data, error, isLoading } = useGetAllAlbumsQuery()
    const albums = data?.content ?? []
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');

    const filteredAlbums = albums.filter(albumItem =>
        albumItem.title.toLowerCase().includes(searchQuery.toLowerCase())
    );

    if (isLoading) {
        return <Spinner />;
    }
    if (error) {
        return <ErrorOverlay error={error} />
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