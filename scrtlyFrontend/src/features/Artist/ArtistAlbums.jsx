import React, {} from 'react'
import {FaCirclePlay} from "react-icons/fa6";
import "../../Styles/AlbumsView&&ArtistsView.css"
import {useNavigate, useParams} from "react-router-dom";
import {useGetArtistAlbumsQuery} from "../../Redux/services/albumApi.js";
import Spinner from "../../Components/Spinner.jsx";
import ErrorOverlay from "../../Components/ErrorOverlay.jsx";

function ArtistAlbums() {
    const {artistId} = useParams();
    const navigate = useNavigate();
    const {
        data: albums = [],
        isLoading,
        isError,
        error,
    } = useGetArtistAlbumsQuery(artistId)

    if (isLoading) {
        return <Spinner />
    }
    if (isError) {
        return <ErrorOverlay error={error} />
    }

    return (
        <div className='artistAlbums'>
            <div className="albums">
                { albums.map((item) => (
                    <div className="album" key={item.id} onClick={() => navigate(`/album/${item.id}`)}>
                        <i className="play"><FaCirclePlay/></i>
                        <img src={item?.albumImage} alt=""/>
                        <span>{item?.artist.pseudonym}</span>
                        <p>{item?.title}</p>
                    </div>
                ))}
            </div>
        </div>
    )
}

export {ArtistAlbums}