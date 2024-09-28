import React, {useEffect, useState} from 'react'
import {useDispatch, useSelector} from "react-redux";
import {searchSong} from "../../Redux/Song/Action.js";
import {BASE_API_URL} from "../../config/api.js";
import {addSongToPlayList} from "../../Redux/PlayList/Action.js";

function AddSongToPlayList({playListId}) {
    const [keyword, setKeyword] = useState('');
    const dispatch = useDispatch();
    const { song } = useSelector(store => store);

    const handleSearch = async (e) => {
        e.preventDefault();
        if (keyword.trim() !== '') {
            dispatch(searchSong({ keyword }));
        }
    }

    const handleAddSong = (songId) => {
        dispatch(addSongToPlayList({playListId, songId}));
    }

    return (
        <div className='searchSong'>
            <form onSubmit={handleSearch}>
                <input type="text" placeholder='title...' onChange={(e) => setKeyword(e.target.value)}/>
                <button>Search</button>
            </form>
            {song.searchResults && song.searchResults.length > 0 ? (
                <div className="songList">
                    {song.searchResults.map((song) => (
                        <div className="song" key={song.id}>
                            <div className="detail">
                                <img src={`${BASE_API_URL}${song?.imageSong || ''}`} alt="" />
                                <div className="songData">
                                    <span>{song?.title}</span>
                                    <p>{song?.artist.artistName}</p>
                                </div>
                            </div>
                            <button onClick={() => handleAddSong(song.id)}>Add Song</button>
                        </div>
                    ))}
                </div>
            ) : (
                <p>No songs found</p>
            )}

        </div>
    )
}

export { AddSongToPlayList }