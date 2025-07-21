import { useEffect } from "react";
import {useDispatch} from "react-redux";

import {sendNotification} from "../../Redux/NotificationService/Action.js";
import {connectWebSocket} from "../../utils/websocketClient.js";
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";


const NotificationsListener = () => {
    const dispatch = useDispatch();
    const { data: reqUser } = useGetCurrentUserQuery(null, {
        skip: !localStorage.getItem('isLoggedIn'),
    });

    useEffect(() => {
        if (reqUser) {
            const client = connectWebSocket(reqUser.email, (notification) => {
                dispatch(sendNotification(notification));
            });

            return () => client.deactivate();
        }
    }, [reqUser, dispatch]);

    return null;
};

export default NotificationsListener;
