import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import {stripePromise} from "../../utils/stripe.js";
import {subscribeAction} from "../../Redux/PaymentService/Action.js";
import {FaCrown} from "react-icons/fa";
import toast from "react-hot-toast";


export function SubscribeButton({ successUrl, cancelUrl }) {
    const dispatch = useDispatch();
    const { error, session } = useSelector(state => state.paymentService);

    useEffect(() => {
        if (error) {
            toast.error(error);
        }
    }, [error]);

    useEffect(() => {
        if (!session) return;

        async function redirect() {
            const stripe = await stripePromise;
            const { error } = await stripe.redirectToCheckout({ sessionId: session });
            if (error) {
                toast.error(error.message || "Stripe redirect error");
            }
        }

        redirect();
    }, [session]);

    const handleSubscribe = () => {
        dispatch(subscribeAction({successUrl, cancelUrl}));
    };

    return (
        <i onClick={handleSubscribe} style={{ cursor: "pointer" }}>
            <FaCrown/>
            <p>
                Go <span>Premium</span>
            </p>
        </i>
    );
}
