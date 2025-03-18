import React from 'react';
import '../Styles/ErrorAlert.css'

const ErrorAlert = ({ message }) => (
    <div className="error-alert">
        <p>Błąd: {message}</p>
    </div>
);

export default ErrorAlert;
