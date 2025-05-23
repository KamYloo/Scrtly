import React, { useState, useRef, useEffect } from 'react'
import '../../Styles/MusicPlayer.css'
import {
    FaBackward,
    FaForward,
    FaHeart,
    FaPause,
    FaPlay,
    FaRegHeart,
    FaShareAlt,
    FaStepBackward,
    FaStepForward
} from 'react-icons/fa'
import { BsDownload } from 'react-icons/bs'
import Hls from 'hls.js'

export function MusicPlayer({
                                hlsManifestUrl,
                                imgSrc,
                                auto = false,
                                volume = 1.0,
                                initialBufferSegments = 3,
                                maxBufferSeconds = 15
                            }) {
    const [isLoved, setLoved] = useState(false)
    const [isPlaying, setPlaying] = useState(false)
    const [duration, setDuration] = useState(0)
    const [currentTime, setCurrentTime] = useState(0)

    const audioRef = useRef(null)
    const progressRef = useRef(null)
    const rafRef = useRef(null)
    const hlsRef = useRef(null)
    const bufferTimeoutRef = useRef(null)
    const initialBufferedRef = useRef(false)

    useEffect(() => {
        const audio = audioRef.current
        if (!audio || !hlsManifestUrl) return

        if (Hls.isSupported()) {
            const hls = new Hls({ maxBufferLength: maxBufferSeconds, autoStartLoad: false })
            hlsRef.current = hls
            hls.attachMedia(audio)
            hls.loadSource(hlsManifestUrl)

            const onLoadedMeta = () => {
                setDuration(audio.duration)
                if (progressRef.current) progressRef.current.max = audio.duration
            }
            audio.addEventListener('loadedmetadata', onLoadedMeta)

            hls.on(Hls.Events.FRAG_APPENDED, (_, data) => {
                if (!initialBufferedRef.current && data.frag.sn >= initialBufferSegments - 1) {
                    initialBufferedRef.current = true
                    hls.stopLoad()
                    if (auto) startPlayback()
                }
            })

            return () => {
                audio.removeEventListener('loadedmetadata', onLoadedMeta)
                hls.destroy()
                clearTimeout(bufferTimeoutRef.current)
                cancelAnimationFrame(rafRef.current)
            }
        }

        if (audio.canPlayType('application/vnd.apple.mpegurl')) {
            audio.src = hlsManifestUrl
            const onNativeMeta = () => {
                setDuration(audio.duration)
                if (progressRef.current) progressRef.current.max = audio.duration
                if (auto) startPlayback()
            }
            audio.addEventListener('loadedmetadata', onNativeMeta)
            return () => audio.removeEventListener('loadedmetadata', onNativeMeta)
        }
    }, [hlsManifestUrl, auto, initialBufferSegments, maxBufferSeconds])

    useEffect(() => {
        if (audioRef.current) audioRef.current.volume = volume
    }, [volume])

    const startPlayback = () => {
        const audio = audioRef.current
        if (!audio) return
        audio.play()
        setPlaying(true)
        rafRef.current = requestAnimationFrame(updateWhilePlaying)
    }

    const togglePlay = () => {
        const audio = audioRef.current
        const hls = hlsRef.current
        if (!audio) return

        if (isPlaying) {
            audio.pause()
            setPlaying(false)
            cancelAnimationFrame(rafRef.current)
            if (hls) hls.stopLoad()
        } else {
            if (hls && !initialBufferedRef.current) {
                hls.startLoad(0)
            } else if (hls) {
                hls.startLoad(-1)
            }
            startPlayback()
        }
    }

    const scheduleBufferCheck = () => {
        bufferTimeoutRef.current = setTimeout(() => {
            const audio = audioRef.current
            const hls = hlsRef.current
            if (audio && hls && isPlaying) {
                const end = audio.buffered.length
                          ? audio.buffered.end(audio.buffered.length - 1)
                    : 0
                if (end - audio.currentTime < 5) {
                    hls.startLoad(-1)
                }
            }
        }, 1000)
    }

    const updateWhilePlaying = () => {
        const audio = audioRef.current
        const bar = progressRef.current
        if (audio && bar) {
            bar.value = audio.currentTime
            bar.style.setProperty(
                '--seek',
                `${(audio.currentTime / audio.duration) * 100}%`
            )
            setCurrentTime(audio.currentTime)
            scheduleBufferCheck()
        }
        rafRef.current = requestAnimationFrame(updateWhilePlaying)
    }

    const onSeek = () => {
        const audio = audioRef.current
        const bar = progressRef.current
        if (audio && bar) {
            audio.currentTime = bar.value
            setCurrentTime(bar.value)
        }
    }

    const formatTime = sec => {
        const m = Math.floor(sec / 60)
        const s = Math.floor(sec % 60)
        return `${m.toString().padStart(2, '0')}:${s
            .toString()
            .padStart(2, '0')}`
    }

    return (
        <div className="musicPlayer">
            <div className="songImage">
                <img src={imgSrc} alt="cover art" />
            </div>
            <div className="songAttributes">
                <audio ref={audioRef} preload="metadata" />
                <div className="top">
                    <div className="left">
                        <div className="loved" onClick={() => setLoved(l => !l)}>
                            {isLoved ? <FaHeart /> : <FaRegHeart />}
                        </div>
                        <BsDownload className="download" />
                    </div>
                    <div className="middle">
                        <div className="back">
                            <FaStepBackward />
                            <FaBackward />
                        </div>
                        <div className="playPause" onClick={togglePlay}>
                            {isPlaying ? <FaPause /> : <FaPlay />}
                        </div>
                        <div className="forward">
                            <FaForward />
                            <FaStepForward />
                        </div>
                    </div>
                    <div className="right">
                        <FaShareAlt />
                    </div>
                </div>
                <div className="bottom">
                    <div className="currentTime">{formatTime(currentTime)}</div>
                    <input
                        type="range"
                        className="progresBar"
                        ref={progressRef}
                        min="0"
                        max={duration}
                        step="0.01"
                        value={currentTime}
                        onChange={onSeek}
                    />
                    <div className="duration">
                        {duration ? formatTime(duration) : '00:00'}
                    </div>
                </div>
            </div>
        </div>
    )
}
