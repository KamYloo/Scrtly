import React, { useState, useRef, useEffect } from 'react'
import '../../Styles/MusicPlayer.css'
import {
    FaBackward, FaForward, FaHeart, FaPause, FaPlay, FaRegHeart,
    FaShareAlt, FaStepBackward, FaStepForward
} from 'react-icons/fa'
import { BsDownload } from 'react-icons/bs'
import { useRecordPlayMutation } from "../../Redux/services/songApi.js";
import {useGaplessAudio} from "../../utils/useGaplessAudio.jsx";

export function MusicPlayer({
                                songId,
                                artistId,
                                trackSrc,
                                hlsManifestUrl,
                                imgSrc,
                                auto = false,
                                volume = 1.0,
                                onNext,
                                onPrev,
                                isLiked,
                                onLike,
                                nextTrackInfo
                            }) {
    const {
        audioARef,
        audioBRef,
        activePlayer,
        isPlaying,
        duration,
        togglePlay,
        seek,
        getCurrentAudio
    } = useGaplessAudio({
        currentManifestUrl: hlsManifestUrl,
        nextManifestUrl: nextTrackInfo?.hlsManifestUrl,
        volume,
        autoPlay: auto,
        onTrackEnd: onNext
    });

    const [currentTime, setCurrentTime] = useState(0);
    const progressRef = useRef(null);
    const rafRef = useRef(null);

    const [playRecorded, setPlayRecorded] = useState(false);
    const [recordPlay] = useRecordPlayMutation();

    const updateProgress = () => {
        const audio = getCurrentAudio();
        const bar = progressRef.current;

        if (audio && bar && !audio.paused) {
            bar.value = audio.currentTime;
            bar.style.setProperty('--seek', `${(audio.currentTime / audio.duration) * 100}%`);
            setCurrentTime(audio.currentTime);
        }
        rafRef.current = requestAnimationFrame(updateProgress);
    };

    useEffect(() => {
        if (isPlaying) {
            rafRef.current = requestAnimationFrame(updateProgress);

            if (!playRecorded && songId && artistId) {
                recordPlay({ songId, artistId }).unwrap().catch(console.error);
                setPlayRecorded(true);
            }
        } else {
            cancelAnimationFrame(rafRef.current);
        }
        return () => cancelAnimationFrame(rafRef.current);
    }, [isPlaying, songId]);

    useEffect(() => {
        setPlayRecorded(false);
    }, [songId]);

    const handleSeek = () => {
        if (progressRef.current) {
            const val = progressRef.current.value;
            seek(val);
            setCurrentTime(val);
        }
    };

    const formatTime = sec => {
        if (!sec || isNaN(sec)) return '00:00';
        const m = Math.floor(sec / 60);
        const s = Math.floor(sec % 60);
        return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`;
    };

    const downloadSong = () => {
        if (!trackSrc) return;
        const link = document.createElement('a');
        link.href = trackSrc;
        link.download = 'song.mp3';
        document.body.appendChild(link).click();
        document.body.removeChild(link);
    };

    return (
        <div className="musicPlayer">
            <div className="songImage">
                <img src={imgSrc} alt="cover art" />
            </div>
            <div className="songAttributes">
                <audio ref={audioARef} preload="metadata" style={{ display: activePlayer === 'A' ? 'block' : 'none' }} />
                <audio ref={audioBRef} preload="metadata" style={{ display: activePlayer === 'B' ? 'block' : 'none' }} />

                <div className="top">
                    <div className="left">
                        <div className="loved" onClick={onLike}>{isLiked ? <FaHeart /> : <FaRegHeart />}</div>
                        <BsDownload className="download" onClick={downloadSong} />
                    </div>
                    <div className="middle">
                        <div className="back" onClick={onPrev}>
                            <FaStepBackward /><FaBackward />
                        </div>
                        <div className="playPause" onClick={togglePlay}>
                            {isPlaying ? <FaPause /> : <FaPlay />}
                        </div>
                        <div className="forward" onClick={onNext}>
                            <FaForward /><FaStepForward />
                        </div>
                    </div>
                    <div className="right"><FaShareAlt /></div>
                </div>

                <div className="bottom">
                    <div className="currentTime">{formatTime(currentTime)}</div>
                    <input
                        type="range"
                        className="progresBar"
                        ref={progressRef}
                        min="0"
                        max={duration || 0}
                        step="0.01"
                        defaultValue="0"
                        onChange={handleSeek}
                    />
                    <div className="duration">{formatTime(duration)}</div>
                </div>
            </div>
        </div>
    );
}