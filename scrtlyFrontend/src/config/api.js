export const BASE_API_URL = "http://localhost:8080"

const fetchWithAuth = async (url, options = {}, errorType) => {
    const headers = {
        'Authorization': `Bearer ${localStorage.getItem('refreshToken')}`,
        ...(options.body instanceof FormData ? {} : { 'Content-Type': 'application/json' }),
        ...options.headers,
    };

    try {
        const response = await fetch(`${BASE_API_URL}${url}`, { ...options, headers });

        if (!response.ok) {
            const errorData = await response.json();
            return { error: true, message: errorData.message || 'Request failed' };
        }
        const contentType = response.headers.get('content-type');
        const isJson = contentType && contentType.includes('application/json');
        return isJson ? await response.json() : await response.text();
    } catch (error) {
        console.error(`Error in ${errorType}:`, error);
        throw new Error(error.message);
    }
};


export const dispatchAction = async (dispatch, actionTypeRequest, actionTypeError, url, options = {}) => {
    const result = await fetchWithAuth(url, options, actionTypeRequest);
    console.log(result);

    if (result.refreshToken) {
        localStorage.setItem('refreshToken', result.refreshToken);
        localStorage.setItem('user', JSON.stringify(result.user));
    }

    if (result.error) {
        dispatch({ type: actionTypeError, payload: result.message });
        throw new Error(result.message);
    }

    dispatch({ type: actionTypeRequest, payload: result });

    return result
};
