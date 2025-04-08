import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import logo from '../img/logo.png';
import { MdCancel } from 'react-icons/md';
import '../Styles/Login&Register.css';
import { useDispatch, useSelector } from 'react-redux';
import toast from 'react-hot-toast';
import { forgotPasswordAction } from '../Redux/AuthService/Action.js';

function ForgotPassword() {
    const [email, setEmail] = useState('');
    const [errors, setErrors] = useState({});
    const dispatch = useDispatch();
    const navigate = useNavigate();

    const { loading } = useSelector(state => state.auth);

    const handleChange = (e) => {
        setEmail(e.target.value);
    };

    const validate = () => {
        const errs = {};
        if (!email) {
            errs.email = "Email is required.";
        } else if (!/^\S+@\S+\.\S+$/.test(email)) {
            errs.email = "Invalid email format.";
        }
        return errs;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const formErrors = validate();
        if (Object.keys(formErrors).length !== 0) {
            setErrors(formErrors);
            return;
        }

        try {
            await dispatch(forgotPasswordAction(email));
            toast.success("A password reset email has been sent.");
            navigate("/login");
        } catch (error) {
            toast.error("Failed to send reset email. Please try again.");
        }
    };

    return (
        <div className='login'>
            <div className="formBox login">
                <i className='cancel'>
                    <Link to="/"><MdCancel /></Link>
                </i>
                <form onSubmit={handleSubmit}>
                    <div className="title">
                        <img src={logo} alt="Logo" />
                        <h1>Forgot Password</h1>
                    </div>
                    <div className="inputBox">
                        <input
                            type="email"
                            name="email"
                            value={email}
                            onChange={handleChange}
                            placeholder='Email address'
                            required
                        />
                        {errors.email && <p className="error">{errors.email}</p>}
                    </div>
                    <button type='submit' disabled={loading}>
                        {loading ? "Sending..." : "Send Reset Email"}
                    </button>
                    <div className="registerLink">
                        <p>
                            Remembered your password? <Link to="/login">Login</Link>
                        </p>
                    </div>
                </form>
            </div>
        </div>
    );
}

export { ForgotPassword };
