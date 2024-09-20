import React, {useEffect, useState} from 'react'

import Verification from '../img/check.png'
import { FaEllipsisH, FaHeadphones, FaCheck } from 'react-icons/fa'
import {useParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {findArtistById} from "../Redux/Artist/Action.js";
import {EditArtist} from "./Artists/EditArtist.jsx";
import {BASE_API_URL} from "../config/api.js";
import {CreateAlbum} from "./AlbumComponents/CreateAlbum.jsx";

function Banner() {
    const {artistId} = useParams();
    const dispatch = useDispatch();
    const {artist} = useSelector(store => store);
    const [menu, setMenu] = useState(false)
    const [editArtist, setEditArtist] = useState(false)
    const [createAlbum, setCreateAlbum] = useState(false)

    useEffect(() => {
        dispatch(findArtistById(artistId))
    }, [artistId])

    return (
        <div className='banner'>
            <img src={`${BASE_API_URL}/${artist.findArtist?.bannerImg || ''}`} alt="" className='bannerImg' />
            <div className="content">
                <div className="top">
                    <p>Home <span>/Popular Artist</span></p>
                    <i onClick={() => setMenu(((prev) => !prev))}><FaEllipsisH/></i>
                    {menu && <ul className="list">
                        <li onClick={() => {
                            setEditArtist(((prev) => !prev))
                            setMenu(((prev) => !prev))
                        }} className="option">
                            <span>Edit</span>
                        </li>
                        <li onClick={() => {
                            setCreateAlbum(((prev) => !prev))
                            setMenu(((prev) => !prev))
                        }} className="option">
                            <span>CreateAlbum</span>
                        </li>
                    </ul>}
                </div>
                <div className="artist">
                    <div className="left">
                        <div className="name">
                            <h2>{artist.findArtist?.artistName}</h2>
                            <img src={Verification} alt=""/>
                        </div>
                        <p><i><FaHeadphones/></i> 12,132,5478 <span>Monthly listeners</span></p>
                    </div>
                    <div className="right">
                        <a href="#">Play</a>
                        {!artist.findArtist?.req_artist && <a href="#"><i><FaCheck/></i>Following</a>}
                    </div>
                </div>
            </div>
            <div className="bottom">
            </div>
            {editArtist && <EditArtist onClose={() => setEditArtist(((prev) => !prev))}/>}
            {createAlbum && <CreateAlbum onClose={() => setCreateAlbum(((prev) => !prev))} />}
        </div>
    )
}

export { Banner }