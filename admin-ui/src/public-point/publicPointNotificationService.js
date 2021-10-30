import { RSocketClient, JsonSerializers } from 'rsocket-core'
import RSocketWebSocketClient from 'rsocket-websocket-client'

let wsClient
let client
let subscriptionPromise
let currentConnection
let subscribers = []

function initClient() {
    wsClient = new RSocketWebSocketClient({ url: 
        `ws://${window.location.hostname}:${window.location.port}/pp-service-rsocket` })
        //`ws://${window.location.hostname}:7004/pp-service-rsocket` })
    client = new RSocketClient({
        serializers: JsonSerializers,
        setup: {
            keepAlive: 30000,
            lifetime: 180000,
            dataMimeType: 'application/json',
            metadataMimeType: 'application/json',
        },
        transport: wsClient,
    })
}

function connect({ token, companyId, publicPointId }) {
    return new Promise((resolve, reject) => {
        console.log("connecting ...")
        initClient()
        client.connect().subscribe({
            onComplete: socket => {
                currentConnection = {
                    companyId, publicPointId, socket
                }
                const subscription = socket.requestStream({
                    data: { companyId, publicPointId },
                    metadata: {
                        'route': 'get.notifications',
                        'token': token
                    },
                })
                internalStatusSubscribe(socket)
                internalSubscribe(subscription)
                resolve(subscription)
            },
            onError: error => {
                console.error("error: " + error)
                reject(error)
            }
        })
    })
}

function internalSubscribe(subscription) {
    subscription.subscribe({
        onComplete: () => subscribers.forEach(subscriber => subscriber.onComplete()),
        onError: error => subscribers.forEach(subscriber => subscriber.onError(error)),
        onNext: event => subscribers.forEach(subscriber => subscriber.onNext(event)),
        onSubscribe: sub => {
            sub.request(2147483647)
            subscribers.forEach(subscriber => subscriber.onSubscribe(sub))
        }
    })
}

function internalStatusSubscribe(socket) {
    socket.connectionStatus().subscribe({
        onComplete: () => console.log('Status onComplete'),
        onError: error => console.log('Status onComplete'),
        onNext: event => console.log('Status onNext ' + JSON.stringify(event)),
        onSubscribe: sub => {
            console.log('Status onSubscribe')
            sub.request(2147483647)
        }
    })
}

let conCounter = 0
const subscribe = (request, subscriber) => {
    if (!subscriptionPromise && ++conCounter === 1) {
        subscriptionPromise = connect(request)
        conCounter--
    }
    subscribers.push(subscriber);
}

const unsubscribe = (subscriber) => {
    subscribers = subscribers.filter(is => is !== subscriber);    
}

const isConnected = (companyId, publicPointId) => {
    return currentConnection &&
        currentConnection.companyId === companyId &&
        currentConnection.publicPointId === publicPointId
}

const close = () => {
    if (currentConnection) {
        console.log("closing ...")
        client.close()
        wsClient.close()
    }
    currentConnection = null
    subscriptionPromise = null
}

const publicPointNotificationService = {
    subscribe,
    unsubscribe,
    isConnected,
    close
}

export default publicPointNotificationService