import React, {useState} from 'react'
import toast from "react-hot-toast";
import {useLazySearchSongQuery} from "../../Redux/services/songApi.js";
import {useUploadSongToPlaylistMutation} from "../../Redux/services/playlistApi.js";

function AddSongToPlayList({playListId}) {
    const [keyword, setKeyword] = useState('');

    const [triggerSearch, { data: searchResults = [], isLoading: isSearching, isError: searchError }] =
        useLazySearchSongQuery();

    const [uploadSong, { isLoading: isAdding }] = useUploadSongToPlaylistMutation();

    const handleSearch = (e) => {
        e.preventDefault();
        if (keyword.trim() === '') return;
        triggerSearch(keyword);
    };

    const handleAddSong = async (songId) => {
        try {
            await uploadSong({ playListId, songId }).unwrap();
            toast.success('Song added to playList successfully.');
        } catch (err) {
            toast.error(err.data.businessErrornDescription);
        }
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
                            <button onClick={() => handleAddSong(song.id)} disabled={isAdding }>
                                {isAdding  ? "Adding..." : "Add Song"}
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