import React, {useEffect, useState} from 'react'
import "../../Styles/AlbumsView&&ArtistsView.css"
import ArtistBanner from '../../img/ArtistsBanner.png'
import Verification from '../../img/check.png'
import { BiSearchAlt } from "react-icons/bi";
import { FaHeadphones } from "react-icons/fa";
import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import {getAllArtists} from "../../Redux/Artist/Action.js";
import {BASE_API_URL} from "../../config/api.js";

function ArtistsView() {
    const dispatch = useDispatch()
    const {artist} = useSelector(store => store);
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');


    const filteredArtists = artist.artists.filter(artistItem =>
        artistItem.artistName.toLowerCase().includes(searchQuery.toLowerCase())
    );

    useEffect(() => {
        dispatch(getAllArtists())
    }, [dispatch])

    return (
        <div className='artistsView'>
            <div className="banner">
                <img src={ArtistBanner} alt="" />
                <div className="bottom">
                </div>
            </div>

            <div className="searchBox">
                <div className="search">
                    <input type="text" placeholder='Search Artist...'
                           value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)}/>
                    <i className='searchIcon'><BiSearchAlt /></i>
                </div>
                <button>Search</button>
            </div>
            <div className="artists">
                { filteredArtists.map((item) => (
                <div className="artist" key={item.id} onClick={() => navigate(`/artist/${item.id}/popular`)}>
                    <i className="listen"><FaHeadphones/></i>
                    <div className="imgPic">
                        <img src={`${BASE_API_URL}/${item.artistPic || ''}`} alt=""/>
                        <img className='check' src={Verification} alt=""/>
                    </div>
                    <p>{item?.artistName}</p>
                </div>
                ))}
            </div>
        </div>
    )
}

export { ArtistsView }