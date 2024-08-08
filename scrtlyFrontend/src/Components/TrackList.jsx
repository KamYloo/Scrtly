import React from 'react'
import { BsFillVolumeUpFill, BsMusicNoteList } from 'react-icons/bs'
import { FaDesktop } from 'react-icons/fa'
import TrackImg from "../img/track.png"

function TrackList() {
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
                <input type="range" />
                <i><BsMusicNoteList /></i>
                <i><FaDesktop /></i>
            </div>
        </div>
    )
}

export { TrackList }