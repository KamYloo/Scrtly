import React, { useEffect, useState } from 'react'
import axios from 'axios';
import "../../Styles/Profile.css"
import { IoIosSettings } from "react-icons/io";

function ProfileInfo() {
    const [user, setUser] = useState({ fullName: '', email: '' });

    useEffect(() => {

        const token = localStorage.getItem('jwtToken')

        if (token) {
            axios.get('http://localhost:8080/api/users/profile', {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            })
                .then(response => {
                    setUser(response.data);
                })
                .catch(error => {
                    console.error("Error fetching user info:", error);
                });
        }
    }, []);

    return (
        <div className='profileInfo'>
            <div className="userData">
                <img src="" alt="" />
                <div className="right">
                    <div className="top">
                        <p>{user.fullName || 'Name Surname'}</p>
                        <button>Edit Profile</button>
                        <i><IoIosSettings /></i>
                    </div>
                    <div className="stats">
                        <p>Posts: 45</p>
                        <p>99 followers</p>
                        <p>Following: 155</p>
                    </div>
                    <div className="description">
                        <p>Name</p>
                        <span>gafgadhgdahfhgadvfavafdaerv</span>
                    </div>
                </div>
            </div>
        </div>
    )
}

export { ProfileInfo }