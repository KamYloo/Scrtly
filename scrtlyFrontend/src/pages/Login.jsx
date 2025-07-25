import React, { useState } from 'react'
import logo from '../assets/logo.png'
import { FaUser, FaLock } from "react-icons/fa";
import { MdCancel } from "react-icons/md";
import '../Styles/Login&Register.css'
import { Link, useNavigate } from 'react-router-dom'
import toast from "react-hot-toast";
import {FcGoogle} from "react-icons/fc";
import {BASE_API_URL} from "../Redux/api.js";
import {useLoginMutation} from "../Redux/services/authApi.js";

function Login() {
    const [formData, setFormData] = useState({ email: '', password: '' })
    const [errors, setErrors] = useState({})
    const navigate = useNavigate();

    const [login, { isLoading }] =
        useLoginMutation()

    const handleChange = (e) => {
        const { name, value } = e.target
        setFormData((f) => ({ ...f, [name]: value }))
    }

    const validate = () => {
        const errs = {}
        if (!formData.email) errs.email = 'Email is required.'
        else if (!/^\S+@\S+\.\S+$/.test(formData.email))
            errs.email = 'Invalid email format.'
        if (!formData.password) errs.password = 'Password is required.'
        return errs
    }

    const handleSubmit = async (e) => {
        e.preventDefault()
        const v = validate()
        if (Object.keys(v).length) {
            setErrors(v)
            return
        }
        try {
            await login(formData).unwrap()
            toast.success('You have logged in successfully.')
            localStorage.setItem('isLoggedIn', '1')
            navigate('/home')
        } catch (err) {
            toast.error(err.data.businessErrornDescription);
        }
    }

    return (
        <div className='login'>
            <div className="formBox login">
                <i className='cancel'><Link to="/"><MdCancel /></Link></i>
                <form onSubmit={handleSubmit}>
                    <div className="title">
                        <img src={logo} alt="Logo"/>
                        <h1>Login</h1>
                    </div>
                    <div className="inputBox">
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            placeholder='Email address'
                            required
                        />
                        <FaUser className='icon'/>
                        {errors.email && <p className="error">{errors.email}</p>}
                    </div>
                    <div className="inputBox">
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder='Password'
                            required
                        />
                        <FaLock className='icon'/>
                        {errors.password && <p className="error">{errors.password}</p>}
                    </div>
                    <div className="button-group">
                        <button type='submit' disabled={isLoading } className="login-btn">
                            {isLoading  ? 'Logging in…' : 'Login'}
                        </button>
                        <button
                            type="button"
                            className="google-btn"
                            onClick={() => {
                                window.location.href = `${BASE_API_URL}/oauth2/authorize/google`;
                            }}
                        >
                            <FcGoogle size={20} style={{marginRight: '8px'}}/>
                            Sign in with Google
                        </button>
                    </div>
                    <div className="forgotLink">
                        <p>Don't remember the password? <Link to="/forgot-password">Change Password</Link></p>
                    </div>
                    <div className="registerLink">
                        <p>Don't have an account? <Link to="/register">Register</Link></p>
                    </div>
                </form>
            </div>
        </div>
    );
}

export {Login};
