import React, { useEffect } from 'react';
import { useDispatch } from 'react-redux';
import './App.css';
import AppRoutes from "./AppRoutes.jsx";
import {currentUser} from "./Redux/AuthService/Action.js";

function App() {
    const dispatch = useDispatch();

    useEffect(() => {
        if (localStorage.getItem('isLoggedIn')) {
            dispatch(currentUser());
        }
    }, [dispatch]);

    return <AppRoutes />;
}

export default App;
