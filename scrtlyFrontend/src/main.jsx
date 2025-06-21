import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import { Provider } from "react-redux"
import { store } from './Redux/store.js'
import {registerSW} from "virtual:pwa-register";

registerSW({
    onRegistered(r) {
        console.log('Service Worker registered under:', r?.scope)
    },
    onRegisterError(err) {
        console.error('SW registration error:', err)
    }
});
ReactDOM.createRoot(document.getElementById('root')).render(
    <React.StrictMode>
        <Provider store={store}>
            <App />
        </Provider>
    </React.StrictMode>
)