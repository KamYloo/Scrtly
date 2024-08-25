import React, {useEffect, useState} from 'react'
import logo from '../../img/logo.png'
import { FaUser, FaLock } from "react-icons/fa";
import { MdCancel } from "react-icons/md";
import '../../Styles/Login&Register.css'
import { Link, useNavigate } from 'react-router-dom'
import {currentUser, login} from "../../Redux/Auth/Action.js";
import {useDispatch, useSelector} from "react-redux";

function Login() {
    const [inputData, setInputData] = useState({email: "", password: ""})
    const dispatch = useDispatch();
    const navigate = useNavigate()

    const {auth} = useSelector(store => store)
    const token = localStorage.getItem('token')

    const handleChange = (e) => {
        const { name, value } = e.target;
        setInputData((values)=>({...values, [name]: value}))
    }

    const handleLogin = async (e) => {
        e.preventDefault()
        e.preventDefault()
        dispatch(login(inputData))
    }

    useEffect(() => {
        if (token)dispatch(currentUser(token))
    }, [dispatch, token])

    useEffect(() => {
        if(auth.reqUser?.fullName) {
            navigate("/home")
        }
    }, [auth.reqUser, navigate])

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
                        <input type="email" name="email" value={inputData.email} onChange={(e) => handleChange(e)} placeholder='Email address' required />
                        <FaUser className='icon' />
                    </div>
                    <div className="inputBox">
                        <input type="password" name="password" value={inputData.password} onChange={(e) => handleChange(e)} placeholder='Password' required />
                        <FaLock className='icon' />
                    </div>
                    <button type='submit'>Login</button>
                    <div className="registerLink">
                        <p>Don t have an account? <Link to="/register">Register</Link></p>
                    </div>
                </form>
            </div>
        </div>
    )
}

export { Login }