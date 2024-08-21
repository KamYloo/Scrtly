import React, { useEffect, useState } from 'react'
import { FaUserEdit, FaEllipsisH, FaAngleRight } from "react-icons/fa";
import { BsCameraVideoFill } from "react-icons/bs";
import axios from 'axios';

function UserInfo({ toggleChatListView }) {
    const [user, setUser] = useState({ fullName: '', email: '' });

    useEffect(() => {
        // Pobierz token JWT z localStorage lub innego miejsca, gdzie go przechowujesz
        const token = localStorage.getItem('jwtToken');

        // Jeśli token istnieje, wyślij żądanie do backendu
        if (token) {
            axios.get('http://localhost:8080/auth/me', {
                headers: {
                    Authorization: `Bearer ${token}`  // Dodaj token do nagłówka
                }
            })
                .then(response => {
                    // Zaktualizuj stan z danymi użytkownika
                    setUser(response.data);
                })
                .catch(error => {
                    console.error("Error fetching user info:", error);
                });
        }
    }, []);

    return (
        <div className='userInfo'>
            <i className='chatListBtn' onClick={toggleChatListView}><FaAngleRight /></i>
            <div className="user">
                <img src="#" alt="" />
                <h2>{user.fullName || 'Name Surname'}</h2>
            </div>
            <div className="icons">
                <i><FaEllipsisH /></i>
                <i><BsCameraVideoFill /></i>
                <i><FaUserEdit /></i>
            </div>
        </div>
    )
}

export { UserInfo }