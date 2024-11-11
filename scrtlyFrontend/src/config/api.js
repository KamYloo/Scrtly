export const BASE_API_URL = "http://localhost:8080"

const fetchWithAuth = async (url, options = {}, errorType) => {
    const headers = {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        ...(options.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }),
        ...options.headers,
    };

    try {
        const response = await fetch(`${BASE_API_URL}${url}`, { ...options, headers });

        if (!response.ok) {
            const errorData = await response.json();
            return { error: true, message: errorData.message || 'Request failed' };
        }

        return await response.json();
    } catch (error) {
        console.error(`Error in ${errorType}:`, error);
        throw new Error(error.message);
    }
};


export const dispatchAction = async (dispatch, actionTypeRequest, actionTypeError, url, options = {}) => {
    const result = await fetchWithAuth(url, options, actionTypeRequest);
    console.log(result);
    if (result.error) {
        dispatch({ type: actionTypeError, payload: result.message });
        throw new Error(result.message);
    }

    dispatch({ type: actionTypeRequest, payload: result });
};
