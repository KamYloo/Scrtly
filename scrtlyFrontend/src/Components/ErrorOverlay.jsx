import React from 'react';
import '../Styles/ErrorAlert.css'
import Lottie from "lottie-react";
import errorAnimation from "../assets/errorAnimation.json"
import PropTypes from "prop-types";

const ErrorOverlay = ({ error, size = 300 }) => {
    const dimension = typeof size === 'number' ? `${size}px` : size;
    let code, businessDescription, genericError;

    if (error) {
        if (error.status) {
            const body = error.data || {};
            code = body.businessErrorCode;
            businessDescription = body.businessErrornDescription;
            genericError = body.error;
        } else if (error.error) {
            genericError = error.error;
        }
    }

    return (
        <div className="error-alert">
            <div className="error-animation" style={{width: dimension, height: dimension}}>
                <Lottie
                    animationData={errorAnimation}
                    loop
                    style={{width: '100%', height: '100%'}}
                />
            </div>
            <div className="error-details">
                {code != null && <p><strong>Error Code:</strong> {code}</p>}
                {businessDescription && <p><strong>Description:</strong> {businessDescription}</p>}
                {/*{genericError && <p><strong>Error:</strong> {genericError}</p>}*/}
            </div>
        </div>
    );
};

ErrorOverlay.propTypes = {
    error: PropTypes.shape({
        status: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
        data: PropTypes.shape({
            businessErrorCode: PropTypes.number,
            businessErrornDescription: PropTypes.string,
            error: PropTypes.string,
            validationErrors: PropTypes.arrayOf(PropTypes.string),
        }),
        error: PropTypes.string,
    }).isRequired,
    size: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
};

export default ErrorOverlay;
