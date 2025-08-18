import { loadStripe } from "@stripe/stripe-js";
const stripeKey = window.__ENV?.VITE_STRIPE_PUBLIC_KEY;

if (!stripeKey) {
    console.error("Stripe public key is missing!", window.__ENV);
}
export const stripePromise = loadStripe(stripeKey);
