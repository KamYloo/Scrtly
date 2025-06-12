import { LOGIN_SUCCESS, LOGOUT_SUCCESS } from "./AuthService/ActionType.js";

export const BASE_API_URL = "http://localhost:8080";

export const fetchWithAuth = async (url, options = {}, errorType) => {
    const headers = {
        ...(options.body instanceof FormData ? {} : { "Content-Type": "application/json" }),
        ...options.headers,
    };
    let response;

    const doFetch = async () => {
        try {
            return await fetch(`${BASE_API_URL}${url}`, {
                ...options,
                headers,
                credentials: "include",
            });
        } catch (networkErr) {
            console.error(`Network error in ${errorType}:`, networkErr);
            throw new Error("Network error");
        }
    };

    response = await doFetch();

    if (response.status === 401) {
        const refreshRes = await fetch(`${BASE_API_URL}/api/auth/refresh`, {
            method: "POST",
            credentials: "include",
        });
        if (refreshRes.ok) {
            response = await doFetch();
        } else {
            localStorage.removeItem("isLoggedIn");
            window.location.href = "/login";
            return;
        }
    }

    const text = await response.text();
    let data = null;
    try {
        data = text ? JSON.parse(text) : null;
    } catch {
        data = null;
    }

    if (!response.ok) {
        const msg =
            (data && (data.message || data.error || data.businessErrorDescription)) ||
            text ||
            `Request failed with status ${response.status}`;
        return { error: true, message: msg, status: response.status };
    }

    const contentType = response.headers.get("Content-Type") || "";
    const isJson = contentType.includes("application/json");
    return isJson ? data : text;
};

export const dispatchAction = async (
    dispatch,
    actionTypeRequest,
    actionTypeSuccess,
    actionTypeError,
    url,
    options = {}
) => {
    dispatch({ type: actionTypeRequest });
    try {
        const result = await fetchWithAuth(url, options, actionTypeRequest);

        if (result && result.error) {
            dispatch({ type: actionTypeError, payload: result.message });
            throw new Error(result.message);
        }

        dispatch({ type: actionTypeSuccess, payload: result });

        if (actionTypeSuccess === LOGIN_SUCCESS) {
            localStorage.setItem("isLoggedIn", "1");
        } else if (actionTypeSuccess === LOGOUT_SUCCESS) {
            localStorage.removeItem("isLoggedIn");
        }

        return result;
    } catch (err) {
        dispatch({ type: actionTypeError, payload: err.message });
        throw err;
    }
};
