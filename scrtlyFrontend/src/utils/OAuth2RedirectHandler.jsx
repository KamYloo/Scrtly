import { useEffect } from 'react'

import {useGetCurrentUserQuery} from "../Redux/services/authApi.js";
import {useNavigate} from "react-router-dom";

export function OAuth2RedirectHandler() {
    const navigate = useNavigate()
    const { data: user, isSuccess, isError, error } =
        useGetCurrentUserQuery(null, {
            skip: false,
            refetchOnMountOrArgChange: true,
        })

    useEffect(() => {
        if (isSuccess) {
            localStorage.setItem('isLoggedIn', '1')
            navigate('/home')
        }
    }, [isSuccess, navigate])

    useEffect(() => {
        if (isError) {
            navigate('/login')
        }
    }, [isError, error, navigate])

    return <div>Login in progress…</div>
}
