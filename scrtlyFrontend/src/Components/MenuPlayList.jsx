import React from 'react'
import { FaPlus } from 'react-icons/fa'
import { BsMusicNoteList, BsTrash } from 'react-icons/bs'
import { Playlist } from './PlayList'

function MenuPlayList() {
    return (
        <div className='playListBox'>
            <div className='nameBox'>
                <p>Playlist</p>
                <i><FaPlus /></i>
            </div>
            <div className="scrollBox">

                {
                    Playlist && Playlist.map((playList)=> (
                    <div className="playList" key={playList.id}>
                        <i className='iconP'><BsMusicNoteList /></i>
                        <p>{playList.name}</p>
                        <i className='trash'><BsTrash /></i>
                    </div>
                    ))
                }

                
            </div>
        </div>
    )
}

export { MenuPlayList }