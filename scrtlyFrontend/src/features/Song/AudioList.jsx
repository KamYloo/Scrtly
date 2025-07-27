import React, {useCallback, useEffect, useState} from 'react'
import { FaHeadphones, FaHeart, FaRegClock, FaRegHeart } from 'react-icons/fa'
import { FaRegSquarePlus } from 'react-icons/fa6'
import { MusicPlayer } from './MusicPlayer.jsx'
import { BsTrash } from 'react-icons/bs'
import { useDispatch, useSelector } from 'react-redux'
import { addSongToPlayList, deleteSongFromPlayList } from '../../Redux/PlayList/Action.js'
import toast from 'react-hot-toast'
import {useDeleteSongMutation, useLikeSongMutation} from "../../Redux/services/songApi.js";

// eslint-disable-next-line react/prop-types
function AudioList({ volume, onTrackChange, initialSongs, req_artist, isplayListSongs, playListId, initialSongId }) {
    const [currentIndex, setCurrentIndex] = useState(0)
    const [auto, setAuto] = useState(false)
    const [addToPlayList, setAddToPlayList] = useState(null)
    const dispatch = useDispatch()
    const [deleteSong] = useDeleteSongMutation()
    const [likeSong]   = useLikeSongMutation()
    const { playList } = useSelector((store) => store)

    const currentSong = initialSongs[currentIndex] || {}

    const [favorites, setFavorites] = useState(new Set())

    useEffect(() => {
        setFavorites(new Set(
            initialSongs
                .filter(s => s.favorite)
                .map(s => s.id)
        ))
    }, [initialSongs])



    useEffect(() => {
        if (initialSongId && initialSongs && initialSongs.length > 0) {
            const idx = initialSongs.findIndex(song => song.id === initialSongId)
            if (idx !== -1) {
                setCurrentIndex(idx)
                setAuto(true)
            }
        }
    }, [initialSongId, initialSongs])

    const setMainSong = (index) => {
        setCurrentIndex(index)
        setAuto(true)
        onTrackChange(
            initialSongs[index]?.title,
            initialSongs[index]?.artist?.pseudonym
        )
    }

    function formatTime(seconds) {
        const minutes = Math.floor(seconds / 60)
        const remainingSeconds = seconds % 60
        return `${minutes}:${remainingSeconds < 10 ? '0' : ''}${remainingSeconds}`
    }

    const handleAddToPlayListToggle = (songId) => {
        setAddToPlayList((prev) => (prev === songId ? null : songId))
    }

    const songDeleteFromAlbumHandler = async (songId) => {
        if (!window.confirm('Are you sure you want to delete this song?')) return;
        try {
            await deleteSong(songId).unwrap()
            toast.success('Song deleted successfully.')
        } catch (err) {
            toast.error(err.data.businessErrornDescription);
        }
    }

    const songDeleteFromPlayListHandler = async (songId) => {
        if (!window.confirm('Are you sure you want to delete this song from playlist?')) return;
        try {
            await deleteSong(songId).unwrap()
            toast.success('Song removed from playlist.')
        } catch (err) {
            toast.error(err.data.businessErrornDescription);
        }
    }

    const handleAddSong = (playListId, songId) => {
        dispatch(addSongToPlayList({ playListId, songId }))
    }

    const likeSongHandler = async (songId) => {
        const newFavorites = new Set(favorites);
        if (favorites.has(songId)) {
            newFavorites.delete(songId);
        } else {
            newFavorites.add(songId);
        }
        setFavorites(newFavorites);

        try {
            await likeSong(songId).unwrap();
        } catch {
            setFavorites(favorites);
        }
    };


    const handleNextSong = useCallback(() => {
        const nextIndex = (currentIndex + 1) % initialSongs.length
        setMainSong(nextIndex)
    }, [currentIndex, initialSongs])

    const handlePrevSong = useCallback(() => {
        const prevIndex = (currentIndex - 1 + initialSongs.length) % initialSongs.length
        setMainSong(prevIndex)
    }, [currentIndex, initialSongs])

    return (
        <div className='audioList'>
            <h2 className='title'>The List <span>{initialSongs?.length} songs</span></h2>
            <div className='songsBox'>
                {initialSongs?.map((songItem, index) => (
                    <div
                        className={
                            `songs ${index === currentIndex ? 'playing' : ''}` +
                            ( !songItem.hlsManifestUrl ? ' disabled' : '' )
                        }
                        key={songItem?.id}
                        onClick={() => {
                            if (!songItem.hlsManifestUrl) return;
                            setMainSong(index);
                        }}
                    >
                        <div className='count'>#{index + 1}</div>
                        <div className='song'>
                            <div className='imgBox'>
                                <img src={songItem?.imageSong || ''} alt='cover' />
                            </div>
                            <div className='section'>
                                <p className='songName'>
                                    {songItem?.title}
                                    <span className='spanArtist'>{songItem.artist?.pseudonym}</span>
                                </p>
                                <div className='hits'>
                                    <p className='hit'>
                                        <FaHeadphones /> {songItem?.playCount}
                                    </p>
                                    <p className='duration'>
                                        <span className="clock-icon">
                                            <FaRegClock />
                                        </span>
                                        <span className="time-value">
                                            {formatTime(songItem?.duration)}
                                        </span>
                                    </p>
                                    <div className='favourite' onClick={(e) => {
                                        e.stopPropagation();
                                        likeSongHandler(songItem.id);
                                    }}>
                                        {favorites.has(songItem.id) ? (
                                            <FaHeart />
                                        ) : (
                                            <FaRegHeart />
                                        )}
                                    </div>
                                    {(req_artist || isplayListSongs) && (
                                        <i className='deleteSong' onClick={(e) => {
                                            e.stopPropagation();
                                            isplayListSongs
                                                ? songDeleteFromPlayListHandler(songItem.id)
                                                : songDeleteFromAlbumHandler(songItem.id)
                                        }}><BsTrash /></i>
                                    )}
                                    {!isplayListSongs && (
                                        <div className='addToPlayList'>
                                            <i className='addToPlayListBtn' onClick={() => handleAddToPlayListToggle(songItem.id)}>
                                                <FaRegSquarePlus />
                                            </i>
                                            {addToPlayList === songItem.id && playList?.playLists?.content && (
                                                <div className='playLists'>
                                                    {playList.playLists.content.map((playlist) => (
                                                        <p key={playlist.id} onClick={() => handleAddSong(playlist.id, songItem.id)}>
                                                            {playlist.title}
                                                        </p>
                                                    ))}
                                                </div>
                                            )}
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
            <MusicPlayer
                songId={currentSong?.id}
                trackSrc={currentSong?.track}
                hlsManifestUrl={currentSong?.hlsManifestUrl}
                imgSrc={encodeURI(currentSong?.imageSong || '')}
                auto={auto}
                volume={volume}
                onNext={handleNextSong}
                onPrev={handlePrevSong}
                isLiked={favorites.has(currentSong.id)}
                onLike={() => likeSongHandler(currentSong?.id)}
            />
        </div>
    )
}

export { AudioList }
