import React from 'react'
import {BsJournalAlbum} from 'react-icons/bs'
import {FaRocketchat, FaHome, FaPodcast, FaMicrophoneAlt, FaBroadcastTower } from 'react-icons/fa'
import {BiPulse} from 'react-icons/bi'

const MenuList = [
    {
        id: 1,
        icon: <FaHome/>,
        name: "Home",
    },
    {
        id: 2,
        icon: <FaRocketchat/>,
        name: "Chat",
    },
    {
        id: 3,
        icon: <FaMicrophoneAlt/>,
        name: "Artists",
    },
    {
        id: 4,
        icon: <BsJournalAlbum/>,
        name: "Album",
    },
    {
        id: 5,
        icon: <FaPodcast/>,
        name: "Podcast",
    },
    {
        id: 6,
        icon: <FaBroadcastTower/>,
        name: "Radio",
    },
    {
        id: 7,
        icon: <BiPulse/>,
        name: "Discover",
    },
]

export {MenuList}

