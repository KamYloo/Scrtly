import React, { useState } from 'react'
import { FaHeadphones, FaHeart, FaRegClock, FaRegHeart } from 'react-icons/fa'
import { FaRegSquarePlus } from "react-icons/fa6";
import { MusicPlayer } from './MusicPlayer.jsx'
import {BsTrash } from 'react-icons/bs'
import {useDispatch, useSelector} from "react-redux";
import {deleteSong, likeSong} from "../../Redux/Song/Action.js";
import {addSongToPlayList, deleteSongFromPlayList} from "../../Redux/PlayList/Action.js";
import toast from "react-hot-toast";


// eslint-disable-next-line react/prop-types
function AudioList({ volume, onTrackChange, initialSongs , req_artist, isplayListSongs, playListId}) {
    const [song, setSong] = useState(initialSongs[0]?.track)
    const [img, setImage] = useState(initialSongs[0]?.imageSong)
    const [auto, setAuto] = useState(false);
    const [addToPlayList, setAddToPlayList] = useState(false)
    const dispatch = useDispatch();
    const {playList} = useSelector(store => store);

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
            dispatch(deleteSong(songId)).then(() => {
                toast.success('Song deleted successfully.');
            }).catch(() => {
                toast.error('Failed to delete Song. Please try again.');
            });
        }
    };

    const songDeleteFromPlayListHandler = (songId) => {
        const confirmDelete = window.confirm('Are you sure you want to delete this song?');
        if (confirmDelete) {
            dispatch(deleteSongFromPlayList({playListId, songId})).then(() => {
                toast.success('Song deleted successfully.');
            }).catch(() => {
                toast.error('Failed to delete Song. Please try again.');
            });
        }
    }

    const handleAddSong = (playListId, songId) => {
        dispatch(addSongToPlayList({playListId, songId}));
    }

    const likeSongHandler = (songId) => {
        dispatch(likeSong(songId));
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
                        <div className="songs" key={song?.id} onClick={() => setMainSong(song?.track, song?.imageSong, song?.title, song?.artist.artistName)}>
                            <div className="count">#{index + 1}</div>
                            <div className="song">
                                <div className="imgBox">
                                    <img src={song?.imageSong || ''} alt="" />
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

                                        <div className="favourite" onClick={() => likeSongHandler(song.id)}>
                                            {
                                                song?.favorite ?
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
                                            <i className="addToPlayListBtn" onClick={() => handleAddToPLayListToggle(song.id)}><FaRegSquarePlus/></i>
                                            {addToPlayList === song.id && playList?.playLists?.content && (
                                                <div className="playLists">
                                                    {playList.playLists.content.map((playlist) => (
                                                        <p key={playlist.id} onClick={() => handleAddSong(playlist.id, song.id)}>
                                                            {playlist.title}
                                                        </p>
                                                    ))}
                                                </div>
                                            )}
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