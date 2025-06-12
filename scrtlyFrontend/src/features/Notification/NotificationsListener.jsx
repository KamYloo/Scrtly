import { useEffect } from "react";
import {useDispatch, useSelector} from "react-redux";

import {sendNotification} from "../../Redux/NotificationService/Action.js";
import {connectWebSocket} from "../../utils/websocketClient.js";


const NotificationsListener = () => {
    const dispatch = useDispatch();
    const { reqUser } = useSelector(state => state.auth);

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
