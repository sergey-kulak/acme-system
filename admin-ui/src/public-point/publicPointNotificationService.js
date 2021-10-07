import { RSocketClient, JsonSerializers } from 'rsocket-core'
import RSocketWebSocketClient from 'rsocket-websocket-client'

const client = new RSocketClient({
    serializers: JsonSerializers,
    setup: {
        keepAlive: 60000,
        lifetime: 180000,
        dataMimeType: 'application/json',
        metadataMimeType: 'application/json',
    },
    transport: new RSocketWebSocketClient({ url: `ws://${window.location.hostname}:7004/pp-service-rsocket` }),
})

let subscriptionPromise
let currentConnection

const connect = (token, companyId, publicPointId) => {
    if (!subscriptionPromise) {
        subscriptionPromise = new Promise((resolve, reject) => {
            client.connect().subscribe({
                onComplete: socket => {
                    currentConnection = {
                        companyId, publicPointId, socket
                    }
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
                    console.error(error)
                    reject(error)
                }
            })
        })
    }

    return subscriptionPromise
}

const subscribe = (subscriber) => {
    subscriptionPromise
        .then(subscription => subscription.subscribe(subscriber))
}

const isConnected = (companyId, publicPointId) => {
    return currentConnection &&
        currentConnection.companyId === companyId &&
        currentConnection.publicPointId === publicPointId
}

const close = () => {
    if (currentConnection) {
        currentConnection.socket.close()
    }
    currentConnection = null
}

const publicPointNotificationService = {
    connect,
    subscribe,
    isConnected,
    close
}

export default publicPointNotificationService