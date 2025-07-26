import React, { } from 'react'
import "../Styles/home.css"
import { useDispatch } from "react-redux";
import Spinner from "../Components/Spinner.jsx";
import ErrorOverlay from "../Components/ErrorOverlay.jsx";
import {useNavigate } from "react-router-dom";
import { likeSong } from "../Redux/Song/Action.js";
import TrendingSong from "../features/Home/TrendingSong.jsx";
import AlbumsList from "../features/Home/AlbumsList.jsx";
import TopSongsList from "../features/Home/TopSongsList.jsx";
import FavouriteArtistsList from "../features/Home/FavouriteArtistsList.jsx";
import {useGetTopAlbumsQuery, useGetTopArtistsQuery, useGetTopSongsQuery} from "../Redux/services/recommendationApi.js";
function Home() {
    const {
        data: artists = [],
        isLoading: loadingArtists,
        isError: errorArtists,
    } = useGetTopArtistsQuery({ window: "day", n: 8 });

    const {
        data: albums = [],
        isLoading: loadingAlbums,
        isError: errorAlbums,
    } = useGetTopAlbumsQuery({ window: "day", n: 6 });

    const {
        data: songs = [],
        isLoading: loadingSongs,
        isError: errorSongs,
    } = useGetTopSongsQuery({ timeWindow  : "day", n: 6 });
    const isLoading = loadingArtists || loadingAlbums || loadingSongs;
    const isError = errorArtists || errorAlbums || errorSongs;

    const dispatch = useDispatch();
    const navigate = useNavigate();
    const trending = songs[0];
    const topList = songs.slice(1, 6);

    const formatTime = sec => {
        const m = Math.floor(sec / 60)
        const s = Math.floor(sec % 60)
        return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
    }

    if (isLoading) return <Spinner />
    if (isError) return <ErrorOverlay error={error} />

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