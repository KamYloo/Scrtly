import React, {useEffect, useState} from 'react'
import logo from '../../img/logo.png'
import {FaUser, FaLock, FaEnvelope, FaMusic} from "react-icons/fa";
import { MdCancel } from "react-icons/md";
import '../../Styles/Login&Register.css'
import { Link, useNavigate } from 'react-router-dom'
import {useDispatch, useSelector} from "react-redux";
import {currentUser, register} from "../../Redux/Auth/Action.js";


function Register() {
    const [inputData, setInputData] = useState({fullName: "",email: "", password: "", confirmPassword: "", role: "", artistName: ""})
    const {auth} = useSelector(store => store)
    const token = localStorage.getItem('token')
    const dispatch = useDispatch();
    const navigate = useNavigate()

    const handleChange = (e) => {
        const { name, value } = e.target;
        setInputData((values)=>({...values, [name]: value}))
    }

    const handleSignup = async (e) => {
        e.preventDefault()
        dispatch(register(inputData))
    };

    useEffect(() => {
        if (token)dispatch(currentUser(token))
    }, [token])

    useEffect(() => {
        if(auth.reqUser?.fullName) {
            navigate("/login")
        }
    }, [auth.reqUser])

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
                               onChange={(e) => handleChange(e)} placeholder='fullName' required/>
                        <FaUser className='icon'/>
                    </div>
                    <div className="inputBox">
                        <input type="email" value={inputData.email} name="email"
                               onChange={(e) => handleChange(e)} placeholder='Email' required/>
                        <FaEnvelope className='icon'/>
                    </div>
                    <div className="inputBox">
                        <input type="password" value={inputData.password} name="password"
                               onChange={(e) => handleChange(e)} placeholder='Password' required/>
                        <FaLock className='icon'/>
                    </div>
                    <div className="inputBox">
                        <input type="password" value={inputData.confirmPassword} name="confirmPassword"
                               onChange={(e) => handleChange(e)} placeholder='Confirm Password' required/>
                        <FaLock className='icon'/>
                    </div>
                    <div className="inputBox">
                        <select value={inputData.role} onChange={(e) => handleChange(e)} name="role">
                            <option value="User">User</option>
                            <option value="Artist">Artist</option>
                        </select>
                    </div>
                    {inputData.role === "Artist" && (
                        <div className="inputBox">
                            <input type="text" value={inputData.artistName} name="artistName"
                                   onChange={(e) => handleChange(e)} placeholder='Artist Name' required />
                            <FaMusic className='icon'/>
                        </div>
                    )}

                    <button type='submit'>Register</button>
                </form>
                <div className="registerLink">
                    <p>Already have an account? <Link to="/login">Login</Link></p>
                </div>
            </div>
        </div>
    )
}

export {Register}