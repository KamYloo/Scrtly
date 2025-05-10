import React from 'react';
import '../Styles/Spinner.css'
import Lottie from 'lottie-react';
import loadingAnimation from '../assets/loadingAnimation.json';

const Spinner = ({ size = 300 }) => {
    const dimension = typeof size === 'number' ? `${size}px` : size;

    return (
        <div className="spinner-container">
            <div className="spinner">
                <div style={{ width: dimension, height: dimension }}>
                    <Lottie
                        animationData={loadingAnimation}
                        loop
                        style={{ width: '100%', height: '100%' }}
                    />
                </div>
            </div>
        </div>
    );
};

export default Spinner;
