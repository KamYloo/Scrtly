import React, { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import Lottie from "lottie-react";
import paymentAnimation from "../../assets/paymentFailed.json";

const CancelPage = ({size = 500}) => {
    const navigate = useNavigate()
    const dimension = typeof size === 'number' ? `${size}px` : size;

    useEffect(() => {
        const t = setTimeout(() => navigate('/'), 2000)
        return () => clearTimeout(t)
    }, [navigate])

    return (
        <div style={{padding: 20}}>
            <div style={{width: dimension, height: dimension}}>
                <Lottie
                    animationData={paymentAnimation}
                    loop
                    style={{width: '100%', height: '100%'}}
                />
            </div>
        </div>
    )
}

export default CancelPage;