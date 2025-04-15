import React, {useEffect, useState} from 'react'
import "../../Styles/Album&&PlayList.css"
import { AudioList } from '../Song/AudioList.jsx'
import {useParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {getPlayList, getPlayListTracks} from "../../Redux/PlayList/Action.js";
import {AddSongToPlayList} from "./AddSongToPlayList.jsx";
import {PlayListForm} from "./PlayListForm.jsx";
import Spinner from "../../Components/Spinner.jsx";
import ErrorAlert from "../../Components/ErrorAlert.jsx";

// eslint-disable-next-line react/prop-types
function PlayList({ volume, onTrackChange}) {
    const [editPlayList, setEditPlayList] = useState(false)
    const {playListId} = useParams();
    const dispatch = useDispatch();
    const {playList, song} = useSelector(store => store);
    const [addSong, setAddSong] = useState(false)
    //const navigate = useNavigate();

    function formatTime(seconds) {
        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const remainingSeconds = seconds % 60;

        const hoursDisplay = hours > 0 ? `${hours}h ` : '';
        const minutesDisplay = minutes > 0 ? `${minutes}min ` : '';
        const secondsDisplay = remainingSeconds > 0 ? `${remainingSeconds}s` : '';

        return `${hoursDisplay}${minutesDisplay}${secondsDisplay}`.trim();
    }


    useEffect(() => {
        dispatch(getPlayList(playListId))
    }, [playListId, playList.uploadSong, playList.deletedSong]);

    useEffect(() => {
        dispatch(getPlayListTracks(playListId))
    }, [playListId, playList.uploadSong, playList.deletedSong, song.likedSong]);


    if (playList.loading || song.loading) {
        return <Spinner />;
    }
    if (playList.error) {
        return <ErrorAlert message={playList.error} />
    } else if (song.loading) {
        return <ErrorAlert message={song.error}/>
    }

    return (
        <div className='playListDetail'>
            <div className="topSection">
                <img src={playList.findPlayList?.coverImage} alt="" />
                <div className="playListData">
                    <p>PlayList</p>
                    <h1 className='playListName'>{playList.findPlayList?.title}</h1>
                    <p className='stats'>{playList.findPlayList?.user.fullName} • {playList.findPlayList?.tracksCount} Songs <span>• {playList.findPlayList?.creationDate} • {formatTime(playList.findPlayList?.totalDuration)}</span> </p>
                </div>
                <div className="buttons">
                    <button className="addSongBtn" onClick={() =>
                        setAddSong(((prev) => !prev))}>Add Song
                    </button>
                    <button className="editPlayListBtn" onClick={()=> setEditPlayList((prev) => !prev)}>Update</button>
                </div>
            </div>
            <AudioList volume={volume} onTrackChange={onTrackChange} initialSongs={playList?.songs.content} isplayListSongs={true} playListId={playListId}/>
            {addSong && <AddSongToPlayList onClose={() => setAddSong(((prev) => !prev))} playListId={playListId} />}
            {editPlayList && <PlayListForm onClose={() => setEditPlayList((prev) => !prev)} isEdit={playList.findPlayList} />}
        </div>
    )
}

export { PlayList }