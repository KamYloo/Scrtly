import React, {useState} from 'react'
import "../Styles/AlbumsView&&ArtistsView.css"
import ArtistBanner from '../assets/ArtistsBanner.png'
import Verification from '../assets/check.png'
import defaultAvatar from "../assets/user.jpg";
import { BiSearchAlt } from "react-icons/bi";
import { FaHeadphones } from "react-icons/fa";
import {useNavigate} from "react-router-dom";
import Spinner from "../Components/Spinner.jsx";
import ErrorOverlay from "../Components/ErrorOverlay.jsx";
import {useGetAllArtistsQuery} from "../Redux/services/artistApi.js";

function ArtistsView() {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');

    const {
        data: { content: artists = [] } = { content: [] },
        isLoading,
        isError,
        error,
    } = useGetAllArtistsQuery();

    const filteredArtists = artists.filter(artistItem =>
        artistItem.pseudonym.toLowerCase().includes(searchQuery.toLowerCase())
    );

    if (isLoading) {
        return <Spinner />;
    }
    if (isError) {
        return <ErrorOverlay error={error} />;
    }

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
                        <img src={item.profilePicture || defaultAvatar} alt=""/>
                        <img className='check' src={Verification} alt=""/>
                    </div>
                    <p>{item?.pseudonym}</p>
                </div>
                ))}
            </div>
        </div>
    )
}

export { ArtistsView }