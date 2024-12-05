import { Client } from '@stomp/stompjs';

export default {
    install(app, options) {
        let stompClient;
        let isConnected = false;
        const subscriptions = [];

        const socketApi = {
            // Подключение к STOMP-серверу
            connect() {
                if (isConnected) {
                    console.log('Already connected to STOMP server');
                    return;
                }

                const serverUrl = 'ws://' + options.server + '/api/v1/streaming/ws';
                console.log('Connecting to:', serverUrl);

                stompClient = new Client({
                    brokerURL: serverUrl,
                    reconnectDelay: 5000,
                    onConnect: function (frame) {
                        console.log('STOMP Connected:', frame);
                        isConnected = true;

                        // Выполняем все отложенные подписки
                        subscriptions.forEach(sub => {
                            console.log(`Subscribing to topic: ${sub.eventType}`);
                            stompClient.subscribe(`${sub.eventType}`, (message) => {
                                console.log('Received message from server:', message);
                                const serverMessage = JSON.parse(message.body);
                                console.log('Parsed message:', serverMessage);
                                sub.callback(serverMessage);
                            });
                        });
                    },
                    onStompError: function (frame) {
                        console.error('STOMP Error: ' + frame.headers['message']);
                        console.error('Error details: ' + frame.body);
                        isConnected = false;
                    },
                    onDisconnect: function () {
                        isConnected = false;
                        console.log('STOMP Disconnected');
                    }
                });

                stompClient.activate();
            },

            sendMessage(payload, topic) {
                const topicPath = topic;
                console.log('Sending message to topic:', topicPath);
                const message = JSON.stringify(payload);
                console.log('Payload message for sendMessage:', message);

                if (isConnected && stompClient && stompClient.connected) {
                    stompClient.publish({
                        destination: topicPath,
                        body: message
                    });
                    console.log('Message successfully sent to:', topicPath);
                } else {
                    console.error('STOMP client is not connected!');
                }
            },

            subscribe(eventType, callback) {
                const topicPath = `${eventType}`;
                console.log('Formatted topicPath for subscribe:', topicPath);

                const isAlreadySubscribed = subscriptions.some(sub => sub.eventType === topicPath);
                if (isAlreadySubscribed) {
                    console.log(`Already subscribed to ${topicPath}`);
                    return;
                }

                subscriptions.push({ eventType, callback });

                if (isConnected && stompClient && stompClient.connected) {
                    console.log(`Subscribing immediately to: ${topicPath}`);
                    stompClient.subscribe(topicPath, (message) => {
                        console.log('Received message from server:', message);
                        const serverMessage = JSON.parse(message.body);
                        console.log('Parsed message:', serverMessage);
                        callback(serverMessage);
                    });
                }
            },

            disconnect() {
                if (stompClient) {
                    stompClient.deactivate();
                    isConnected = false;
                    console.log('Disconnected from STOMP server');
                }
            }
        };

        app.config.globalProperties.$socket = socketApi;
    }
};
