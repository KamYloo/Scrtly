import { useState, useEffect } from 'react';

export const useTheme = () => {
    const getInitialTheme = () => {
        const savedTheme = localStorage.getItem('app-theme');
        if (savedTheme) {
            return savedTheme;
        }
        return 'dark';
    };

    const [theme, setTheme] = useState(getInitialTheme);

    useEffect(() => {
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem('app-theme', theme);
    }, [theme]);

    const toggleTheme = () => {
        setTheme((prevTheme) => (prevTheme === 'light' ? 'dark' : 'light'));
    };

    return { theme, toggleTheme };
};