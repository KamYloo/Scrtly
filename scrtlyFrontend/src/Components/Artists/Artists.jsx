import React from 'react'
import "../../Styles/AlbumsView&&ArtistsView.css"
import ArtistBanner from '../../img/ArtistsBanner.png'
import Verification from '../../img/check.png'
import { BiSearchAlt } from "react-icons/bi";
import { FaHeadphones } from "react-icons/fa";
import ArtistPic from '../../img/timber.jpg'
import {FaCirclePlay} from "react-icons/fa6";

function Artists() {
    return (
        <div className='artistsView'>
            <div className="banner">
                <img src={ArtistBanner} alt="" />
                <div className="bottom">
                </div>
            </div>

            <div className="searchBox">
                <div className="search">
                    <input type="text" placeholder='Search Artist...' />
                    <i className='searchIcon'><BiSearchAlt /></i>
                </div>
                <button>Search</button>
            </div>
            <div className="artists">
                <div className="artist">
                    <i className="listen"><FaHeadphones/></i>
                    <div className="imgPic">
                        <img src={ArtistPic} alt=""/>
                        <img className='check' src={Verification} alt=""/>
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <i className="listen"><FaHeadphones/></i>
                    <div className="imgPic">
                        <img src={ArtistPic} alt=""/>
                        <img className='check' src={Verification} alt=""/>
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <i className="listen"><FaHeadphones/></i>
                    <div className="imgPic">
                        <img src={ArtistPic} alt=""/>
                        <img className='check' src={Verification} alt=""/>
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <i className="listen"><FaHeadphones/></i>
                    <div className="imgPic">
                        <img src={ArtistPic} alt=""/>
                        <img className='check' src={Verification} alt=""/>
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <i className="listen"><FaHeadphones/></i>
                    <div className="imgPic">
                        <img src={ArtistPic} alt=""/>
                        <img className='check' src={Verification} alt=""/>
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <i className="listen"><FaHeadphones/></i>
                    <div className="imgPic">
                        <img src={ArtistPic} alt=""/>
                        <img className='check' src={Verification} alt=""/>
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <i className="listen"><FaHeadphones/></i>
                    <div className="imgPic">
                        <img src={ArtistPic} alt=""/>
                        <img className='check' src={Verification} alt=""/>
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <i className="listen"><FaHeadphones/></i>
                    <div className="imgPic">
                        <img src={ArtistPic} alt=""/>
                        <img className='check' src={Verification} alt=""/>
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <i className="listen"><FaHeadphones/></i>
                    <div className="imgPic">
                        <img src={ArtistPic} alt=""/>
                        <img className='check' src={Verification} alt=""/>
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <i className="listen"><FaHeadphones/></i>
                    <div className="imgPic">
                        <img src={ArtistPic} alt=""/>
                        <img className='check' src={Verification} alt=""/>
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <i className="listen"><FaHeadphones/></i>
                    <div className="imgPic">
                        <img src={ArtistPic} alt=""/>
                        <img className='check' src={Verification} alt=""/>
                    </div>
                    <p>Pitbull</p>
                </div>
            </div>

        </div>
    )
}

export { Artists }