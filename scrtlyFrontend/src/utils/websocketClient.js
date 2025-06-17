import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client/dist/sockjs";

const WEBSOCKET_URL = "http://localhost:8080/api/ws";

export const connectWebSocket = (username, onMessageReceived) => {
    const client = new Client({
        webSocketFactory: () => new SockJS(WEBSOCKET_URL),
        reconnectDelay: 5000,
        onConnect: () => {
            client.subscribe(`/exchange/notification.exchange/notification.user.${username}`, (message) => {
                const notification = JSON.parse(message.body);
                onMessageReceived(notification);
            });
            // console.log(`Subscribed to channel: /user/${username}/queue/notifications`);
        },
        onStompError: (frame) => {
            console.error("STOMP Error: ", frame);
        },
    });

    client.activate();

    return client;
};
