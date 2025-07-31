import React from 'react'
import '../Styles/LeftMenu.css'
import logo from '../assets/logo512.png'

import { BiSearchAlt } from "react-icons/bi";
import { Menu } from './Menu';
import { MenuList } from './MenuList';
import { MenuPlayList } from '../features/PlayList/MenuPlayList.jsx';
import { TrackList } from './TrackList';
import {useNavigate} from "react-router-dom";

function LeftMenu({ onVolumeChange, currentTrack, setCreatePlayList}) {
    const navigate = useNavigate();
    return (
        <div className='LeftMenu'>
            <div className="logoBox" onClick={() => navigate('/home')}>
                <img src={logo} alt="" />
                <h2>Zuvoria</h2>
            </div>
            <div className="searchBox">
                <input type="text" placeholder='Search...' />
                <i className='searchIcon'><BiSearchAlt /></i>
            </div>

            <Menu title={'Menu'} menuObject={MenuList}/>
            <MenuPlayList setCreatePlayList={setCreatePlayList}/>
            <TrackList onVolumeChange={onVolumeChange} songName={currentTrack.songName} artist={currentTrack.artist} />
        </div>
    )
}

export { LeftMenu }