import React from 'react'
import "../Styles/home.css"
import { FaRegHeart, FaPlay } from "react-icons/fa";
import album from "../img/album.jpg"
import { FaCirclePlay } from "react-icons/fa6";
import { FaRegSquarePlus } from "react-icons/fa6";
function Home() {

    return (
        <div className='homeBox'>
            <div className='trending'>
                <div className="left">
                    <h5>Trending New Song</h5>
                    <div className="info">
                        <h2>Timber</h2>
                        <h4>Pitbull</h4>
                        <h5>63 Million Plays</h5>
                        <div className="buttons">
                            <button>Listen Now</button>
                            <i><FaRegHeart /></i>
                        </div>
                    </div>
                </div>
                <img src={album} alt="" />
            </div>
            <div className="onTime">
                <div className="albums">
                    <div className="header">
                        <h5>Top Albums</h5>
                        <a href="">See all</a>
                    </div>
                    <div className="items">
                        <div className="item">
                            <div className="imgPic">
                                <i className="play"><FaCirclePlay /></i>
                                <img src={album} alt="" />
                            </div>
                            <div className="data">
                                <span>Pitbull</span>
                                <p>Timber</p>
                            </div>
                        </div>
                        <div className="item">
                            <div className="imgPic">
                                <i className="play"><FaCirclePlay /></i>
                                <img src={album} alt="" />
                            </div>
                            <div className="data">
                                <span>Pitbull</span>
                                <p>Timber</p>
                            </div>
                        </div>
                        <div className="item">
                            <div className="imgPic">
                                <i className="play"><FaCirclePlay /></i>
                                <img src={album} alt="" />
                            </div>
                            <div className="data">
                                <span>Pitbull</span>
                                <p>Timber</p>
                            </div>
                        </div>
                        <div className="item">
                            <div className="imgPic">
                                <i className="play"><FaCirclePlay /></i>
                                <img src={album} alt="" />
                            </div>
                            <div className="data">
                                <span>Pitbull</span>
                                <p>Timber</p>
                            </div>
                        </div>
                        <div className="item">
                            <div className="imgPic">
                                <i className="play"><FaCirclePlay /></i>
                                <img src={album} alt="" />
                            </div>
                            <div className="data">
                                <span>Pitbull</span>
                                <p>Timber</p>
                            </div>
                        </div>
                        <div className="item">
                            <div className="imgPic">
                                <i className="play"><FaCirclePlay /></i>
                                <img src={album} alt="" />
                            </div>
                            <div className="data">
                                <span>Pitbull</span>
                                <p>Timber</p>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="musicList">
                    <div className="header">
                        <h5>Top Songs</h5>
                        <a href="">See all</a>
                    </div>
                    <div className="items">
                        <div className="item">
                            <div className="info">
                                <p>01</p>
                                <img src={album} alt="" />
                                <div className="details">
                                    <h5>Timber</h5>
                                    <p>Pitbull</p>
                                </div>
                            </div>
                            <div className="actions">
                                <p>03:45</p>
                                <div className="icon">
                                    <i><FaPlay /></i>
                                </div>
                                <i><FaRegSquarePlus /></i>
                            </div>
                        </div>
                        <div className="item">
                            <div className="info">
                                <p>02</p>
                                <img src={album} alt="" />
                                <div className="details">
                                    <h5>Timber</h5>
                                    <p>Pitbull</p>
                                </div>
                            </div>
                            <div className="actions">
                                <p>03:45</p>
                                <div className="icon">
                                    <i><FaPlay /></i>
                                </div>
                                <i><FaRegSquarePlus /></i>
                            </div>
                        </div>
                        <div className="item">
                            <div className="info">
                                <p>03</p>
                                <img src={album} alt="" />
                                <div className="details">
                                    <h5>Timber</h5>
                                    <p>Pitbull</p>
                                </div>
                            </div>
                            <div className="actions">
                                <p>03:45</p>
                                <div className="icon">
                                    <i><FaPlay /></i>
                                </div>
                                <i><FaRegSquarePlus /></i>
                            </div>
                        </div>
                        <div className="item">
                            <div className="info">
                                <p>04</p>
                                <img src={album} alt="" />
                                <div className="details">
                                    <h5>Timber</h5>
                                    <p>Pitbull</p>
                                </div>
                            </div>
                            <div className="actions">
                                <p>03:45</p>
                                <div className="icon">
                                    <i><FaPlay /></i>
                                </div>
                                <i><FaRegSquarePlus /></i>
                            </div>
                        </div>
                        <div className="item">
                            <div className="info">
                                <p>05</p>
                                <img src={album} alt="" />
                                <div className="details">
                                    <h5>Timber</h5>
                                    <p>Pitbull</p>
                                </div>
                            </div>
                            <div className="actions">
                                <p>03:45</p>
                                <div className="icon">
                                    <i><FaPlay /></i>
                                </div>
                                <i><FaRegSquarePlus /></i>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div className="favouriteArtists">
                <div className="header">
                    <h5>Favourite Artists</h5>
                    <a href="">See all</a>
                </div>
                <div className="artists">
                    <div className="item">
                        <div className="imgPic">
                            <img src={album} alt="" />
                            <i><FaCirclePlay /></i>
                        </div>
                        <div className="data">
                            <p>Pitbull</p>
                            <span>Performer</span>
                        </div>
                    </div>
                    <div className="item">
                        <div className="imgPic">
                            <img src={album} alt="" />
                            <i><FaCirclePlay /></i>
                        </div>
                        <div className="data">
                            <p>Pitbull</p>
                            <span>Performer</span>
                        </div>
                    </div>
                    <div className="item">
                        <div className="imgPic">
                            <img src={album} alt="" />
                            <i><FaCirclePlay /></i>
                        </div>
                        <div className="data">
                            <p>Pitbull</p>
                            <span>Performer</span>
                        </div>
                    </div>
                    <div className="item">
                        <div className="imgPic">
                            <img src={album} alt="" />
                            <i><FaCirclePlay /></i>
                        </div>
                        <div className="data">
                            <p>Pitbull</p>
                            <span>Performer</span>
                        </div>
                    </div>
                    <div className="item">
                        <div className="imgPic">
                            <img src={album} alt="" />
                            <i><FaCirclePlay /></i>
                        </div>
                        <div className="data">
                            <p>Pitbull</p>
                            <span>Performer</span>
                        </div>
                    </div>
                    <div className="item">
                        <div className="imgPic">
                            <img src={album} alt="" />
                            <i><FaCirclePlay /></i>
                        </div>
                        <div className="data">
                            <p>Pitbull</p>
                            <span>Performer</span>
                        </div>
                    </div>
                    <div className="item">
                        <div className="imgPic">
                            <img src={album} alt="" />
                            <i><FaCirclePlay /></i>
                        </div>
                        <div className="data">
                            <p>Pitbull</p>
                            <span>Performer</span>
                        </div>
                    </div>
                    <div className="item">
                        <div className="imgPic">
                            <img src={album} alt="" />
                            <i><FaCirclePlay /></i>
                        </div>
                        <div className="data">
                            <p>Pitbull</p>
                            <span>Performer</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}

export { Home }