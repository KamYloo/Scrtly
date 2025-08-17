import React, { useEffect } from 'react';
import toast from 'react-hot-toast';
import '../Styles/BillingPage.css';
import logo from '../assets/subscriptionLogo.png';
import {useBillingPortalMutation, useFetchSubscriptionQuery} from "../Redux/services/paymentApi.js";

function BillingPage() {

    const {
        data: subscription,
        error: fetchError,
        isLoading,
    } = useFetchSubscriptionQuery();

    const [
        openBillingPortal,
        {
            data: billingPortalData,
            error: billingError,
            isLoading: isPortalLoading,
            isSuccess: isPortalSuccess,
        }
    ] = useBillingPortalMutation();

    useEffect(() => {
        if (isPortalSuccess && billingPortalData?.url) {
            window.location.href = billingPortalData.url;
        }
    }, [isPortalSuccess, billingPortalData]);

    useEffect(() => {
        if (fetchError) toast.error(fetchError);
    }, [fetchError]);

    useEffect(() => {
        if (billingError) toast.error(billingError);
    }, [billingError]);

    return (
        <div className="billing-page">
            <h1>Subscription Plan</h1>
            {isLoading  ? (
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
                        onClick={() => openBillingPortal()}
                        disabled={isPortalLoading}
                    >
                        Manage in Stripe
                    </button>
                </div>
            )}
        </div>
    );
}

export { BillingPage };
