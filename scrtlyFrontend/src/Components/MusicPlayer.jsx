import React, { useState } from 'react'
import '../Styles/MusicPlayer.css'
import { FaBackward, FaForward, FaHeart, FaPause, FaPlay, FaRegHeart, FaShareAlt, FaStepBackward, FaStepForward } from 'react-icons/fa'
import { BsDownload } from 'react-icons/bs'

function MusicPlayer({ song, imgSrc }) {

    const [isLove, setLoved] = useState(false)
    const [isPlaying, setPlay] = useState(false)

    const changeLoved = () => {
        setLoved(!isLove)
    }

    const changePlayPause = () => {
        setPlay(!isPlaying)
    }

    return (
        <div className='musicPlayer'>
            <div className="songImage">
                <img src={imgSrc} alt="" />
            </div>
            <div className="songAttributes">
                <audio src={imgSrc} preload="metadata"></audio>

                <div className="top">
                    <div className="left">
                        <div className="loved" onClick={changeLoved}>
                            {isLove ? (
                                <i>
                                    <FaHeart />
                                </i>
                            ) : (
                                <i>
                                    <FaRegHeart />
                                </i>

                            )}
                        </div>
                        <i className="download"><BsDownload /></i>
                    </div>
                    <div className="middle">
                        <div className="back">
                            <i><FaStepBackward /></i>
                            <i><FaBackward /></i>
                        </div>
                        <div className="playPause" onClick={changePlayPause}>
                            {isPlaying ? (
                                <i>
                                    <FaPause />
                                </i>
                            ) : (
                                <i>
                                    <FaPlay />
                                </i>
                            )}
                        </div>
                        <div className="forward">
                            <i><FaForward /></i>
                            <i><FaStepForward /></i>
                        </div>
                    </div>
                    <div className="right"><i><FaShareAlt /></i></div>
                </div>

                <div className="bottom">
                    <div className="currentTime">00:00</div>
                    <input type="range" className='progresBar' />
                    <div className="duration">00:00</div>
                </div>
            </div>
        </div>
    )
}

export { MusicPlayer }