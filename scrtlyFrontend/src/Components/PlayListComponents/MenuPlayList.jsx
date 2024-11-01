import React from 'react'
import { FaPlus } from 'react-icons/fa'
import { BsMusicNoteList, BsTrash } from 'react-icons/bs'
import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import {deletePlayList} from "../../Redux/PlayList/Action.js";

function MenuPlayList({setCreatePlayList}) {
    const dispatch = useDispatch();
    const { playList} = useSelector(store => store);
    const navigate = useNavigate();

    const deletePlayListHandler = (playListId) => {
        const confirmDelete = window.confirm('Are you sure you want to delete this playList?');
        if (confirmDelete) {
            dispatch(deletePlayList(playListId))
            navigate('/home')
        }
    }

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
                    <div className="playList" key={playList.id}>
                        <i className='iconP'><BsMusicNoteList /></i>
                        <p onClick={() => navigate(`/playList/${playList.id}`)}>{playList?.title}</p>
                        <i className='trash' onClick={() => deletePlayListHandler(playList.id)}><BsTrash /></i>
                    </div>
                    ))
                }
            </div>
        </div>
    )
}

export { MenuPlayList }