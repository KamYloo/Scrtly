import React from 'react'
import "../../Styles/Album.css"
import AlbumPic from '../../img/album.jpg'
import { AudioList } from '../AudioList'

function Album({ volume, onTrackChange}) {
    return (
        <div className='albumDetail'>
            <div className="topSection">
                <img src={AlbumPic} alt="" />
                <div className="albumData">
                    <p>Album</p>
                    <h1 className='albumName'>LAST DAY</h1>
                    <p className='stats'>Pitbull • 12 Songs <span>• 2024 • 1h 24min</span> </p>
                </div>
            </div>
            <AudioList volume={volume} onTrackChange={onTrackChange}/>
        </div>
    )
}

export { Album }