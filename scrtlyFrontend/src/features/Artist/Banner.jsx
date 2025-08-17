import React, {useState} from 'react'

import Verification from '../../assets/check.png'
import { FaEllipsisH, FaHeadphones, FaCheck } from 'react-icons/fa'
import {EditArtist} from "./EditArtist.jsx";
import {CreateAlbum} from "../Album/CreateAlbum.jsx";
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";
import {useFollowUserMutation} from "../../Redux/services/userApi.js";

// eslint-disable-next-line react/prop-types
function Banner({artist}) {
    const [menu, setMenu] = useState(false)
    const [editArtist, setEditArtist] = useState(false)
    const [createAlbum, setCreateAlbum] = useState(false)
    const { data: reqUser } = useGetCurrentUserQuery(null, {
        skip: !localStorage.getItem('isLoggedIn'),
    });
    const [followUser, { isLoading: isFollowing }] = useFollowUserMutation();

    const handleFollow = async () => {
        await followUser(artist.id).unwrap();
    };

    const isOwner = artist?.id === reqUser?.id;

    return (
        <div className='banner'>
            <img src={artist?.bannerImg} alt="" className='bannerImg' />
            <div className="content">
                <div className="top">
                    <p>Home <span>/Popular Artist</span></p>
                    {isOwner && <i onClick={() => setMenu(((prev) => !prev))}><FaEllipsisH/></i>}
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
                            <h2>{artist?.pseudonym}</h2>
                            <img src={Verification} alt=""/>
                        </div>
                        <p><i><FaHeadphones/></i> {artist?.monthlyPlays} <span>Monthly listeners</span></p>
                    </div>
                    <div className="right">
                        {!isOwner && <button className={artist?.observed ? 'following' : 'follow'}
                        onClick={handleFollow} disabled={isFollowing}><i><FaCheck/></i>{artist?.observed ? 'Following': 'Follow'}</button>}
                    </div>
                </div>
            </div>
            <div className="bottom">
            </div>
            {editArtist && <EditArtist artist={artist} onClose={() => setEditArtist(((prev) => !prev))}/>}
            {createAlbum && <CreateAlbum onClose={() => setCreateAlbum(((prev) => !prev))} />}
        </div>
    )
}

export { Banner }