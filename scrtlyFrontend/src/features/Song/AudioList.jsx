import React, { useState } from 'react'
import { FaHeadphones, FaHeart, FaRegClock, FaRegHeart } from 'react-icons/fa'
import { FaRegSquarePlus } from 'react-icons/fa6'
import { MusicPlayer } from './MusicPlayer.jsx'
import { BsTrash } from 'react-icons/bs'
import { useDispatch, useSelector } from 'react-redux'
import { deleteSong, likeSong } from '../../Redux/Song/Action.js'
import { addSongToPlayList, deleteSongFromPlayList } from '../../Redux/PlayList/Action.js'
import toast from 'react-hot-toast'

// eslint-disable-next-line react/prop-types
function AudioList({ volume, onTrackChange, initialSongs, req_artist, isplayListSongs, playListId }) {
    const [currentManifest, setCurrentManifest] = useState(initialSongs[0]?.hlsManifestUrl)
    const [img, setImage] = useState(initialSongs[0]?.imageSong)
    const [auto, setAuto] = useState(false)
    const [addToPlayList, setAddToPlayList] = useState(null)

    const dispatch = useDispatch()
    const { playList } = useSelector((store) => store)

    const setMainSong = (manifestUrl, imgSrc, songName, songArtist) => {
        const encodedManifest = encodeURI(manifestUrl)
        const encodedImg = encodeURI(imgSrc)
        setCurrentManifest(encodedManifest)
        setImage(encodedImg)
        setAuto(true)
        onTrackChange(songName, songArtist)
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

    return (
        <div className='audioList'>
            <h2 className='title'>The List <span>{initialSongs?.length} songs</span></h2>
            <div className='songsBox'>
                {initialSongs?.map((songItem, index) => (
                    <div
                        className='songs'
                        key={songItem?.id}
                        onClick={() => setMainSong(
                            songItem?.hlsManifestUrl,
                            songItem?.imageSong,
                            songItem?.title,
                            songItem?.artist.artistName
                        )}
                    >
                        <div className='count'>#{index + 1}</div>
                        <div className='song'>
                            <div className='imgBox'>
                                <img src={songItem?.imageSong || ''} alt='cover' />
                            </div>
                            <div className='section'>
                                <p className='songName'>
                                    {songItem?.title}
                                    <span className='spanArtist'>{songItem.artist?.artistName}</span>
                                </p>
                                <div className='hits'>
                                    <p className='hit'><FaHeadphones /> 95,490,102</p>
                                    <p className='duration'><FaRegClock />{formatTime(songItem?.duration)}</p>
                                    <div className='favourite' onClick={() => likeSongHandler(songItem.id)}>
                                        {songItem?.favorite ? <FaHeart /> : <FaRegHeart />}
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
                hlsManifestUrl={currentManifest}
                imgSrc={img}
                auto={auto}
                volume={volume}
            />
        </div>
    )
}

export { AudioList }
