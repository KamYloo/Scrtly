import React from 'react'
import { BsJournalAlbum } from 'react-icons/bs'
import { FaRocketchat, FaHome, FaPodcast, FaMicrophoneAlt, FaBroadcastTower } from 'react-icons/fa'
import { BiPulse } from 'react-icons/bi'

const MenuList = [
    {
        id: 1,
        icon: <FaHome />,
        name: "Home",
        route: "/home",
    },
    {
        id: 2,
        icon: <FaRocketchat />,
        name: "Chat",
        route: "/chat",
    },
    {
        id: 3,
        icon: <FaMicrophoneAlt />,
        name: "Artists",
        route: "/artists",
    },
    {
        id: 4,
        icon: <BsJournalAlbum />,
        name: "Album",
        route: "/album",
    },
    {
        id: 5,
        icon: <FaPodcast />,
        name: "Podcast",
        route: "/podcast",
    },
    {
        id: 6,
        icon: <FaBroadcastTower />,
        name: "Radio",
        route: "/radio",
    },
    {
        id: 7,
        icon: <BiPulse />,
        name: "Discover",
         route: "/discover",
    },
]

export { MenuList }

