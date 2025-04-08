import React, { useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import '../Styles/Login&Register.css';
import {BASE_API_URL} from "../config/api.js";
import logo from "../img/logo.png"; // Upewnij się, że ścieżka do CSS jest prawidłowa

function ChangePassword() {
    const { userId, token } = useParams();
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        password: '',
        passwordConfirmation: ''
    });
    const [errors, setErrors] = useState({});

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevData) => ({ ...prevData, [name]: value }));
    };

    const validate = () => {
        const errs = {};
        if (!formData.password) errs.password = "Password is required.";
        else if (formData.password.length < 6) errs.password = "Password must be at least 6 characters.";

        if (!formData.passwordConfirmation) errs.passwordConfirmation = "Password confirmation is required.";
        else if (formData.passwordConfirmation.length < 6)
            errs.passwordConfirmation = "Password confirmation must be at least 6 characters.";

        if (formData.password && formData.passwordConfirmation && formData.password !== formData.passwordConfirmation) {
            errs.passwordConfirmation = "Passwords do not match.";
        }
        return errs;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const validationErrors = validate();
        if (Object.keys(validationErrors).length !== 0) {
            setErrors(validationErrors);
            return;
        }
        try {
            const response = await fetch(`${BASE_API_URL}/api/auth/reset-password/${userId}/${token}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });
            if (response.ok) {
                toast.success("Password has been reset successfully.");
                navigate('/login');
            } else {
                const errorText = await response.text();
                toast.error("Failed to reset password. " + errorText);
            }
        } catch (error) {
            toast.error("Error: " + error.message);
        }
    };

    return (
        <div className="login">
            <div className="formBox login">
                <div className="title">
                    <img src={logo} alt=""/>
                    <h1>Change Password</h1>
                </div>
                <form onSubmit={handleSubmit}>
                    <div className="inputBox">
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="New Password"
                            required
                        />
                        {errors.password && <p className="error">{errors.password}</p>}
                    </div>
                    <div className="inputBox">
                        <input
                            type="password"
                            name="passwordConfirmation"
                            value={formData.passwordConfirmation}
                            onChange={handleChange}
                            placeholder="Confirm New Password"
                            required
                        />
                        {errors.passwordConfirmation && <p className="error">{errors.passwordConfirmation}</p>}
                    </div>
                    <button type="submit">Reset Password</button>
                </form>
                <div className="registerLink">
                    <p>
                        Back to <Link to="/login">Login</Link>
                    </p>
                </div>
            </div>
        </div>
    );
}

export {ChangePassword};
