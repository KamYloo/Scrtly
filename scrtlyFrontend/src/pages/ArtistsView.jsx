import React, {useCallback, useEffect, useRef, useState} from 'react'
import "../Styles/AlbumsView&&ArtistsView.css"
import ArtistBanner from '../assets/artistsBanner.png'
import Verification from '../assets/check.png'
import defaultAvatar from "../assets/user.jpg";
import { BiSearchAlt } from "react-icons/bi";
import { FaHeadphones } from "react-icons/fa";
import {useNavigate} from "react-router-dom";
import Spinner from "../Components/Spinner.jsx";
import ErrorOverlay from "../Components/ErrorOverlay.jsx";
import {useGetAllArtistsQuery} from "../Redux/services/artistApi.js";
import throttle from "lodash.throttle";

function ArtistsView() {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');

    const [page, setPage] = useState(0);
    const size = 9;
    const [allArtists, setAllArtists] = useState([]);

    const { data = [], isLoading, isError, error, isFetching } =
        useGetAllArtistsQuery({ page, size });

    const listRef = useRef();

    useEffect(() => {
        if (data) {
            setAllArtists(prev =>
                page === 0
                    ? data
                    : [...prev, ...data.filter(a => !prev.find(x => x.id === a.id))]
            );
        }
    }, [data, page]);

    const onScroll = useCallback(
        throttle(() => {
            const el = listRef.current;
            if (!el || isFetching || data.length < size) return;
            if (el.scrollHeight - el.scrollTop - el.clientHeight < 100) {
                setPage(p => p + 1);
            }
        }, 200),
        [isFetching, data]
    );

    useEffect(() => {
        const el = listRef.current;
        if (!el) return;
        el.addEventListener("scroll", onScroll);
        return () => el.removeEventListener("scroll", onScroll);
    }, [onScroll]);

    if (isLoading) {
        return <Spinner />;
    }
    if (isError) {
        return <ErrorOverlay error={error} />;
    }

    const filtered = allArtists.filter(a =>
        a.pseudonym.toLowerCase().includes(searchQuery.toLowerCase())
    );

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
            <div className="artists" ref={listRef}>
                { filtered.map((item) => (
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