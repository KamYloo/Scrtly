import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';

import { LeftMenu } from './Components/LeftMenu';
import { RightMenu } from './Components/RightMenu';
import { MobileNav } from './Components/MobileNav';
import NotificationsListener from './features/Notification/NotificationsListener.jsx';

import { Home } from './pages/Home.jsx';
import { ChatView } from './pages/ChatView.jsx';
import { ArtistsView } from './pages/ArtistsView.jsx';
import { Artist } from './features/Artist/Artist.jsx';
import { AlbumsView } from './pages/AlbumsView.jsx';
import { Album } from './features/Album/Album.jsx';
import { PlayList } from './features/PlayList/PlayList.jsx';
import { PlayListForm } from './features/PlayList/PlayListForm.jsx';
import { Discover } from './pages/Discover.jsx';
import { Profile } from './features/User/Profile.jsx';
import { ProfileEdit } from './features/User/ProfileEdit.jsx';

import { Login } from './pages/Login.jsx';
import { Register } from './pages/Register.jsx';
import { ForgotPassword } from './pages/ForgotPassword.jsx';
import { ChangePassword } from './pages/ChangePassword.jsx';

function AppRoutes() {
    const [createPlayList, setCreatePlayList] = useState(false);
    const [volume, setVolume] = useState(0.5);
    const [currentTrack, setCurrentTrack] = useState({
        songName: 'Default Song',
        artist: 'Default Artist',
    });
    const [isMobile, setIsMobile] = useState(window.innerWidth <= 748);

    useEffect(() => {
        const handleResize = () => setIsMobile(window.innerWidth <= 748);
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    const handleTrackChange = (songName, artist) => {
        setCurrentTrack({ songName, artist });
    };

    const renderLayout = (Component, props = {}) => (
        <div className={`App ${isMobile ? 'mobile' : ''}`}>
            {!isMobile && (
                <LeftMenu
                    onVolumeChange={setVolume}
                    currentTrack={currentTrack}
                    setCreatePlayList={setCreatePlayList}
                />
            )}
            <Component
                volume={volume}
                onTrackChange={handleTrackChange}
                {...props}
            />
            {!isMobile && <RightMenu />}
            {isMobile && <MobileNav setCreatePlayList={setCreatePlayList}/>}
            {createPlayList && (
                <PlayListForm onClose={() => setCreatePlayList(prev => !prev)} />
            )}
            <div className="background" />
        </div>
    );

    return (
        <Router>
            <NotificationsListener />
            <Routes>
                <Route
                    path="/login"
                    element={
                        <div className="loginLayout">
                            <Login />
                            <div className="background" />
                        </div>
                    }
                />
                <Route
                    path="/register"
                    element={
                        <div className="loginLayout">
                            <Register />
                            <div className="background" />
                        </div>
                    }
                />
                <Route
                    path="/forgot-password"
                    element={
                        <div className="loginLayout">
                            <ForgotPassword />
                            <div className="background" />
                        </div>
                    }
                />
                <Route
                    path="/change-password/:userId/:token"
                    element={
                        <div className="loginLayout">
                            <ChangePassword />
                            <div className="background" />
                        </div>
                    }
                />

                <Route path="/home" element={renderLayout(Home)} />
                <Route path="/chat" element={renderLayout(ChatView)} />
                <Route path="/artists" element={renderLayout(ArtistsView)} />
                <Route
                    path="/artist/:artistId/*"
                    element={renderLayout(Artist)}
                />
                <Route path="/albums" element={renderLayout(AlbumsView)} />
                <Route
                    path="/album/:albumId"
                    element={renderLayout(Album)}
                />
                <Route
                    path="/playList/:playListId"
                    element={renderLayout(PlayList)}
                />
                <Route path="/discover" element={renderLayout(Discover)} />
                <Route
                    path="/profile/:nickName"
                    element={renderLayout(Profile)}
                />
                <Route
                    path="/profile/edit"
                    element={renderLayout(ProfileEdit)}
                />

                <Route path="/" element={<Navigate to="/home" replace />} />
            </Routes>

            <Toaster position="bottom-right" reverseOrder={false} />
        </Router>
    );
}

export default AppRoutes;
