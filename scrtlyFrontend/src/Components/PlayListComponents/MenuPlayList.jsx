import React from 'react'
import { FaPlus } from 'react-icons/fa'
import { BsMusicNoteList, BsTrash } from 'react-icons/bs'
import {useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";

function MenuPlayList({setCreatePlayList}) {
    const { playList} = useSelector(store => store);
    const navigate = useNavigate();

    return (
        <div className='playListBox'>
            <div className='nameBox'>
                <p>Playlist</p>
                <i onClick={() =>
                    setCreatePlayList(((prev) => !prev))}
                    ><FaPlus /></i>
            </div>
            <div className="scrollBox">
                {
                    playList?.playLists.map((playList)=> (
                    <div className="playList" key={playList.id}
                         onClick={() => navigate(`/playList/${playList.id}`)}>
                        <i className='iconP'><BsMusicNoteList /></i>
                        <p>{playList?.title}</p>
                        <i className='trash'><BsTrash /></i>
                    </div>
                    ))
                }
            </div>
        </div>
    )
}

export { MenuPlayList }