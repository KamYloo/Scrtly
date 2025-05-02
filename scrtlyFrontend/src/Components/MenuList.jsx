import React from 'react'
import { BsJournalAlbum } from 'react-icons/bs'
import { FaRocketchat, FaHome, FaMicrophoneAlt } from 'react-icons/fa'
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
        route: "/albums",
    },
    {
        id: 5,
        icon: <BiPulse />,
        name: "Discover",
         route: "/discover",
    },
]

export { MenuList }

