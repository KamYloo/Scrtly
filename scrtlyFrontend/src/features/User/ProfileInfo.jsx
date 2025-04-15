import React, { useEffect } from 'react';
import "../../Styles/Profile.css";
import { IoIosSettings } from "react-icons/io";
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import {findUser, followUser} from "../../Redux/AuthService/Action.js";
import { useDispatch, useSelector } from "react-redux";
import Spinner from "../../Components/Spinner.jsx";
import ErrorAlert from "../../Components/ErrorAlert.jsx";

function ProfileInfo() {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const { nickName } = useParams();
    const { auth } = useSelector(state => state);
    const location = useLocation();
    const userData = (() => { try { return JSON.parse(localStorage.getItem("user")) || null; } catch { return null; } })();

    useEffect(() => {
        dispatch(findUser(nickName));
    }, [dispatch, nickName]);


    useEffect(() => {
        const searchParams = new URLSearchParams(location.search);
        if (searchParams.get('reload')) {
            dispatch(findUser(nickName));
        }
    }, [location, dispatch, nickName]);

    if (auth.loading) {
        return <Spinner />;
    }
    if (auth.error) {
        return <ErrorAlert message={auth.error} />;
    }

    return (
        <div className='profileInfo'>
            <div className="userData">
                <img
                    src={auth.findUser?.profilePicture || ''}
                    alt={auth.findUser?.fullName || 'Profile Picture'}
                />
                <div className="right">
                    <div className="top">
                        <p>{auth.findUser?.fullName || 'Name Surname'}</p>
                        {auth.findUser?.id !== userData?.id && (
                            <button className={auth.findUser?.observed ? 'following' : 'follow'}
                                    onClick={() => dispatch(followUser(auth.findUser?.id))}>
                                {auth.findUser?.observed ? 'unFollow' : 'Follow'}
                            </button>
                        )}
                        {auth.findUser?.id === userData?.id && (
                            <button onClick={() => navigate("/profile/edit")}>
                                Edit Profile
                            </button>
                        )}
                        <i><IoIosSettings/></i>
                    </div>
                    <div className="stats">
                        <p>Posts: 45</p>
                        <p>{auth.findUser?.observersCount} Followers</p>
                        <p>Following: {auth.findUser?.observationsCount}</p>
                    </div>
                    <div className="description">
                        <p>{auth.findUser?.fullName || 'Name'}</p>
                        <span>{auth.findUser?.description || ''}</span>
                    </div>
                </div>
            </div>
        </div>
    );
}

export { ProfileInfo };
