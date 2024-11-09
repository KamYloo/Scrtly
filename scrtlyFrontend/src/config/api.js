export const BASE_API_URL = "http://localhost:8080"

const fetchWithAuth = async (url, options = {}, errorType) => {
    const headers = {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        ...(options.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }),
        ...options.headers,
    };

    try {
        const response = await fetch(`${BASE_API_URL}${url}`, { ...options, headers });
        return await response.json();
    } catch (error) {
        console.error(`Error in ${errorType}:`, error);
        throw new Error(error.message);
    }
};

export const dispatchAction = async (dispatch, actionTypeRequest,actionTypeError, url, options = {}) => {
    try {
        const data = await fetchWithAuth(url, options, actionTypeRequest);
        dispatch({ type: actionTypeRequest, payload: data });
    } catch (error) {
        dispatch({ type: actionTypeError, payload: error.message });
    }
};