import React, { useState, useEffect } from 'react';
import './App.css';
import { Toaster } from "react-hot-toast";
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import { LeftMenu } from './Components/LeftMenu';
import { RightMenu } from './Components/RightMenu';
import { MobileNav } from './Components/MobileNav';
import { Artist } from './features/Artist/Artist.jsx';
import { ChatView } from './pages/ChatView.jsx';
import { Login } from './pages/Login.jsx';
import { Register } from './pages/Register.jsx';
import { Profile } from './features/User/Profile';
import { ArtistsView } from './pages/ArtistsView.jsx';
import { Discover } from './pages/Discover.jsx';
import { ProfileEdit } from "./features/User/ProfileEdit.jsx";
import { AlbumsView } from "./pages/AlbumsView.jsx";
import { Album } from "./features/Album/Album.jsx";
import { PlayListForm } from "./features/PlayList/PlayListForm.jsx";
import { PlayList } from "./features/PlayList/PlayList.jsx";
import { Home } from "./pages/Home.jsx";
import NotificationsListener from "./features/Notification/NotificationsListener.jsx";
import { ChangePassword } from "./pages/ChangePassword.jsx";
import { ForgotPassword } from "./pages/ForgotPassword.jsx";

function App() {
  const [createPlayList, setCreatePlayList] = useState(false);
  const [volume, setVolume] = useState(0.5);
  const [currentTrack, setCurrentTrack] = useState({ songName: 'Default Song', artist: 'Default Artist' });
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 748);

  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth <= 748);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const handleTrackChange = (songName, artist) => {
    setCurrentTrack({ songName, artist });
  };

  const renderLayout = (Component, props) => {
    return (
        <div className={`App ${isMobile ? 'mobile' : ''}`}>
          {!isMobile &&
              <LeftMenu onVolumeChange={setVolume} currentTrack={currentTrack} setCreatePlayList={setCreatePlayList} />}
          <Component volume={volume} onTrackChange={handleTrackChange} {...props} />
          {!isMobile && <RightMenu />}
          {isMobile && <MobileNav />}
          {createPlayList && <PlayListForm onClose={() => setCreatePlayList(prev => !prev)} />}
          <div className="background"></div>
        </div>
    );
  };

  return (
      <Router>
        <NotificationsListener/>
        <Routes>
          <Route path="/login" element={
            <div className='loginLayout'><Login />
            <div className="background"></div>
          </div>}
        />

        <Route path="/register" element={
          <div className="loginLayout"><Register />
            <div className="background"></div>
          </div>
        } />

        <Route path="/change-password/:userId/:token" element={
          <div className="loginLayout"><ChangePassword />
            <div className="background"></div>
          </div>
        } />

        <Route path="/forgot-password" element={
          <div className='loginLayout'>
            <ForgotPassword />
            <div className="background"></div>
          </div>
        } />

        <Route path="/home" element={
          renderLayout(Home, { setVolume, currentTrack, volume, handleTrackChange })
        } />

        <Route path="/chat" element={
          renderLayout(ChatView, { setVolume, currentTrack })
        } />

        <Route path="/artists" element={
          renderLayout(ArtistsView, { setVolume, currentTrack })
        } />

        <Route path="/artist/:artistId/*" element={
          renderLayout(Artist, { setVolume, currentTrack, volume, handleTrackChange })
        } />

        <Route path="/albums" element={
          renderLayout(AlbumsView, { setVolume, currentTrack })
        } />

        <Route path="/album/:albumId" element={
          renderLayout(Album, { setVolume, currentTrack, volume, handleTrackChange })
        } />

        <Route path="/playList/:playListId" element={
          renderLayout(PlayList, { setVolume, currentTrack, volume, handleTrackChange })
        } />

        <Route path="/discover" element={
          renderLayout(Discover, { setVolume, currentTrack })
        } />

        <Route path="/profile/:nickName" element={
          renderLayout(Profile, { setVolume, currentTrack })
        } />

        <Route path="/profile/edit" element={
          renderLayout(ProfileEdit, { setVolume, currentTrack })
        } />

        <Route path="/" element={<Navigate to="/home" />} />
      </Routes>
      <Toaster position="bottom-right" reverseOrder={false} />
    </Router>
  );
}

export default App;