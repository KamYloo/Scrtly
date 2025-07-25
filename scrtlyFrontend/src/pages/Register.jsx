import React, {useState} from 'react'
import logo from '../assets/logo.png'
import {FaUser, FaLock, FaEnvelope} from "react-icons/fa";
import { MdCancel } from "react-icons/md";
import '../Styles/Login&Register.css'
import { Link, useNavigate } from 'react-router-dom'
import toast from "react-hot-toast";
import {useRegisterMutation} from "../Redux/services/authApi.js";

function Register() {
    const [formData, setFormData] = useState({
        fullName: '',
        nickName: '',
        email: '',
        password: '',
        confirmPassword: '',
    })
    const [errors, setErrors] = useState({})
    const navigate = useNavigate()

    const [register, { isLoading }] = useRegisterMutation()

    const handleChange = (e) => {
        const { name, value } = e.target
        setFormData((f) => ({ ...f, [name]: value }))
    }

    const validate = () => {
        const errs = {}
        if (!formData.fullName) errs.fullName = 'Full Name is required.'
        if (!formData.nickName) errs.nickName = 'NickName is required.'
        if (!formData.email) errs.email = 'Email is required.'
        else if (!/^\S+@\S+\.\S+$/.test(formData.email))
            errs.email = 'Invalid email format.'
        if (!formData.password) errs.password = 'Password is required.'
        else if (formData.password.length < 6)
            errs.password = 'Password must be at least 6 characters.'
        if (formData.password !== formData.confirmPassword)
            errs.confirmPassword = 'Passwords do not match.'
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
            await register(formData).unwrap()
            toast.success('You have registered successfully.')
            navigate('/login')
        } catch (err) {
            toast.error(err.data.businessErrornDescription)
        }
    }

    return (
        <div className='login r'>
            <div className="formBox register">
                <i className='cancel'><Link to="/"><MdCancel/></Link></i>
                <form onSubmit={handleSubmit}>
                    <div className="title">
                        <img src={logo} alt=""/>
                        <h1>Registration</h1>
                    </div>
                    <div className="inputBox">
                        <input type="text" value={formData.fullName} name="fullName"
                               onChange={(e) => handleChange(e)} placeholder='FullName' required/>
                        <FaUser className='icon'/>
                        {errors.fullName && <p className="error">{errors.fullName}</p>}
                    </div>
                    <div className="inputBox">
                        <input type="text" value={formData.nickName} name="nickName"
                               onChange={(e) => handleChange(e)} placeholder='NickName' required/>
                        <FaUser className='icon'/>
                        {errors.nickName && <p className="error">{errors.nickName}</p>}
                    </div>
                    <div className="inputBox">
                        <input type="email" value={formData.email} name="email"
                               onChange={(e) => handleChange(e)} placeholder='Email' required/>
                        <FaEnvelope className='icon'/>
                        {errors.email && <p className="error">{errors.email}</p>}
                    </div>
                    <div className="inputBox">
                        <input type="password" value={formData.password} name="password"
                               onChange={(e) => handleChange(e)} placeholder='Password' required/>
                        <FaLock className='icon'/>
                        {errors.password && <p className="error">{errors.password}</p>}
                    </div>
                    <div className="inputBox">
                        <input type="password" value={formData.confirmPassword} name="confirmPassword"
                               onChange={(e) => handleChange(e)} placeholder='Confirm Password' required/>
                        <FaLock className='icon'/>
                        {errors.confirmPassword && <p className="error">{errors.confirmPassword}</p>}
                    </div>

                    <button type="submit" disabled={isLoading}>
                        {isLoading ? 'Registering…' : 'Register'}
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
