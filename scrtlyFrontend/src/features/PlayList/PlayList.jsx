import React, {useState} from 'react'
import "../../Styles/Album&&PlayList.css"
import { AudioList } from '../Song/AudioList.jsx'
import {useParams} from "react-router-dom";
import {AddSongToPlayList} from "./AddSongToPlayList.jsx";
import {PlayListForm} from "./PlayListForm.jsx";
import Spinner from "../../Components/Spinner.jsx";
import ErrorOverlay from "../../Components/ErrorOverlay.jsx";
import {useGetPlaylistQuery} from "../../Redux/services/playlistApi.js";

// eslint-disable-next-line react/prop-types
function PlayList({ volume, onTrackChange}) {
    const [editPlayList, setEditPlayList] = useState(false)
    const {playListId} = useParams();
    const [addSong, setAddSong] = useState(false)

    const {
        data: playList,
        isLoading: loadingPlaylist,
        isError: errorPlaylist,
        error: playlistError,
    } = useGetPlaylistQuery(playListId);

    function formatTime(seconds) {
        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const remainingSeconds = seconds % 60;

        const hoursDisplay = hours > 0 ? `${hours}h ` : '';
        const minutesDisplay = minutes > 0 ? `${minutes}min ` : '';
        const secondsDisplay = remainingSeconds > 0 ? `${remainingSeconds}s` : '';

        return `${hoursDisplay}${minutesDisplay}${secondsDisplay}`.trim();
    }

    if (loadingPlaylist) return <Spinner />;
    if (errorPlaylist) return <ErrorOverlay error={playlistError} />;

    return (
        <div className='playListDetail'>
            <div className="topSection">
                <img src={playList?.coverImage} alt="" />
                <div className="playListData">
                    <p>PlayList</p>
                    <h1 className='playListName'>{playList?.title}</h1>
                    <p className='stats'>{playList?.user.fullName} • {playList?.tracksCount} Songs <span>• {playList?.creationDate} • {formatTime(playList?.totalDuration)}</span> </p>
                </div>
                <div className="buttons">
                    <button className="addSongBtn" onClick={() =>
                        setAddSong(((prev) => !prev))}>Add Song
                    </button>
                    <button className="editPlayListBtn" onClick={()=> setEditPlayList((prev) => !prev)}>Update</button>
                </div>
            </div>
            <AudioList volume={volume} onTrackChange={onTrackChange} isplayListSongs={true} playListId={playListId}/>
            {addSong && <AddSongToPlayList onClose={() => setAddSong(((prev) => !prev))} playListId={playListId} />}
            {editPlayList && <PlayListForm onClose={() => setEditPlayList((prev) => !prev)} isEdit={playList} />}
        </div>
    )
}

export { PlayList }