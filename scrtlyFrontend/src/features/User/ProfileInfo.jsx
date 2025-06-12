import React, { useEffect, useState } from 'react';
import "../../Styles/Profile.css";
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import { findUser, followUser } from "../../Redux/AuthService/Action.js";
import { useDispatch, useSelector } from "react-redux";
import Spinner from "../../Components/Spinner.jsx";
import ErrorOverlay from "../../Components/ErrorOverlay.jsx";
import ArtistVerificationModal from "./ArtistVerificationModal.jsx";
import defaultAvatar from "../../assets/user.jpg";


function ProfileInfo() {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const { nickName } = useParams();
    const { auth, post } = useSelector(state => state);
    const location = useLocation();
    const [showVerifyModal, setShowVerifyModal] = useState(false);

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
        return <ErrorOverlay message={auth.error} />;
    }

    return (
        <div className='profileInfo'>
            <div className="userData">
                <img
                    src={auth.findUser?.profilePicture || defaultAvatar}
                    alt={auth.findUser?.fullName || 'Profile Picture'}
                />
                <div className="right">
                    <div className="top">
                        <p>{auth.findUser?.fullName || 'Name Surname'}</p>
                        {auth.findUser?.id !== auth.reqUser?.id && (
                            <button className={auth.findUser?.observed ? 'following' : 'follow'}
                                    onClick={() => dispatch(followUser(auth.findUser?.id))}>
                                {auth.findUser?.observed ? 'unFollow' : 'Follow'}
                            </button>
                        )}
                        {auth.findUser?.id === auth.reqUser?.id && (
                            <>
                                <button onClick={() => navigate("/profile/edit")}>
                                    Edit Profile
                                </button>
                                <button onClick={() => setShowVerifyModal(true)} style={{ marginLeft: '10px' }}>
                                    Verify
                                </button>
                            </>
                        )}
                    </div>
                    <div className="stats">
                        <p>Posts: {post.posts.content.length}</p>
                        <p>{auth.findUser?.observersCount} Followers</p>
                        <p>Following: {auth.findUser?.observationsCount}</p>
                    </div>
                    <div className="description">
                        <p>{auth.findUser?.fullName || 'Name'}</p>
                        <span>{auth.findUser?.description || ''}</span>
                    </div>
                </div>
            </div>
            {showVerifyModal && <ArtistVerificationModal onClose={() => setShowVerifyModal(false)} />}
        </div>
    );
}

export { ProfileInfo };
