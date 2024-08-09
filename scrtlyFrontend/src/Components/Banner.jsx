import React from 'react'

import Artist from '../img/banner.png'
import Verification from '../img/check.png'
import { FaEllipsisH, FaHeadphones, FaCheck } from 'react-icons/fa'

function Banner() {
    return (
        <div className='banner'>
            <img src={Artist} alt="" className='bannerImg' />
            <div className="content">
                <div className="top">
                    <p>Home <span>/Popular Artist</span></p>
                    <i><FaEllipsisH /></i>
                </div>
                <div className="artist">
                    <div className="left">
                        <div className="name">
                            <h2>Pitbull</h2>
                            <img src={Verification} alt="" />
                        </div>
                        <p><i><FaHeadphones /></i> 12,132,5478 <span>Monthly listeners</span></p>
                    </div>
                    <div className="right">
                        <a href="#">Play</a>
                        <a href="#"><i><FaCheck /></i>Following</a>
                    </div>
                </div>
            </div>
            <div className="bottom">
            </div>
        </div>
    )
}

export { Banner }