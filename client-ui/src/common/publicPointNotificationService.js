import { RSocketClient, JsonSerializers } from 'rsocket-core';
import RSocketWebSocketClient from 'rsocket-websocket-client';


const client = new RSocketClient({
    serializers: JsonSerializers,
    setup: {
        keepAlive: 60000,
        lifetime: 180000,
        dataMimeType: 'application/json',
        metadataMimeType: 'application/json',
    },
    transport: new RSocketWebSocketClient({ url: `ws://${window.location.hostname}:7004/pp-service-rsocket` }),
});

let subscriptionPromise;

const connect = function (token, companyId, publicPointId) {
    if (!subscriptionPromise) {
        subscriptionPromise = new Promise((resolve, reject) => {
            client.connect().subscribe({
                onComplete: socket => {
                    let subscription = socket.requestStream({
                        data: { companyId, publicPointId },
                        metadata: {
                            'route': 'get.notifications',
                            'token': token
                        },
                    })
                    resolve(subscription)
                },
                onError: error => {
                    console.error(error);
                    reject(error);
                }
            });
        });
    }

    return subscriptionPromise;
}

const subscribe = function (subscriber) {
    subscriptionPromise
        .then(subscription => subscription.subscribe(subscriber));
}

const publicPointNotificationService = {
    connect,
    subscribe
}

export default publicPointNotificationService;