import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import toast from 'react-hot-toast';
import { billingPortalAction, fetchSubscriptionAction } from "../Redux/PaymentService/Action.js";
import '../Styles/BillingPage.css';
import logo from '../assets/subscriptionLogo.png';

function BillingPage() {
    const dispatch = useDispatch();
    const { subscription, billingPortalUrl, loading, error, loadingButton } = useSelector(state => state.paymentService);

    useEffect(() => {
        dispatch(fetchSubscriptionAction());
    }, [dispatch]);

    useEffect(() => {
        if (billingPortalUrl) window.location.href = billingPortalUrl;
    }, [billingPortalUrl]);

    useEffect(() => {
        if (error) toast.error(error);
    }, [error]);

    return (
        <div className="billing-page">
            <h1>Subscription Plan</h1>
            {loading ? (
                <p className="bp--loading">Loading data…</p>
            ) : (
                <div className="bp__card">
                    <div className="bp__logo-wrap">
                        <img src={logo} alt="Logo subskrypcji" className="bp__logo" />
                    </div>
                    <div className="bp__header bp__header--vertical">
                        <div className="bp__plan-info">
                            <h2>Monthly plan</h2>
                            <p className="bp__price">19,99PLN / month</p>
                        </div>
                    </div>
                    {subscription ? (
                        <div className="bp__body bp__body--active">
                            <div className="bp__row">
                                <span>From:</span>
                                <strong>{new Date(subscription.startDate).toLocaleDateString()}</strong>
                            </div>
                            <div className="bp__row">
                                <span>To:</span>
                                <strong>
                                    {subscription.currentPeriodEnd
                                        ? new Date(subscription.currentPeriodEnd).toLocaleDateString()
                                        : '—'}
                                </strong>
                            </div>
                            <div className="bp__row">
                                <span>Status:</span>
                                <span >{subscription.status}</span>
                            </div>
                        </div>
                    ) : (
                        <p className="bp__no-sub">You do not have an active subscription.</p>
                    )}
                    <button
                        className="bp__btn"
                        onClick={() => dispatch(billingPortalAction())}
                        disabled={loadingButton}
                    >
                        Manage in Stripe
                    </button>
                </div>
            )}
        </div>
    );
}

export { BillingPage };
