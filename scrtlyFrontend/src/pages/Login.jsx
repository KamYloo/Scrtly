import React, { useState, useEffect } from 'react'
import logo from '../assets/logo.png'
import { FaUser, FaLock } from "react-icons/fa";
import { MdCancel } from "react-icons/md";
import '../Styles/Login&Register.css'
import { Link, useNavigate } from 'react-router-dom'
import { loginAction } from "../Redux/AuthService/Action.js";
import { useDispatch, useSelector } from "react-redux";
import toast from "react-hot-toast";
import {FcGoogle} from "react-icons/fc";
import {BASE_API_URL} from "../Redux/api.js";

function Login() {
    const [inputData, setInputData] = useState({ email: "", password: "" });
    const [errors, setErrors] = useState({});
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { loading, error, loginResponse } = useSelector(state => state.auth);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setInputData(values => ({ ...values, [name]: value }));
    };

    const validate = () => {
        const newErrors = {};
        if (!inputData.email) newErrors.email = "Email is required.";
        else if (!/^\S+@\S+\.\S+$/.test(inputData.email)) newErrors.email = "Invalid email format.";
        if (!inputData.password) newErrors.password = "Password is required.";
        return newErrors;
    };

    const handleLogin = (e) => {
        e.preventDefault();
        const formErrors = validate();
        if (Object.keys(formErrors).length === 0) {
            dispatch(loginAction(inputData));
        } else {
            setErrors(formErrors);
        }
    };

    useEffect(() => {
        if (error) {
            toast.error(error);
        }
    }, [error]);

    useEffect(() => {
        if (loginResponse) {
            toast.success("You have logged in successfully.");
            navigate("/home");
        }
    }, [dispatch, loginResponse, navigate]);

    return (
        <div className='login'>
            <div className="formBox login">
                <i className='cancel'><Link to="/"><MdCancel /></Link></i>
                <form onSubmit={handleLogin}>
                    <div className="title">
                        <img src={logo} alt="Logo"/>
                        <h1>Login</h1>
                    </div>
                    <div className="inputBox">
                        <input
                            type="email"
                            name="email"
                            value={inputData.email}
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
                            value={inputData.password}
                            onChange={handleChange}
                            placeholder='Password'
                            required
                        />
                        <FaLock className='icon'/>
                        {errors.password && <p className="error">{errors.password}</p>}
                    </div>
                    <div className="button-group">
                        <button type='submit' disabled={loading} className="login-btn">
                            {loading ? 'Logging in…' : 'Login'}
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
