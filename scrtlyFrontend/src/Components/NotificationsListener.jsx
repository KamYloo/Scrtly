import { useEffect } from "react";
import { useDispatch } from "react-redux";

import {addNotification} from "../Redux/NotificationService/Action.js";
import {connectWebSocket} from "../utils/websocketClient.js";


const NotificationsListener = () => {
    const dispatch = useDispatch();
    const userData = (() => { try { return JSON.parse(localStorage.getItem("user")) || null; } catch { return null; } })();

    useEffect(() => {
        if (userData) {
            const client = connectWebSocket(userData.email, (notification) => {
                dispatch(addNotification(notification));
            });

            return () => client.deactivate();
        }
    }, [userData, dispatch]);

    return null;
};

export default NotificationsListener;
