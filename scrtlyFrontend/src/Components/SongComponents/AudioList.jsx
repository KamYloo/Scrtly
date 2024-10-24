import React, { useState, useEffect } from 'react'
import { FaHeadphones, FaHeart, FaRegClock, FaRegHeart } from 'react-icons/fa'
import { FaRegSquarePlus } from "react-icons/fa6";
import { MusicPlayer } from './MusicPlayer.jsx'
import {BASE_API_URL} from "../../config/api.js";
import {BsTrash } from 'react-icons/bs'
import {useDispatch, useSelector} from "react-redux";
import {deleteSong} from "../../Redux/Song/Action.js";
import {addSongToPlayList, deleteSongFromPlayList} from "../../Redux/PlayList/Action.js";


// eslint-disable-next-line react/prop-types
function AudioList({ volume, onTrackChange, initialSongs , req_artist, isplayListSongs, playListId}) {
    const [songs, setSongs] = useState(initialSongs);
    const [song, setSong] = useState(songs[0]?.track)
    const [img, setImage] = useState(songs[0]?.imageSong)
    const [auto, setAuto] = useState(false);
    const [addToPlayList, setAddToPlayList] = useState(false)
    const dispatch = useDispatch();
    const {playList} = useSelector(store => store);

    useEffect(() => {
        const songs = document.querySelectorAll(".songs")

        function changeActive() {
            songs.forEach((n) => n.classList.remove("active"))
            this.classList.add("active")
        }

        songs.forEach((n) => n.addEventListener("click", changeActive))
    }, [])


    const changeFavourite = (id) => {
        songs.map((song) => {
            if (song.id === id) {
                song.favourite = !song.favourite
            }
        })
        setSongs([...songs])
    }

    const setMainSong = (songSrc, imgSrc, songName, songArtist) => {
        const encodedSongSrc = encodeURI(songSrc)
        const encodedImgSrc = encodeURI(imgSrc)
        setSong(encodedSongSrc)
        setImage(encodedImgSrc)
        setAuto(true)
        onTrackChange(songName, songArtist)
    }

    function formatTime(seconds) {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds < 10 ? '0' : ''}${remainingSeconds}`;
    }

    const handleAddToPLayListToggle = (songId) => {
        setAddToPlayList((prev) => (prev === songId ? null : songId))
    };

    const songDeleteFromAlbumHandler = (songId) => {
        const confirmDelete = window.confirm('Are you sure you want to delete this song?');
        if (confirmDelete) {
            dispatch(deleteSong(songId));
        }
    };

    const songDeleteFromPlayListHandler = (songId) => {
        const confirmDelete = window.confirm('Are you sure you want to delete this song?');
        if (confirmDelete) {
            dispatch(deleteSongFromPlayList({playListId, songId}));
        }
    }

    const handleAddSong = (playListId, songId) => {
        dispatch(addSongToPlayList({playListId, songId}));
    }

    return (
        <div className='audioList'>
            <h2 className="title">
                The List <span>{initialSongs?.length} songs</span>
            </h2>
            <div className="songsBox">
                {
                    // eslint-disable-next-line react/prop-types
                    initialSongs?.map((song, index) => (
                        <div className="songs" key={song?.id} onClick={() => setMainSong(`${BASE_API_URL}${song?.track || ''}`, `${BASE_API_URL}${song?.imageSong || ''}`, song?.title, song?.artist.artistName)}>
                            <div className="count">#{index + 1}</div>
                            <div className="song">
                                <div className="imgBox">
                                    <img src={`${BASE_API_URL}${song?.imageSong || ''}`} alt="" />
                                </div>
                                <div className="section">
                                    <p className="songName">
                                        {song?.title}
                                        <span className='spanArtist'>{song?.artist.artistName}</span>
                                    </p>
                                    <div className="hits">
                                        <p className="hit">
                                            <i><FaHeadphones /></i>
                                            95,490,102
                                        </p>

                                        <p className="duration">
                                            <i><FaRegClock /></i>
                                            {formatTime(song?.duration)}
                                        </p>

                                        <div className="favourite" onClick={() => changeFavourite(song?.id)}>
                                            {
                                                song?.favourite ?
                                                    <i><FaHeart /></i>
                                                    :
                                                    <i><FaRegHeart /></i>
                                            }
                                        </div>
                                        {(req_artist || isplayListSongs) && <i className="deleteSong" onClick={() => {
                                            if (isplayListSongs)
                                                songDeleteFromPlayListHandler(song.id)
                                            else
                                                songDeleteFromAlbumHandler(song.id)}}><BsTrash/></i>}

                                        {!isplayListSongs && <div className="addToPlayList">
                                            <i className="addToPlayListBtn"
                                               onClick={() => handleAddToPLayListToggle(song.id)}><FaRegSquarePlus/></i>
                                            {addToPlayList === song.id && (
                                                <div className="playLists">
                                                    {playList?.playLists.map((playList) => (<p key={playList.id}
                                                                                               onClick={() => handleAddSong(playList.id, song.id)}>{playList?.title}</p>))}
                                                </div>)}
                                        </div>}

                                    </div>
                                </div>
                            </div>
                        </div>
                    ))
                }
            </div>
            {<MusicPlayer song={song} imgSrc={img} autoplay={auto} volume={volume} />}
        </div>
    )
}

export { AudioList }