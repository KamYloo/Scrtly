import React, {useEffect} from 'react'

import Artist from '../img/banner.png'
import Verification from '../img/check.png'
import { FaEllipsisH, FaHeadphones, FaCheck } from 'react-icons/fa'
import {useParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";
import {findArtistById, getAllArtists} from "../Redux/Artist/Action.js";

function Banner() {
    const {artistId} = useParams();
    const dispatch = useDispatch();
    const {artist} = useSelector(store => store);

    useEffect(() => {
        dispatch(findArtistById(artistId))
    }, [artistId])

    return (
        <div className='banner'>
            <img src={Artist} alt="" className='bannerImg' />
            <div className="content">
                <div className="top">
                    <p>Home <span>/Popular Artist</span></p>
                    <i><FaEllipsisH /></i>
                </div>
                <div className="artist">
                    <div className="left">
                        <div className="name">
                            <h2>{artist.findArtist.artistName}</h2>
                            <img src={Verification} alt="" />
                        </div>
                        <p><i><FaHeadphones /></i> 12,132,5478 <span>Monthly listeners</span></p>
                    </div>
                    <div className="right">
                        <a href="#">Play</a>
                        <a href="#"><i><FaCheck /></i>Following</a>
                    </div>
                </div>
            </div>
            <div className="bottom">
            </div>
        </div>
    )
}

export { Banner }