import React, { useState, useRef, useEffect } from 'react'
import { BsFillVolumeUpFill, BsMusicNoteList } from 'react-icons/bs'
import { FaDesktop } from 'react-icons/fa'
import TrackImg from "../img/track.png"

function TrackList({ onVolumeChange }) {
    const [volume, setVolume] = useState(0.5);


    const progressBarValue = useRef();

    const changeVolume = (event) => {
        const newVolume = event.target.value;
        setVolume(newVolume);
        onVolumeChange(newVolume);  // Wywołujemy funkcję z LeftMenu przy zmianie głośności
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
                <p className='trackName'>Song Name
                    <span className='trackArtist'>Artist</span>
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