import React, { useState, useRef, useEffect } from 'react'
import { BsFillVolumeUpFill, BsMusicNoteList } from 'react-icons/bs'
import { FaDesktop } from 'react-icons/fa'
import TrackImg from "../img/track.png"

function TrackList({ onVolumeChange, songName, artist }) {
    const [volume, setVolume] = useState(0.5);


    const progressBarValue = useRef();

    const changeVolume = (event) => {
        const newVolume = event.target.value;
        setVolume(newVolume);
        onVolumeChange(newVolume);
    }

    useEffect(() => {
        if (progressBarValue.current) {
            progressBarValue.current.style.setProperty('--player-volume', `${volume * 100}%`);
        }
    }, [volume])

    return (
        <div className='trackList'>
            <div className="top">
                <img src={TrackImg} alt="" />
                <p className='trackName'>{songName}
                    <span className='trackArtist'>{artist}</span>
                </p>
            </div>
            <div className="bottom">
                <i><BsFillVolumeUpFill /></i>
                <input type="range" min="0" max="1" step="0.01" value={volume} onChange={changeVolume} ref={progressBarValue} />
                <i><BsMusicNoteList /></i>
                <i><FaDesktop /></i>
            </div>
        </div>
    )
}

export { TrackList }