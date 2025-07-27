import { FaRegHeart, FaHeart } from "react-icons/fa";

function TrendingSong({ trending, isFav, onLike, onListen }) {
    return (
        <div className='trending'>
            <div className="left">
                <h5>Trending New Song</h5>
                <div className="info">
                    <h2>{trending?.title}</h2>
                    <h4>{trending?.artist.pseudonym}</h4>
                    <h5>{trending?.playCount} Plays</h5>
                    <div className="buttons">
                        <button onClick={onListen}>Listen Now</button>
                        <i onClick={onLike}>
                            {isFav ? <FaHeart /> : <FaRegHeart />}
                        </i>
                    </div>
                </div>
            </div>
            <img src={trending?.imageSong} alt={trending?.title} />
        </div>
    );
}

export default TrendingSong;