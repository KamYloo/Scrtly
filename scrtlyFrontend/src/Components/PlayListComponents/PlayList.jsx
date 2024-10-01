import React, {useEffect, useState} from 'react'
import "../../Styles/Album&&PlayList.css"
import { AudioList } from '../SongComponents/AudioList.jsx'
import {useParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {BASE_API_URL} from "../../config/api.js";
import {getPlayList, getPlayListTracks} from "../../Redux/PlayList/Action.js";
import {AddSongToPlayList} from "./AddSongToPlayList.jsx";

// eslint-disable-next-line react/prop-types
function PlayList({ volume, onTrackChange}) {
    const {playListId} = useParams();
    const dispatch = useDispatch();
    const {playList} = useSelector(store => store);
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
    }, [playListId, playList.uploadSong, playList.deletedSong]);

    return (
        <div className='playListDetail'>
            <div className="topSection">
                <img src={`${BASE_API_URL}${playList.findPlayList?.playListImage || ''}`} alt="" />
                <div className="playListData">
                    <p>Album</p>
                    <h1 className='playListName'>{playList.findPlayList?.title}</h1>
                    <p className='stats'>{playList.findPlayList?.user.fullName} • {playList.findPlayList?.totalSongs} Songs <span>• {playList.findPlayList?.creationDate} • {formatTime(playList.findPlayList?.totalDuration)}</span> </p>
                </div>
                    <div className="buttons">
                        <button className="addSongBtn" onClick={() =>
                            setAddSong(((prev) => !prev))}>Add Song</button>
                    </div>
            </div>
            <AudioList volume={volume} onTrackChange={onTrackChange} initialSongs={playList?.songs} isplayListSongs={true} playListId={playListId}/>
            {addSong && <AddSongToPlayList onClose={() => setAddSong(((prev) => !prev))} playListId={playListId} />}
        </div>
    )
}

export { PlayList }