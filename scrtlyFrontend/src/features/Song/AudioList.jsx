import React, {useCallback, useEffect, useRef, useState} from 'react'
import { FaHeadphones, FaHeart, FaRegClock, FaRegHeart } from 'react-icons/fa'
import { FaRegSquarePlus } from 'react-icons/fa6'
import { MusicPlayer } from './MusicPlayer.jsx'
import { BsTrash } from 'react-icons/bs'
import toast from 'react-hot-toast'
import {useDeleteSongMutation, useLikeSongMutation} from "../../Redux/services/songApi.js";
import {
    useDeleteSongFromPlaylistMutation, useGetPlaylistTracksQuery,
    useGetUserPlaylistsQuery,
    useUploadSongToPlaylistMutation
} from "../../Redux/services/playlistApi.js";
import throttle from "lodash.throttle";
import {useGetArtistTracksQuery} from "../../Redux/services/artistApi.js";

// eslint-disable-next-line react/prop-types
function AudioList({ volume, onTrackChange, initialSongs, req_artist, isplayListSongs, playListId,  artistId, initialSongId }) {
    const [currentIndex, setCurrentIndex] = useState(0)
    const [auto, setAuto] = useState(false)
    const [addToPlayList, setAddToPlayList] = useState(null)
    const [deleteSong] = useDeleteSongMutation()
    const [likeSong]   = useLikeSongMutation()
    const [uploadSong] = useUploadSongToPlaylistMutation()
    const [removeFromPlaylist] = useDeleteSongFromPlaylistMutation()
    const [pagePL, setPagePL] = useState(0)
    const sizePL = 10
    const [allPlaylists, setAllPlaylists] = useState([])
    const shouldFetchPL = addToPlayList !== null
    const { data: plData, isFetching: plFetching } = useGetUserPlaylistsQuery(
        { page: pagePL, size: sizePL },
        { skip: !shouldFetchPL })

    const dropdownRef = useRef()

    const [favorites, setFavorites] = useState(new Set())

    useEffect(() => {
        if (plData?.content) {
            setAllPlaylists(prev =>
                pagePL === 0
                    ? plData.content
                    : [...prev, ...plData.content.filter(p => !prev.find(x => x.id === p.id))]
            )
        }
    }, [plData?.content, pagePL])

    const onScrollPL = useCallback(
        throttle(() => {
            const el = dropdownRef.current
            if (!el || plFetching || (plData?.content.length ?? 0) < sizePL) return
            if (el.scrollHeight - el.scrollTop - el.clientHeight < 50) {
                setPagePL(p => p + 1)
            }
        }, 200),
        [plFetching, plData]
    )
    useEffect(() => {
        const el = dropdownRef.current
        if (!el) return
        el.addEventListener('scroll', onScrollPL)
        return () => el.removeEventListener('scroll', onScrollPL)
    }, [onScrollPL])


    const [pageTR, setPageTR] = useState(0)
    const sizeTR = 10
    const [allTracks, setAllTracks] = useState([])
    const shouldFetchTR = isplayListSongs
    const { data: trData, isFetching: trFetching, isLoading: trLoading } = useGetPlaylistTracksQuery(
        { playListId, page: pageTR, size: sizeTR },
        { skip: !shouldFetchTR }
    )
    const tracksRef = useRef()

    useEffect(() => {
        if (trData) {
            setAllTracks(prev =>
                pageTR === 0
                    ? trData
                    : [...prev, ...trData.filter(s => !prev.find(x => x.id === s.id))]
            )
        }
    }, [trData, pageTR])

    const onScrollTR = useCallback(
        throttle(() => {
            const el = tracksRef.current
            if (!el || trFetching || (trData?.length ?? 0) < sizeTR) return
            if (el.scrollHeight - el.scrollTop - el.clientHeight < 50) {
                setPageTR(p => p + 1)
            }
        }, 200),
        [trFetching, trData]
    )
    useEffect(() => {
        const el = tracksRef.current
        if (!el) return
        el.addEventListener('scroll', onScrollTR)
        return () => el.removeEventListener('scroll', onScrollTR)
    }, [onScrollTR])

    const [pageAR, setPageAR] = useState(0)
    const sizeAR = 9
    const [allArtistTracks, setAllArtistTracks] = useState([])
    const shouldFetchAR = !!artistId
    const { data: arData, isFetching: arFetching, isLoading: arLoading } = useGetArtistTracksQuery(
        { artistId, page: pageAR, size: sizeAR },
        { skip: !shouldFetchAR }
    )

    const artistRef = useRef()
    useEffect(() => {
        if (Array.isArray(arData)) {
            setAllArtistTracks(prev =>
                pageAR === 0
                    ? arData
                    : [
                        ...prev,
                        ...arData.filter(s => !prev.find(x => x.id === s.id))
                    ]
            );
        }
    }, [arData, pageAR]);

    const onScrollAR = useCallback(
        throttle(() => {
            const el = artistRef.current
            if (!el || arFetching || (arData?.length ?? 0) < sizeAR) return;
            if (el.scrollHeight - el.scrollTop - el.clientHeight < 50) {
                setPageAR(p => p + 1)
            }
        }, 200),
        [arFetching, arData]
    )
    useEffect(() => {
        const el = artistRef.current
        if (!el) return
        el.addEventListener('scroll', onScrollAR)
        return () => el.removeEventListener('scroll', onScrollAR)
    }, [onScrollAR])

    let songs = initialSongs
    let containerProps = {}
    if (isplayListSongs) {
        songs = allTracks
        containerProps = { ref: tracksRef }
    } else if (artistId) {
        songs = allArtistTracks
        containerProps = { ref: artistRef}
    }

    useEffect(() => {
        setFavorites(new Set(songs.filter(s => s.favorite).map(s => s.id)))
    }, [songs])
    useEffect(() => {
        if (initialSongId && songs.length) {
            const idx = songs.findIndex(s => s.id === initialSongId)
            if (idx !== -1) {
                setCurrentIndex(idx)
                setAuto(true)
            }
        }
    }, [initialSongId, songs])


    const setMainSong = idx => {
        setCurrentIndex(idx)
        onTrackChange(songs[idx].title, songs[idx].artist.pseudonym)
        setAuto(true)
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

    const songDeleteFromPlayListHandler = async (id) => {
        if (!window.confirm('Are you sure you want to delete this song from playlist?')) return;
        try {
            await removeFromPlaylist({ playListId, songId: id }).unwrap()
            toast.success('Song removed from playlist.')
        } catch (err) {
            toast.error(err.data.businessErrornDescription);
        }
    }

    const handleAddSong = async (playListId, songId) => {
        try {
            await uploadSong({ playListId, songId }).unwrap()
            toast.success('Added to playlist!')
            setAddToPlayList(null)
        } catch (err) {
            toast.error(err.data.businessErrornDescription);

        }
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

    return (
        <div className='audioList'>
            <h2 className='title'>The List <span>{songs?.length} songs</span></h2>
            <div className='songsBox' {...containerProps}>
                {songs?.map((songItem, index) => (
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
                                            {addToPlayList === songItem.id && allPlaylists && (
                                                <div className='playLists' ref={dropdownRef}>
                                                    {allPlaylists.map((playlist) => (
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
                songId={songs[currentIndex]?.id}
                artistId={songs[currentIndex]?.artist.id}
                trackSrc={songs[currentIndex]?.track}
                hlsManifestUrl={songs[currentIndex]?.hlsManifestUrl}
                imgSrc={encodeURI(songs[currentIndex]?.imageSong||'')}
                auto={auto}
                volume={volume}
                onNext={()=>setMainSong((currentIndex+1)%songs.length)}
                onPrev={()=>setMainSong((currentIndex-1+songs.length)%songs.length)}
                isLiked={favorites.has(songs[currentIndex]?.id)}
                onLike={()=>likeSongHandler(songs[currentIndex]?.id)}
            />
        </div>
    )
}

export { AudioList }
