import React, {useCallback, useEffect, useState} from 'react'
import { FaHeadphones, FaHeart, FaRegClock, FaRegHeart } from 'react-icons/fa'
import { FaRegSquarePlus } from 'react-icons/fa6'
import { MusicPlayer } from './MusicPlayer.jsx'
import { BsTrash } from 'react-icons/bs'
import { useDispatch, useSelector } from 'react-redux'
import { deleteSong, likeSong } from '../../Redux/Song/Action.js'
import { addSongToPlayList, deleteSongFromPlayList } from '../../Redux/PlayList/Action.js'
import toast from 'react-hot-toast'

// eslint-disable-next-line react/prop-types
function AudioList({ volume, onTrackChange, initialSongs, req_artist, isplayListSongs, playListId, initialSongId }) {
    const [currentIndex, setCurrentIndex] = useState(0)
    const [auto, setAuto] = useState(false)
    const [addToPlayList, setAddToPlayList] = useState(null)
    const dispatch = useDispatch()
    const { playList } = useSelector((store) => store)

    const currentSong = initialSongs[currentIndex] || {}

    useEffect(() => {
        if (initialSongId && initialSongs && initialSongs.length > 0) {
            // eslint-disable-next-line react/prop-types
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
            initialSongs[index]?.artist?.artistName
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

    const songDeleteFromAlbumHandler = (songId) => {
        if (window.confirm('Are you sure you want to delete this song?')) {
            dispatch(deleteSong(songId))
                .then(() => toast.success('Song deleted successfully.'))
                .catch(() => toast.error('Failed to delete song. Please try again.'))
        }
    }

    const songDeleteFromPlayListHandler = (songId) => {
        if (window.confirm('Are you sure you want to delete this song from playlist?')) {
            dispatch(deleteSongFromPlayList({ playListId, songId }))
                .then(() => toast.success('Song removed from playlist.'))
                .catch(() => toast.error('Failed to remove song. Please try again.'))
        }
    }

    const handleAddSong = (playListId, songId) => {
        dispatch(addSongToPlayList({ playListId, songId }))
    }

    const likeSongHandler = (songId) => {
        dispatch(likeSong(songId))
    }

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
                        className={`songs ${index === currentIndex ? 'playing' : ''}`}
                        key={songItem?.id}
                        onClick={() => setMainSong(index)}
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
                                    <div className='favourite' onClick={() => likeSongHandler(songItem.id)}>
                                        {songItem?.favorite ? (
                                            <FaHeart />
                                        ) : (
                                            <FaRegHeart />
                                        )}
                                    </div>
                                    {(req_artist || isplayListSongs) && (
                                        <i className='deleteSong' onClick={() => {
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
                isLiked={currentSong?.favorite}
                onLike={() => likeSongHandler(currentSong?.id)}
            />
        </div>
    )
}

export { AudioList }
