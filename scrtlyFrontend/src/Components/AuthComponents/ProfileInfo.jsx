import React, { useEffect } from 'react'
import "../../Styles/Profile.css"
import { IoIosSettings } from "react-icons/io";
import {useNavigate, useParams} from 'react-router-dom';
import {BASE_API_URL} from "../../config/api.js";
import {findUserById, followUser} from "../../Redux/Auth/Action.js";
import {useDispatch, useSelector} from "react-redux";


function ProfileInfo() {
    const navigate = useNavigate()
    const dispatch = useDispatch()
    const {userId} = useParams();

    const {auth} = useSelector(store => store);

    useEffect(() => {
        dispatch(findUserById(userId))
    }, [userId]);
    return (
        <div className='profileInfo'>
            <div className="userData">
                <img src={`${BASE_API_URL}/${auth.findUser?.profilePicture || ''}`} alt="" />
                <div className="right">
                    <div className="top">
                        <p>{auth.findUser?.fullName || 'Name Surname'}</p>
                        {!auth.findUser?.req_user &&
                            (<button className={auth.findUser?.followed ? 'following' : 'follow'} onClick={() => {
                           dispatch(followUser(userId))
                        }}>{auth.findUser?.followed ? 'unFollow' : 'Follow'}
                        </button>)}
                        {auth.findUser?.req_user &&
                            (
                        <button onClick={() => {
                            navigate("/profile/edit")
                        }}>Edit Profile
                        </button>)}
                        <i><IoIosSettings/></i>
                    </div>
                    <div className="stats">
                        <p>Posts: 45</p>
                        <p>{auth.findUser?.totalFollowers} Followers</p>
                        <p>Following: {auth.findUser?.totalFollowing}</p>
                    </div>
                    <div className="description">
                        <p>{auth.findUser?.fullName || 'Name'}</p>
                        <span>{auth.findUser?.description || ''}</span>
                    </div>
                </div>
            </div>
        </div>
    )
}

export { ProfileInfo }