import { FaCirclePlay } from "react-icons/fa6";
import {Link} from "react-router-dom";
import React from "react";

function AlbumsList({ albums, onAlbumClick }) {
    return (
        <div className="albums">
            <div className="header">
                <h5>Top Albums</h5>
                <Link to="/albums">See all</Link>
            </div>
            <div className="items">
                {albums.map(album => (
                    <div key={album?.id} className="item" onClick={() => onAlbumClick(album?.id)}>
                        <div className="imgPic">
                            <i className="play"><FaCirclePlay /></i>
                            <img src={album?.albumImage} alt={album?.title} />
                        </div>
                        <div className="data">
                            <span>{album?.artist.pseudonym}</span>
                            <p>{album?.title}</p>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default AlbumsList;