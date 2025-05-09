import React from 'react';
import '../Styles/Spinner.css'
import Lottie from 'lottie-react';
import loadingAnimation from '../assets/loadingAnimation.json';

const Spinner = () => (
    <div className="spinner-container">
        <div className="spinner">
            <Lottie animationData={loadingAnimation} loop={true} />
        </div>
    </div>
);

export default Spinner;
