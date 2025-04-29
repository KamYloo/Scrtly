import React, {useEffect, useState} from 'react'
import logo from '../assets/logo.png'
import {FaUser, FaLock, FaEnvelope} from "react-icons/fa";
import { MdCancel } from "react-icons/md";
import '../Styles/Login&Register.css'
import { Link, useNavigate } from 'react-router-dom'
import {useDispatch, useSelector} from "react-redux";
import {registerAction} from "../Redux/AuthService/Action.js";
import toast from "react-hot-toast";

function Register() {
    const dispatch = useDispatch()
    const navigate = useNavigate()
    const { loading, error, registerResponse } = useSelector(s => s.auth)

    const [inputData, setInputData] = useState({ fullName: '', nickName: '', email: '', password: '', confirmPassword: '' })
    const [formErrors, setFormErrors] = useState({})

    const handleChange = (e) => {
        const { name, value } = e.target;
        setInputData((values) => ({ ...values, [name]: value }));
    };

    useEffect(() => {
        if (error) {
            toast.error(error)
        }
    }, [error])

    useEffect(() => {
        if (registerResponse) {
            toast.success('You have registered successfully.')
            navigate('/login')
        }
    }, [registerResponse, navigate])

    const validate = () => {
        const errs = {}
        if (!inputData.fullName) errs.fullName = 'Full Name is required.'
        if (!inputData.nickName) errs.nickName = 'NickName is required.'
        if (!inputData.email) errs.email = 'Email is required.'
        if (inputData.email && !/^\S+@\S+\.\S+$/.test(inputData.email)) errs.email = 'Invalid email format.'
        if (!inputData.password) errs.password = 'Password is required.'
        if (inputData.password && inputData.password.length < 6) errs.password = 'Password must be at least 6 characters.'
        if (inputData.password !== inputData.confirmPassword) errs.confirmPassword = 'Passwords do not match.'
        return errs
    }

    const handleSignup = (e) => {
        e.preventDefault()
        const errs = validate()
        if (Object.keys(errs).length) {
            setFormErrors(errs)
        } else {
            dispatch(registerAction(inputData))
        }
    }

    return (
        <div className='login r'>
            <div className="formBox register">
                <i className='cancel'><Link to="/"><MdCancel/></Link></i>
                <form onSubmit={handleSignup}>
                    <div className="title">
                        <img src={logo} alt=""/>
                        <h1>Registration</h1>
                    </div>
                    <div className="inputBox">
                        <input type="text" value={inputData.fullName} name="fullName"
                               onChange={(e) => handleChange(e)} placeholder='FullName' required/>
                        <FaUser className='icon'/>
                        {formErrors.fullName && <p className="error">{formErrors.fullName}</p>}
                    </div>
                    <div className="inputBox">
                        <input type="text" value={inputData.nickName} name="nickName"
                               onChange={(e) => handleChange(e)} placeholder='NickName' required/>
                        <FaUser className='icon'/>
                        {formErrors.nickName && <p className="error">{formErrors.nickName}</p>}
                    </div>
                    <div className="inputBox">
                        <input type="email" value={inputData.email} name="email"
                               onChange={(e) => handleChange(e)} placeholder='Email' required/>
                        <FaEnvelope className='icon'/>
                        {formErrors.email && <p className="error">{formErrors.email}</p>}
                    </div>
                    <div className="inputBox">
                        <input type="password" value={inputData.password} name="password"
                               onChange={(e) => handleChange(e)} placeholder='Password' required/>
                        <FaLock className='icon'/>
                        {formErrors.password && <p className="error">{formErrors.password}</p>}
                    </div>
                    <div className="inputBox">
                        <input type="password" value={inputData.confirmPassword} name="confirmPassword"
                               onChange={(e) => handleChange(e)} placeholder='Confirm Password' required/>
                        <FaLock className='icon'/>
                        {formErrors.confirmPassword && <p className="error">{formErrors.confirmPassword}</p>}
                    </div>

                    <button type="submit" disabled={loading}>
                        {loading ? 'Registering…' : 'Register'}
                    </button>
                </form>
                <div className="registerLink">
                    <p>Already have an account? <Link to="/login">Login</Link></p>
                </div>
            </div>
        </div>
    );
}

export {Register};
