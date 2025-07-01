import React, {useState} from 'react'
import {useDispatch, useSelector} from "react-redux";
import {searchSong} from "../../Redux/Song/Action.js";
import {addSongToPlayList} from "../../Redux/PlayList/Action.js";
import toast from "react-hot-toast";

function AddSongToPlayList({playListId}) {
    const [keyword, setKeyword] = useState('');
    const dispatch = useDispatch();
    const { song } = useSelector(state => state);
    const { loading, error } = useSelector(state => state.playList);

    const handleSearch = async (e) => {
        e.preventDefault();
        if (keyword.trim() !== '') {
            dispatch(searchSong({ keyword }));
        }
    }

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
                <button>Search</button>
            </form>
            {song.searchResults && song.searchResults.length > 0 ? (
                <div className="songList">
                    {song.searchResults.map((song) => (
                        <div className="song" key={song.id}>
                            <div className="detail">
                                <img src={song?.imageSong} alt="" />
                                <div className="songData">
                                    <span>{song?.title}</span>
                                    <p>{song?.artist.artistName}</p>
                                </div>
                            </div>
                            <button onClick={() => handleAddSong(song.id)} disabled={loading}>
                                {loading ? "Adding..." : "Add Song"}
                            </button>
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