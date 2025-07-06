import React, { useEffect } from 'react'
import "../Styles/home.css"
import { useDispatch, useSelector } from "react-redux";
import Spinner from "../Components/Spinner.jsx";
import ErrorOverlay from "../Components/ErrorOverlay.jsx";
import { getTopAlbumsAction, getTopArtistsAction, getTopSongsAction } from "../Redux/RecommendationService/Action.js";
import {useNavigate } from "react-router-dom";
import { likeSong } from "../Redux/Song/Action.js";
import TrendingSong from "../features/Home/TrendingSong.jsx";
import AlbumsList from "../features/Home/AlbumsList.jsx";
import TopSongsList from "../features/Home/TopSongsList.jsx";
import FavouriteArtistsList from "../features/Home/FavouriteArtistsList.jsx";
function Home() {
    const { loading, error, albums, artists, songs } = useSelector(state => state.recommendationService);
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const trending = songs[0];
    const topList = songs.slice(1, 6);

    const formatTime = sec => {
        const m = Math.floor(sec / 60)
        const s = Math.floor(sec % 60)
        return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
    }

    useEffect(() => {
        dispatch(getTopArtistsAction('day', 8))
        dispatch(getTopAlbumsAction('day', 6))
        dispatch(getTopSongsAction('day', 6))
    }, [dispatch])

    if (loading) return <Spinner />
    if (error) return <ErrorOverlay message={error} />

    return (
        <div className='homeBox'>
            <TrendingSong
                trending={trending}
                onLike={() => dispatch(likeSong(trending.id))}
                onListen={() => navigate(`/artist/${trending.artist.id}/popular`, { state: { playSongId: trending.id } })}
            />
            <div className="onTime">
                <AlbumsList albums={albums} onAlbumClick={id => navigate(`/album/${id}`)} />
                <TopSongsList songs={topList} formatTime={formatTime} onSongPlay={song => navigate(`/artist/${song.artist.id}/popular`, { state: { playSongId: song.id } })} />
            </div>
            <FavouriteArtistsList artists={artists} onArtistClick={id => navigate(`/artist/${id}/popular`)} />
        </div>
    )
}

export { Home }