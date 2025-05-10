import React, {useEffect} from 'react'
import {FaCirclePlay} from "react-icons/fa6";
import "../../Styles/AlbumsView&&ArtistsView.css"
import {getArtistAlbums} from "../../Redux/Album/Action.js";
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useParams} from "react-router-dom";

function ArtistAlbums() {
    const {artistId} = useParams();
    const dispatch = useDispatch();
    const {album} = useSelector(state => state);
    const navigate = useNavigate();

    useEffect(() => {
        dispatch(getArtistAlbums(artistId))
    }, [artistId, dispatch])

    return (
        <div className='artistAlbums'>
            <div className="albums">
                { album?.albums2.map((item) => (
                    <div className="album" key={item.id} onClick={() => navigate(`/album/${item.id}`)}>
                        <i className="play"><FaCirclePlay/></i>
                        <img src={item?.albumImage} alt=""/>
                        <span>{item?.artist.artistName}</span>
                        <p>{item?.title}</p>
                    </div>
                ))}
            </div>
        </div>
    )
}

export {ArtistAlbums}