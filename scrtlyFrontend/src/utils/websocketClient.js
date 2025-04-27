import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client/dist/sockjs";
import {BASE_API_URL} from "../config/api.js";

const WEBSOCKET_URL = `${BASE_API_URL}/ws`;

export const connectWebSocket = (username, onMessageReceived) => {
    const client = new Client({
        webSocketFactory: () => new SockJS(WEBSOCKET_URL),
        reconnectDelay: 5000,
        onConnect: () => {
            client.subscribe(`/user/${username}/queue/notifications`, (message) => {
                const notification = JSON.parse(message.body);
                onMessageReceived(notification);
            });
            console.log(`Subscribed to channel: /user/${username}/queue/notifications`);
        },
        onStompError: (frame) => {
            console.error("STOMP Error: ", frame);
        },
    });

    client.activate();

    return client;
};
