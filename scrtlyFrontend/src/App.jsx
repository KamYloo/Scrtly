import React, { } from 'react';
import './App.css';
import AppRoutes from "./AppRoutes.jsx";
import {useGetCurrentUserQuery} from "./Redux/services/authApi.js";

function App() {
    useGetCurrentUserQuery(null, {
        skip: !localStorage.getItem('isLoggedIn'),
    });

    return <AppRoutes />;
}

export default App;
