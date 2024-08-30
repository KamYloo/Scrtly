import React from 'react'
import "../../Styles/Artists.css"
import ArtistBanner from '../../img/ArtistsBanner.png'
import Verification from '../../img/check.png'
import { BiSearchAlt } from "react-icons/bi";
import ArtistPic from '../../img/timber.jpg'

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
                    <input type="text" placeholder='Search...' />
                    <i className='searchIcon'><BiSearchAlt /></i>
                </div>
                <button>Search</button>
            </div>
            <div className="artists">
                <div className="artist">
                    <div className="imgPic">
                        <img src={ArtistPic} alt="" />
                        <img className='check' src={Verification} alt="" />
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <div className="imgPic">
                        <img src={ArtistPic} alt="" />
                        <img className='check' src={Verification} alt="" />
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <div className="imgPic">
                        <img src={ArtistPic} alt="" />
                        <img className='check' src={Verification} alt="" />
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <div className="imgPic">
                        <img src={ArtistPic} alt="" />
                        <img className='check' src={Verification} alt="" />
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <div className="imgPic">
                        <img src={ArtistPic} alt="" />
                        <img className='check' src={Verification} alt="" />
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <div className="imgPic">
                        <img src={ArtistPic} alt="" />
                        <img className='check' src={Verification} alt="" />
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <div className="imgPic">
                        <img src={ArtistPic} alt="" />
                        <img className='check' src={Verification} alt="" />
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <div className="imgPic">
                        <img src={ArtistPic} alt="" />
                        <img className='check' src={Verification} alt="" />
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <div className="imgPic">
                        <img src={ArtistPic} alt="" />
                        <img className='check' src={Verification} alt="" />
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <div className="imgPic">
                        <img src={ArtistPic} alt="" />
                        <img className='check' src={Verification} alt="" />
                    </div>
                    <p>Pitbull</p>
                </div>
                <div className="artist">
                    <div className="imgPic">
                        <img src={ArtistPic} alt="" />
                        <img className='check' src={Verification} alt="" />
                    </div>
                    <p>Pitbull</p>
                </div>
            </div>

        </div>
    )
}

export { Artists }