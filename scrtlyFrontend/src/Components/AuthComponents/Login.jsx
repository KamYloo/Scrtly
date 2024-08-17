import React from 'react'
import logo from '../../img/logo.png'
import { FaUser, FaLock } from "react-icons/fa";
import { MdCancel } from "react-icons/md";
import '../../Styles/Login&Register.css'
import { Link } from 'react-router-dom'

function Login() {
    return (
        <div className='login'>
            <div className="formBox login">
                <i className='cancel' ><Link to="/"><MdCancel /></Link></i>
                <form action="">
                    <div className="title">
                        <img src={logo} alt="" />
                        <h1>Login</h1>
                    </div>
                    <div className="inputBox">
                        <input type="text" placeholder='Username' required />
                        <FaUser className='icon' />
                    </div>
                    <div className="inputBox">
                        <input type="text" placeholder='Password' required />
                        <FaLock className='icon' />
                    </div>
                    <button type='submit'>Login</button>
                    <div className="registerLink">
                        <p>Don't have an account? <Link to="/register">Register</Link></p>
                    </div>
                </form>
            </div>
        </div>
    )
}

export { Login }