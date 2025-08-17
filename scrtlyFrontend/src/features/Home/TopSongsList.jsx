import { FaPlay } from "react-icons/fa";
import { FaRegSquarePlus } from "react-icons/fa6";

function TopSongsList({ songs, formatTime, onSongPlay }) {
    return (
        <div className="musicList">
            <div className="header">
                <h5>Top Songs</h5>
            </div>
            <div className="items">
                {songs.map((song, idx) => (
                    <div key={song?.id} className="item">
                        <div className="info">
                            <p>{String(idx + 1).padStart(2, '0')}</p>
                            <img src={song?.imageSong} alt={song?.title} />
                            <div className="details">
                                <h5>{song?.title}</h5>
                                <p>{song?.artist.pseudonym}</p>
                            </div>
                        </div>
                        <div className="actions">
                            <p>{formatTime(song?.duration)}</p>
                            <div className="icon">
                                <i onClick={() => onSongPlay(song)}><FaPlay/></i>
                            </div>
                            <i><FaRegSquarePlus/></i>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default TopSongsList;