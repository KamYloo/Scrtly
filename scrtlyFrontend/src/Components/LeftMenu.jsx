import React from 'react'
import '../Styles/LeftMenu.css'
import logo from '../img/logo.png'

import { FaEllipsisH } from "react-icons/fa";
import { BiSearchAlt } from "react-icons/bi";
import { Menu } from './Menu';
import { MenuList } from './MenuList';
import { MenuPlayList } from './MenuPlayList';
import { TrackList } from './TrackList';

function LeftMenu({ onVolumeChange, currentTrack}) {
    return (
        <div className='LeftMenu'>
            <div className="logoBox">
                <img src={logo} alt="" />
                <h2>Scrtly</h2>
                <i><FaEllipsisH /></i>
            </div>
            <div className="searchBox">
                <input type="text" placeholder='Search...' />
                <i className='searchIcon'><BiSearchAlt /></i>
            </div>

            <Menu title={'Menu'} menuObject={MenuList}/>
            <MenuPlayList />
            <TrackList onVolumeChange={onVolumeChange} songName={currentTrack.songName} artist={currentTrack.artist} />
        </div>
    )
}

export { LeftMenu }