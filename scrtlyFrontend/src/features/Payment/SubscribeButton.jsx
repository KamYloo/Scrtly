import React, { useEffect } from "react";
import {stripePromise} from "../../utils/stripe.js";
import {FaCrown} from "react-icons/fa";
import toast from "react-hot-toast";
import {useSubscribeMutation} from "../../Redux/services/paymentApi.js";

export function SubscribeButton({ successUrl, cancelUrl }) {
    const [subscribe, { data, error, isError }] = useSubscribeMutation();

    useEffect(() => {
        if (isError) {
            toast.error(error);
        }
    }, [error, isError]);

    useEffect(() => {
        if (!data?.sessionId) return;

        async function redirect() {
            const stripe = await stripePromise;
            const { error } = await stripe.redirectToCheckout({ sessionId: data.sessionId, });
            if (error) {
                toast.error(error.message || "Stripe redirect error");
            }
        }

        redirect();
    }, [data]);

    const handleSubscribe = () => {
        subscribe({ successUrl, cancelUrl });
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
