import React, {useState} from 'react'
import {useDispatch, useSelector} from "react-redux";
import {addSongToPlayList} from "../../Redux/PlayList/Action.js";
import toast from "react-hot-toast";
import {useLazySearchSongQuery} from "../../Redux/services/songApi.js";

function AddSongToPlayList({playListId}) {
    const [keyword, setKeyword] = useState('');
    const dispatch = useDispatch();
    const { loading, error } = useSelector(state => state.playList);

    const [triggerSearch, { data: searchResults = [], isLoading: isSearching, isError: searchError }] =
        useLazySearchSongQuery();

    const handleSearch = (e) => {
        e.preventDefault();
        if (keyword.trim() === '') return;
        triggerSearch(keyword);
    };

    const handleAddSong = (songId) => {
        dispatch(addSongToPlayList({playListId, songId}))
            .then(() => {
                toast.success('Song added to playList successfully.');
            })
            .catch(() => {
                toast.error(error);
            })
    }

    return (
        <div className='searchSong'>
            <form onSubmit={handleSearch}>
                <input type="text" placeholder='title...' onChange={(e) => setKeyword(e.target.value)}/>
                <button disabled={keyword.trim() === ''}>Search</button>
            </form>
            {!isSearching && searchResults.length > 0 ? (
                <div className="songList">
                    {searchResults.map((song) => (
                        <div className="song" key={song.id}>
                            <div className="detail">
                                <img src={song?.imageSong} alt="" />
                                <div className="songData">
                                    <span>{song?.title}</span>
                                    <p>{song?.artist.pseudonym}</p>
                                </div>
                            </div>
                            <button onClick={() => handleAddSong(song.id)} disabled={loading}>
                                {loading ? "Adding..." : "Add Song"}
                            </button>
                        </div>
                    ))}
                </div>
            ) : (
                !isSearching && !searchError && searchResults.length === 0 && (
                    <p className="noResult">No songs found</p>)
            )}

        </div>
    )
}

export { AddSongToPlayList }