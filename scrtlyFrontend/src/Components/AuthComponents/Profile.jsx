import React from 'react'
import "../../Styles/Profile.css"
import { ProfileInfo } from './ProfileInfo'

function Profile({auth, token}) {
    return (
        <div className='profileSite'>
            <ProfileInfo auth={auth} token={token} />
            <hr />
            <div className="posts">
                <div className="post">
                    <img src="" alt="" />
                </div>
                <div className="post">
                    <img src="" alt="" />
                </div>
                <div className="post">
                    <img src="" alt="" />
                </div>
                <div className="post">
                    <img src="" alt="" />
                </div>
                <div className="post">
                    <img src="" alt="" />
                </div>
                <div className="post">
                    <img src="" alt="" />
                </div>
                <div className="post">
                    <img src="" alt="" />
                </div><div className="post">
                    <img src="" alt="" />
                </div><div className="post">
                    <img src="" alt="" />
                </div><div className="post">
                    <img src="" alt="" />
                </div><div className="post">
                    <img src="" alt="" />
                </div><div className="post">
                    <img src="" alt="" />
                </div><div className="post">
                    <img src="" alt="" />
                </div><div className="post">
                    <img src="" alt="" />
                </div><div className="post">
                    <img src="" alt="" />
                </div><div className="post">
                    <img src="" alt="" />
                </div>
                
            </div>
        </div>
    )
}

export { Profile }