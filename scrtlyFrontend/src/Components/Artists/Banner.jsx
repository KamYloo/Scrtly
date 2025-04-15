import React, {useState} from 'react'

import Verification from '../../img/check.png'
import { FaEllipsisH, FaHeadphones, FaCheck } from 'react-icons/fa'
import {EditArtist} from "./EditArtist.jsx";
import {CreateAlbum} from "../AlbumComponents/CreateAlbum.jsx";
import {useDispatch} from "react-redux";
import {followUser} from "../../Redux/AuthService/Action.js";

// eslint-disable-next-line react/prop-types
function Banner({artist}) {
    const [menu, setMenu] = useState(false)
    const [editArtist, setEditArtist] = useState(false)
    const [createAlbum, setCreateAlbum] = useState(false)
    const dispatch = useDispatch()
    const userData = (() => { try { return JSON.parse(localStorage.getItem("user")) || null; } catch { return null; } })();

    return (
        <div className='banner'>
            <img src={artist.findArtist?.bannerImg} alt="" className='bannerImg' />
            <div className="content">
                <div className="top">
                    <p>Home <span>/Popular Artist</span></p>
                    {artist.findArtist?.id === userData?.id && <i onClick={() => setMenu(((prev) => !prev))}><FaEllipsisH/></i>}
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
            </ul>
            }
                </div>
                <div className="artist">
                    <div className="left">
                        <div className="name">
                            <h2>{artist.findArtist?.pseudonym}</h2>
                            <img src={Verification} alt=""/>
                        </div>
                        <p><i><FaHeadphones/></i> 12,132,5478 <span>Monthly listeners</span></p>
                    </div>
                    <div className="right">
                        {!artist.findArtist?.id === userData?.id && <button className={artist.findArtist?.followed ? 'following' : 'follow'}
                        onClick={() => dispatch(followUser(artist.findArtist?.id))}><i><FaCheck/></i>{artist.findArtist?.observed ? 'Following': 'Follow'}</button>}
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