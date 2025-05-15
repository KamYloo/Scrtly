import React, {useEffect} from 'react'
import { FaPlus } from 'react-icons/fa'
import { BsMusicNoteList, BsTrash } from 'react-icons/bs'
import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import {deletePlayList, getUserPlayLists} from "../../Redux/PlayList/Action.js";
import toast from "react-hot-toast";
import Spinner from "../../Components/Spinner.jsx";
import ErrorAlert from "../../Components/ErrorAlert.jsx";

function MenuPlayList({setCreatePlayList}) {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const { playList } = useSelector(state => state);

    const deletePlayListHandler = (playListId) => {
        const confirmDelete = window.confirm('Are you sure you want to delete this playList?');
        if (confirmDelete) {
            dispatch(deletePlayList(playListId)).then(() => {
                toast.success('Playlist deleted successfully.');
                navigate('/home')
            }).catch(() => {
                toast.error('Failed to delete playlist. Please try again.');
            });
        }
    }

    useEffect(() => {
        if (localStorage.getItem("user")) {
            dispatch(getUserPlayLists());
        }
    }, [dispatch]);

    if (playList.loading) {
        return (
               <Spinner size={100}/>)
    }

    if (playList.error) {
        return (
            <div className="playListBox">
                <p>Błąd: {playList.error}</p>
            </div>
        );
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
                    playList?.playLists.content.map((playList)=> (
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