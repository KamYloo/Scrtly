import React, { useState } from 'react'
import axios from 'axios'
import logo from '../../img/logo.png'
import { FaUser, FaLock } from "react-icons/fa";
import { MdCancel } from "react-icons/md";
import '../../Styles/Login&Register.css'
import { Link, useNavigate } from 'react-router-dom'

function Login({ setFullName }) {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const navigate = useNavigate()

    const handleLogin = async (e) => {
        e.preventDefault()
        try {
            if (!email || !password) {
                setError('Please enter both username and password.')
                return
            }

            const response = await axios.post('http://localhost:8080/auth/login', { email, password })
            console.log('Login successful:', response.data)

            if (response.data.jwt) {
                localStorage.setItem('jwtToken', response.data.jwt); // Zapisz token JWT
                navigate('/home');
            } else {
                console.error('JWT not found in response');
            }
        } catch (error) {
            console.error('Login failed:', error.response ? error.response.data : error.message)
            setError('Invalid username or password.')
        }
    };

    return (
        <div className='login'>
            <div className="formBox login">
                <i className='cancel' ><Link to="/"><MdCancel /></Link></i>
                <form onSubmit={handleLogin}>
                    <div className="title">
                        <img src={logo} alt="" />
                        <h1>Login</h1>
                    </div>
                    <div className="inputBox">
                        <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder='Email address' required />
                        <FaUser className='icon' />
                    </div>
                    <div className="inputBox">
                        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} placeholder='Password' required />
                        <FaLock className='icon' />
                    </div>
                    {error && <p className="error">{error}</p>}
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