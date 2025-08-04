import React, {useCallback, useEffect, useRef, useState} from 'react'
import "../Styles/AlbumsView&&ArtistsView.css"
import AlbumBanner from '../assets/albumBanner.png'
import { BiSearchAlt } from "react-icons/bi";
import { FaCirclePlay } from "react-icons/fa6";
import {useNavigate} from "react-router-dom";
import Spinner from "../Components/Spinner.jsx";
import ErrorOverlay from "../Components/ErrorOverlay.jsx";
import {useGetAllAlbumsQuery} from "../Redux/services/albumApi.js";
import throttle from "lodash.throttle";

function AlbumsView() {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState('');

    const [page, setPage] = useState(0);
    const size = 9;
    const [allAlbums, setAllAlbums] = useState([]);

    const {
        data = [],
        isLoading,
        isError,
        error,
        isFetching,
    } = useGetAllAlbumsQuery({ page, size });

    const listRef = useRef();

    useEffect(() => {
        if (!Array.isArray(data) || data.length === 0) return;

        if (page === 0) {
            setAllAlbums(data);
            return;
        }

        setAllAlbums(prev => {
            const newOnes = data.filter(a => !prev.some(x => x.id === a.id));
            if (newOnes.length === 0) return prev;
            return [...prev, ...newOnes];
        });
    }, [data, page]);

    const onScroll = useCallback(
        throttle(() => {
            const el = listRef.current;
            if (!el || isFetching || data.length < size) return;
            if (el.scrollHeight - el.scrollTop - el.clientHeight < 100) {
                setPage(p => p + 1);
            }
        }, 200),
        [isFetching, data.length]
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
        return <ErrorOverlay error={error} />
    }

    const filtered = allAlbums.filter(a =>
        a.title.toLowerCase().includes(searchQuery.toLowerCase())
    );

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
            <div className="albums" ref={listRef}>
                {filtered.map((item) => (
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