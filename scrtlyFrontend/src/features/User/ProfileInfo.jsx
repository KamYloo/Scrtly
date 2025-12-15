import React, { useState } from 'react';
import "../../Styles/Profile.css";
import { useNavigate, useParams } from 'react-router-dom';
import Spinner from "../../Components/Spinner.jsx";
import ErrorOverlay from "../../Components/ErrorOverlay.jsx";
import ArtistVerificationModal from "./ArtistVerificationModal.jsx";
import defaultAvatar from "../../assets/user.jpg";
import {useFindUserQuery, useFollowUserMutation} from "../../Redux/services/userApi.js";
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";
import {useGetPostsByUserQuery} from "../../Redux/services/postApi.js";


function ProfileInfo() {
    const navigate = useNavigate();
    const { nickName } = useParams();
    const [showVerifyModal, setShowVerifyModal] = useState(false);
    const [followUser] = useFollowUserMutation();
    const {
        data: viewedUser,
        isLoading: viewingLoading,
        isError: viewingError,
        error: viewingErrorData,
    } = useFindUserQuery(nickName, {
        skip: !nickName,
    });
    const { data: reqUser } = useGetCurrentUserQuery(null, {
        skip: !localStorage.getItem('isLoggedIn'),
    });

    const {
        data: postsPage,
    } = useGetPostsByUserQuery(
        { nickName, page: 0, size: 1 },
        { skip: !nickName }
    );

    const handleFollow = async () => {
        await followUser(viewedUser.id).unwrap();
    };

    if (viewingLoading) {
        return <Spinner />;
    }
    if (viewingError) {
        return <ErrorOverlay error={viewingErrorData?.data} />;
    }

    return (
        <div className='profileInfo'>
            <div className="userData">
                <img
                    src={viewedUser?.profilePicture || defaultAvatar}
                    alt={viewedUser?.fullName || 'Profile Picture'}
                    onClick={() => {
                        if (viewedUser?.isArtist) {
                            navigate(`/artist/${viewedUser.id}`);
                        }
                    }}
                    style={{
                        cursor: viewedUser?.isArtist ? 'pointer' : 'default'
                    }}
                />
                <div className="right">
                    <div className="top">
                        <p>{viewedUser?.fullName || 'Name Surname'}</p>
                        {viewedUser?.id !== reqUser?.id && (
                            <button className={viewedUser?.observed ? 'following' : 'follow'}
                                    onClick={handleFollow}>
                                {viewedUser?.observed ? 'unFollow' : 'Follow'}
                            </button>
                        )}
                        {viewedUser?.id === reqUser?.id && (
                            <>
                                <button onClick={() => navigate("/profile/edit")}>
                                    Edit Profile
                                </button>
                                {!viewedUser?.isArtist && (
                                    <button onClick={() => setShowVerifyModal(true)} style={{marginLeft: '10px'}}>
                                        Verify
                                    </button>)}
                            </>
                        )}
                    </div>
                    <div className="stats">
                        <p>Posts: {postsPage?.totalElements ?? 0}</p>
                        <p>{viewedUser?.observersCount} Followers</p>
                        <p>Following: {viewedUser?.observationsCount}</p>
                    </div>
                    <div className="description">
                        <p>{viewedUser?.fullName || 'Name'}</p>
                        <span>{viewedUser?.description || ''}</span>
                    </div>
                </div>
            </div>
            {showVerifyModal && <ArtistVerificationModal onClose={() => setShowVerifyModal(false)}/>}
        </div>
    );
}

export {ProfileInfo};
