import React, {useEffect, useState} from 'react'
import logo from '../../img/logo.png'
import {FaUser, FaLock, FaEnvelope, FaMusic} from "react-icons/fa";
import { MdCancel } from "react-icons/md";
import '../../Styles/Login&Register.css'
import { Link, useNavigate } from 'react-router-dom'
import {useDispatch, useSelector} from "react-redux";
import {currentUser, register} from "../../Redux/Auth/Action.js";
import toast from "react-hot-toast";

function Register() {
    const [inputData, setInputData] = useState({
        fullName: "", email: "", password: "", confirmPassword: "", role: "User", artistName: ""
    });
    const [errors, setErrors] = useState({});
    const {auth} = useSelector(store => store);
    const token = localStorage.getItem('token');
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setInputData((values) => ({ ...values, [name]: value }));
    };

    const validate = () => {
        const newErrors = {};
        if (!inputData.fullName) newErrors.fullName = "Full Name is required.";
        if (!inputData.email) newErrors.email = "Email is required.";
        if (!/^\S+@\S+\.\S+$/.test(inputData.email)) newErrors.email = "Invalid email format.";
        if (!inputData.password) newErrors.password = "Password is required.";
        if (inputData.password.length < 6) newErrors.password = "Password must be at least 6 characters.";
        if (inputData.password !== inputData.confirmPassword) newErrors.confirmPassword = "Passwords do not match.";
        if (inputData.role === "Artist" && !inputData.artistName) newErrors.artistName = "Artist Name is required.";

        return newErrors;
    };

    const handleSignup = async (e) => {
        e.preventDefault();
        const formErrors = validate();
        if (Object.keys(formErrors).length === 0) {
            dispatch(register(inputData))
        } else {
            setErrors(formErrors);
        }
    };

    useEffect(() => {
        if (token) dispatch(currentUser(token));
    }, [token, dispatch]);

    useEffect(() => {
        if (auth.reqUser?.fullName) {
            navigate("/login");
            toast.success('You have registered successfully.');
        } else {
            toast.error('Failed to register. Please try again.');
        }
    }, [auth.reqUser, navigate]);

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
                               onChange={(e) => handleChange(e)} placeholder='Full Name' required/>
                        <FaUser className='icon'/>
                        {errors.fullName && <p className="error">{errors.fullName}</p>}
                    </div>
                    <div className="inputBox">
                        <input type="email" value={inputData.email} name="email"
                               onChange={(e) => handleChange(e)} placeholder='Email' required/>
                        <FaEnvelope className='icon'/>
                        {errors.email && <p className="error">{errors.email}</p>}
                    </div>
                    <div className="inputBox">
                        <input type="password" value={inputData.password} name="password"
                               onChange={(e) => handleChange(e)} placeholder='Password' required/>
                        <FaLock className='icon'/>
                        {errors.password && <p className="error">{errors.password}</p>}
                    </div>
                    <div className="inputBox">
                        <input type="password" value={inputData.confirmPassword} name="confirmPassword"
                               onChange={(e) => handleChange(e)} placeholder='Confirm Password' required/>
                        <FaLock className='icon'/>
                        {errors.confirmPassword && <p className="error">{errors.confirmPassword}</p>}
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
                            {errors.artistName && <p className="error">{errors.artistName}</p>}
                        </div>
                    )}

                    <button type='submit'>Register</button>
                </form>
                <div className="registerLink">
                    <p>Already have an account? <Link to="/login">Login</Link></p>
                </div>
            </div>
        </div>
    );
}

export {Register};
