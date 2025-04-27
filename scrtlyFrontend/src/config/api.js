export const BASE_API_URL = "http://145.239.91.66:5001"

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
                return { error: true, message: 'Sesja wygasła. Zaloguj się ponownie.' };
            }
        }

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

        console.log(result);

        if (result.user) {
            localStorage.setItem('user', JSON.stringify(result.user));
        }

        if (result.error) {
            dispatch({ type: actionTypeError, payload: result.message });
            throw new Error(result.message);
        }

        dispatch({ type: actionTypeSuccess, payload: result });
        return result;
    } catch (error) {
        dispatch({ type: actionTypeError, payload: error.message });
        throw error;
    }
};



