import React, {useEffect, useState} from 'react'
import logo from '../../img/logo.png'
import { FaUser, FaLock } from "react-icons/fa";
import { MdCancel } from "react-icons/md";
import '../../Styles/Login&Register.css'
import { Link, useNavigate } from 'react-router-dom'
import {currentUser, login} from "../../Redux/Auth/Action.js";
import {useDispatch, useSelector} from "react-redux";
import toast from "react-hot-toast";

function Login() {
    const [inputData, setInputData] = useState({email: "", password: ""});
    const [errors, setErrors] = useState({});
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const {auth} = useSelector(store => store);
    const token = localStorage.getItem('token');

    const handleChange = (e) => {
        const { name, value } = e.target;
        setInputData((values) => ({ ...values, [name]: value }));
    };

    const validate = () => {
        const newErrors = {};
        if (!inputData.email) newErrors.email = "Email is required.";
        if (!/^\S+@\S+\.\S+$/.test(inputData.email)) newErrors.email = "Invalid email format.";
        if (!inputData.password) newErrors.password = "Password is required.";
        return newErrors;
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        const formErrors = validate();
        if (Object.keys(formErrors).length === 0) {
            dispatch(login(inputData))
        } else {
            setErrors(formErrors);
        }
    };

    useEffect(() => {
        if (token) dispatch(currentUser(token));
    }, [dispatch, token]);

    useEffect(() => {
        if (auth.reqUser?.fullName) {
            navigate("/home")
            toast.success('You have logged in successfully.');
        } else {
            toast.error('Failed to login. Please try again.');
        }
    }, [auth.reqUser, navigate]);

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
                        {errors.email && <p className="error">{errors.email}</p>}
                    </div>
                    <div className="inputBox">
                        <input type="password" name="password" value={inputData.password} onChange={(e) => handleChange(e)} placeholder='Password' required />
                        <FaLock className='icon' />
                        {errors.password && <p className="error">{errors.password}</p>}
                    </div>
                    <button type='submit'>Login</button>
                    <div className="registerLink">
                        <p>Don't have an account? <Link to="/register">Register</Link></p>
                    </div>
                </form>
            </div>
        </div>
    );
}

export { Login };
