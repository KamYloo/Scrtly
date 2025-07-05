import { FaCirclePlay } from "react-icons/fa6";
import {Link} from "react-router-dom";
import React from "react";

function FavouriteArtistsList({ artists, onArtistClick }) {
    return (
        <div className="favouriteArtists">
            <div className="header">
                <h5>Favourite Artists</h5>
                <Link to="/artists">See all</Link>
            </div>
            <div className="artists">
                {artists.map(artist => (
                    <div key={artist?.id} className="item" onClick={() => onArtistClick(artist?.id)}>
                        <div className="imgPic">
                            <img src={artist?.profilePicture} alt={artist?.name} />
                            <i><FaCirclePlay /></i>
                        </div>
                        <div className="data">
                            <p>{artist?.pseudonym}</p>
                            <span>Performer</span>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default FavouriteArtistsList;