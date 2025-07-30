import React, {useCallback, useEffect, useRef, useState} from 'react'
import { FaPlus } from 'react-icons/fa'
import { BsMusicNoteList, BsTrash } from 'react-icons/bs'
import {useNavigate} from "react-router-dom";
import toast from "react-hot-toast";
import Spinner from "../../Components/Spinner.jsx";
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";
import {useDeletePlaylistMutation, useGetUserPlaylistsQuery} from "../../Redux/services/playlistApi.js";
import throttle from "lodash.throttle";

function MenuPlayList({setCreatePlayList, closeModals = () => {}}) {
    const navigate = useNavigate();
    const { data: reqUser } = useGetCurrentUserQuery(null, {
        skip: !localStorage.getItem('isLoggedIn'),
    });

    const [page, setPage] = useState(0);
    const size = 10;
    const [allPlaylists, setAllPlaylists] = useState([]);

    const {
        data,
        isLoading,
        isFetching,
        isError,
    } = useGetUserPlaylistsQuery({ page, size }, { skip: !reqUser });

    const [deletePlaylist] = useDeletePlaylistMutation();
    const containerRef = useRef();

    useEffect(() => {
        if (data?.content) {
            setAllPlaylists(prev => {
                if (page === 0) return data.content;
                const existingIds = new Set(prev.map(p => p.id));
                const newOnes = data.content.filter(p => !existingIds.has(p.id));
                return [...prev, ...newOnes];
            });
        }
    }, [data?.content, page]);

    // eslint-disable-next-line react-hooks/exhaustive-deps
    const onScroll = useCallback(
        throttle(() => {
            const el = containerRef.current;
            if (!el || isFetching || data?.last) return;
            if (el.scrollHeight - el.scrollTop - el.clientHeight < 100) {
                setPage(p => p + 1);
            }
        }, 200),
        [isFetching, data?.last]
    );

    useEffect(() => {
        const el = containerRef.current;
        if (!el) return;
        el.addEventListener('scroll', onScroll);
        return () => el.removeEventListener('scroll', onScroll);
    }, [onScroll]);

    const deleteHandler = async (id) => {
        if (!window.confirm('Are you sure you want to delete this playList?')) return;
        try {
            await deletePlaylist(id).unwrap();
            toast.success('Playlist deleted successfully.');
            setAllPlaylists(prev => prev.filter(pl => pl.id !== id));
            navigate('/home');
        } catch (err) {
            toast.error(err.data.businessErrornDescription);
        }
    }

    if (isLoading && page === 0) {
        return (
               <Spinner size={100}/>)
    }

    if (isError) {
        return (
            <div className="playListBox">
                <p>Error loading playlists</p>
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
            <div className="scrollBox"  ref={containerRef}>
                {
                    allPlaylists.map((playList)=> (
                        <div className="playList" key={playList.id}>
                            <i className='iconP'><BsMusicNoteList/></i>
                            <p onClick={() => {
                                closeModals()
                                navigate(`/playList/${playList.id}`);
                            }}>
                                {playList?.title}
                            </p>
                            <i className='trash' onClick={() => deleteHandler(playList.id)}><BsTrash/></i>
                        </div>
                    ))
                }
            </div>
        </div>
    )
}

export {MenuPlayList}