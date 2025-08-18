import { loadStripe } from "@stripe/stripe-js";
const stripeKey = window.__ENV?.VITE_STRIPE_PUBLIC_KEY || import.meta.env.VITE_STRIPE_PUBLIC_KEY;
export const stripePromise = loadStripe(stripeKey);
