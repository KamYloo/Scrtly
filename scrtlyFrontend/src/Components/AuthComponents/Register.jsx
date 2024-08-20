import React, { useState } from 'react'
import axios from "axios";
import logo from '../../img/logo.png'
import { FaUser, FaLock, FaEnvelope } from "react-icons/fa";
import { MdCancel } from "react-icons/md";
import '../../Styles/Login&Register.css'
import { Link } from 'react-router-dom'

function Register() {
    const [user, setUser] = useState({
        username: "",
        password: "",
        firstName: "",
        lastName: "",
        email: "",
    });

    const handleChange = (e) => {
        setUser({
            ...user,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post("api/auth/register", user);
            alert(response.data); // Wyświetlenie komunikatu o sukcesie
        } catch (error) {
            console.error("Error registering user:", error.response.data);
            alert("Error registering user");
        }
    };

    return (
        <div className='login'>
            <div className="formBox register">
                <i className='cancel' ><Link to="/"><MdCancel /></Link></i>
                <form onSubmit={handleSubmit}>
                    <div className="title">
                        <img src={logo} alt="" />
                        <h1>Registration</h1>
                    </div>
                    <div className="inputBox">
                        <input type="text" name="username" value={user.username}
                            onChange={handleChange} placeholder='Username' required />
                        <FaUser className='icon' />
                    </div>
                    <div className="inputBox">
                        <input type="email" name="email" value={user.email}
                            onChange={handleChange} placeholder='Email' required />
                        <FaEnvelope className='icon' />
                    </div>
                    <div className="inputBox">
                        <input type="password" name="password" value={user.password}
                            onChange={handleChange} placeholder='Password' required />
                        <FaLock className='icon' />
                    </div>
                    <button type='submit'>Register</button>
                    <div className="registerLink">
                        <p>Already have an account? <Link to="/login">Login</Link></p>
                    </div>
                </form>
            </div>
        </div>
    )
}

export { Register }