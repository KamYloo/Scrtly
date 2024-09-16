import React from 'react'
import "../../Styles/AlbumsView&&ArtistsView.css"
import AlbumBanner from '../../img/albumBanner.png'
import { BiSearchAlt } from "react-icons/bi";
import AlbumPic from '../../img/album.jpg'
import { FaCirclePlay } from "react-icons/fa6";

function AlbumsView() {
    return (
        <div className='albumsView'>
            <div className="banner">
                <img src={AlbumBanner} alt="" />
                <div className="bottom">
                </div>
            </div>

            <div className="searchBox">
                <div className="search">
                    <input type="text" placeholder='Search Album...' />
                    <i className='searchIcon'><BiSearchAlt /></i>
                </div>
                <button>Search</button>
            </div>
            <div className="albums">
                <div className="album">
                    <i className="play"><FaCirclePlay/></i>
                    <img src={AlbumPic} alt=""/>
                    <span>Pitbull</span>
                    <p>Last Day</p>
                </div>
                <div className="album">
                    <i className="play"><FaCirclePlay/></i>
                    <img src={AlbumPic} alt=""/>
                    <span>Pitbull</span>
                    <p>Last Day</p>
                </div>
                <div className="album">
                    <i className="play"><FaCirclePlay/></i>
                    <img src={AlbumPic} alt=""/>
                    <span>Pitbull</span>
                    <p>Last Day</p>
                </div>
                <div className="album">
                    <i className="play"><FaCirclePlay/></i>
                    <img src={AlbumPic} alt=""/>
                    <span>Pitbull</span>
                    <p>Last Day</p>
                </div>
                <div className="album">
                    <i className="play"><FaCirclePlay/></i>
                    <img src={AlbumPic} alt=""/>
                    <span>Pitbull</span>
                    <p>Last Day</p>
                </div>
                <div className="album">
                    <i className="play"><FaCirclePlay/></i>
                    <img src={AlbumPic} alt=""/>
                    <span>Pitbull</span>
                    <p>Last Day</p>
                </div>
                <div className="album">
                    <i className="play"><FaCirclePlay/></i>
                    <img src={AlbumPic} alt=""/>
                    <span>Pitbull</span>
                    <p>Last Day</p>
                </div>
                <div className="album">
                    <i className="play"><FaCirclePlay/></i>
                    <img src={AlbumPic} alt=""/>
                    <span>Pitbull</span>
                    <p>Last Day</p>
                </div>
                <div className="album">
                    <i className="play"><FaCirclePlay/></i>
                    <img src={AlbumPic} alt=""/>
                    <span>Pitbull</span>
                    <p>Last Day</p>
                </div>
                <div className="album">
                    <i className="play"><FaCirclePlay/></i>
                    <img src={AlbumPic} alt=""/>
                    <span>Pitbull</span>
                    <p>Last Day</p>
                </div>
                <div className="album">
                    <i className="play"><FaCirclePlay/></i>
                    <img src={AlbumPic} alt=""/>
                    <span>Pitbull</span>
                    <p>Last Day</p>
                </div>
                <div className="album">
                    <i className="play"><FaCirclePlay/></i>
                    <img src={AlbumPic} alt=""/>
                    <span>Pitbull</span>
                    <p>Last Day</p>
                </div>
            </div>
        </div>
    )
}

export {AlbumsView}