import { loadStripe } from "@stripe/stripe-js";
const stripeKey = "pk_test_51RiZalFy63ngC4AgptAl0yXRIOdi3VdvgcepZMXeZ29Kvxnu4E8dHnB9eRvEij1Xg08T8XbUHg4I1sYQG4LKFwVB002tV01mDV";

if (!stripeKey) {
    console.error("Stripe public key is missing!");
}

export const stripePromise = loadStripe(stripeKey);