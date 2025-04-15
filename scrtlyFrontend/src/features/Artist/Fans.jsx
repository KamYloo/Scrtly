import React from 'react'
import { FaUser } from "react-icons/fa";
import {BASE_API_URL} from "../../config/api.js";
import Verification from "../../assets/check.png";
import {useNavigate} from "react-router-dom";

// eslint-disable-next-line react/prop-types
function Fans({fans}) {
    const navigate = useNavigate();
    return (
        <div className='fans'>
            <div className="users">
                { fans?.map((item) => (
                    <div className="user" key={item.id} onClick={() => navigate(`/profile/${item.id}`)}>
                        <i className="push"><FaUser/></i>
                        <div className="imgPic">
                            <img src={`${BASE_API_URL}/${item.profilePicture || ''}`} alt=""/>
                            <img className='check' src={Verification} alt=""/>
                        </div>
                        <p>{item?.fullName}</p>
                    </div>
                ))}
            </div>
        </div>
    )
}

export {Fans}