// src/pages/OAuth2RedirectHandler.jsx
import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useDispatch } from 'react-redux'
import {currentUser} from "../Redux/AuthService/Action.js";

export function OAuth2RedirectHandler() {
    const dispatch = useDispatch()
    const navigate = useNavigate()

    useEffect(() => {
        dispatch(currentUser())
            .then(() => {
                localStorage.setItem('isLoggedIn', '1')
                navigate('/home')
            })
            .catch(() => {
                navigate('/login')
            })
    }, [dispatch, navigate])

    return <div>Login in progress…</div>
}
