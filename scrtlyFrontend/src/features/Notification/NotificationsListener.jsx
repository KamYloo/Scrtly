import { useEffect } from "react";
import {useDispatch} from "react-redux";

import {connectWebSocket} from "../../utils/websocketClient.js";
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";
import {notificationApi} from "../../Redux/services/notificationApi.js";


const NotificationsListener = () => {
    const dispatch = useDispatch();
    const { data: reqUser } = useGetCurrentUserQuery(null, {
        skip: !localStorage.getItem('isLoggedIn'),
    });

    useEffect(() => {
        if (!reqUser) return;

        const client = connectWebSocket(reqUser.email, (notification) => {
            dispatch(
                notificationApi.util.updateQueryData(
                    "getNotifications",
                    { page: 0, size: 10 },
                    (draft) => {
                        draft.notifications.unshift(notification);
                        draft.notifications = [
                            ...new Map(
                                draft.notifications.map((n) => [n.id, n])
                            ).values(),
                        ];
                    }
                )
            );
        });

        return () => {
            client.deactivate();
        };
    }, [reqUser, dispatch]);

    return null;
};

export default NotificationsListener;
