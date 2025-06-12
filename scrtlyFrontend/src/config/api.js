import {LOGIN_SUCCESS, LOGOUT_SUCCESS} from "../Redux/AuthService/ActionType.js";

export const BASE_API_URL = "http://localhost:8080"

export const fetchWithAuth = async (url, options = {}, errorType) => {
    let headers = {
        ...(options.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }),
        ...options.headers,
    };

    try {
        let response = await fetch(`${BASE_API_URL}${url}`, {
            ...options,
            headers,
            credentials: 'include'
        });

        if (url === '/api/authService/login' && response.status === 401) {
            const errorData = await response.json();
            const msg = errorData.error || errorData.businessErrornDescription;
            return { error: true, message: msg };
        }

        if (response.status === 401) {
            const refreshResponse = await fetch(`${BASE_API_URL}/api/auth/refresh`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include'
            });

            if (refreshResponse.ok) {
                response = await fetch(`${BASE_API_URL}${url}`, {
                    ...options,
                    headers,
                    credentials: 'include'
                });
            } else {
                window.location.href = "/login";
                return { error: true, message: 'Your session has expired. Please log in again' };
            }
        }

        if (!response.ok) {
            const errorData = await response.json();
            const serverMessage =
                errorData.message ||
                errorData.businessErrornDescription ||
                errorData.error ||
                (errorData.validationErrors && errorData.validationErrors.join(', ')) ||
                'Request failed';
            return { error: true, message: serverMessage };
        }

        const contentType = response.headers.get('content-type');
        const isJson = contentType && contentType.includes('application/json');
        return isJson ? await response.json() : await response.text();
    } catch (error) {
        console.error(`Error in ${errorType}:`, error);
        throw new Error(error.message);
    }
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
        // console.log(result);
        if (result.error) {
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
    } catch (error) {
        dispatch({ type: actionTypeError, payload: error.message });
        throw error;
    }
};



