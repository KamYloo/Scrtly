import React from 'react';
import '../Styles/ErrorAlert.css'
import Lottie from "lottie-react";
import errorAnimation from "../assets/errorAnimation.json"

const ErrorOverlay = ({ message, size = 300 }) => {
    const dimension = typeof size === 'number' ? `${size}px` : size;
    return (
        <div className="error-alert">
            <div className="error-animation" style={{ width: dimension, height: dimension }}>
                <Lottie
                    animationData={errorAnimation}
                    loop
                    style={{ width: '100%', height: '100%' }}
                />
            </div>
            <p className="error-message"><strong>Error:</strong> {message}</p>
        </div>
    );
};

export default ErrorOverlay;
