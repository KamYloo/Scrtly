import React, { useState } from 'react'
import axios from "axios";
import logo from '../../img/logo.png'
import { FaUser, FaLock, FaEnvelope } from "react-icons/fa";
import { MdCancel } from "react-icons/md";
import '../../Styles/Login&Register.css'
import { Link, useNavigate } from 'react-router-dom'


function Register() {

    const [fullName, setfullName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('')
    const [error, setError] = useState('') // State to manage error messages 
    const navigate = useNavigate()

    const handleSignup = async (e) => {
        e.preventDefault()
        try {
            // Check for empty fields 
            if (!fullName || !email || !password || !confirmPassword) {
                setError('Please fill in all fields.')
                return;
            }

            if (password !== confirmPassword) {
                throw new Error("Passwords do not match")
            }

            const response = await axios.post('http://localhost:8080/auth/register', {
                fullName,
                email,
                password,
            })

            // Handle successful signup 
            console.log(response.data)
            navigate('/login')
        } catch (error) {
            // Handle signup error 
            console.error('Signup failed:', error.response ? error.response.data : error.message)
            setError(error.response ? error.response.data : error.message)
        }
    };

    return (
        <div className='login r'>
            <div className="formBox register">
                <i className='cancel' ><Link to="/"><MdCancel /></Link></i>
                <form onSubmit={handleSignup}>
                    <div className="title">
                        <img src={logo} alt="" />
                        <h1>Registration</h1>
                    </div>
                    <div className="inputBox">
                        <input type="text" value={fullName}
                            onChange={(e) => setfullName(e.target.value)} placeholder='fullName' required />
                        <FaUser className='icon' />
                    </div>
                    <div className="inputBox">
                        <input type="email" value={email}
                            onChange={(e) => setEmail(e.target.value)} placeholder='Email' required />
                        <FaEnvelope className='icon' />
                    </div>
                    <div className="inputBox">
                        <input type="password" value={password}
                            onChange={(e) => setPassword(e.target.value)} placeholder='Password' required />
                        <FaLock className='icon' />
                    </div>
                    <div className="inputBox">
                        <input type="password" value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)} placeholder='Confirm Password' required />
                        <FaLock className='icon' />
                    </div>
                    {error && <p className="error">{error}</p>} {/* Wyświetlanie błędów */}
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