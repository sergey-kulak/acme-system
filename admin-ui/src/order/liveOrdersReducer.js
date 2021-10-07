import { byProperty } from "../common/utils";

function updateOrderStatus(state, orderId, status) {
    const tableOrders = state.tableOrders
    for (let item of tableOrders) {
        for (let order of item.orders) {
            if (order.id === orderId && order.status !== status) {
                order.status = status;
                return { ...state };
            }
        }
    }

    return state;
}

function removeOrder(state, orderId) {
    const tableOrders = state.tableOrders
    let newTableOrders = tableOrders.filter(item => {
        item.orders = item.orders.filter(order => order.id !== orderId);

        return item.orders.length > 0;
    });
    return { ...state, tableOrders: newTableOrders };
}

function updateItemStatus(state, itemId, status) {
    const tableOrders = state.tableOrders
    for (let item of tableOrders) {
        for (let order of item.orders) {
            for (let item of order.items) {
                if (item.id === itemId && item.status !== status) {
                    item.status = status;
                    return { ...state };
                }
            }
        }
    }

    return state;
}

function sort(tableOrders) {
    for (let item of tableOrders) {
        for (let order of item.orders) {
            order.items.sort(byProperty('dishName'))
        }
        item.orders.sort(byProperty('number'))
    }
    tableOrders.sort(byProperty('tableName'))
    return tableOrders;
}

function addOrder(state, order) {
    const tableOrders = state.tableOrders
    const tableNames = state.tableNames

    const toItem = tableOrders.find(toItem => toItem.tableId === order.tableId);
    if (toItem) {
        if (!toItem.orders.some(iOrder => iOrder.id === order.id)) {
            toItem.orders.push(order);
        }
    } else {
        tableOrders.push({
            tableId: order.tableId,
            tableName: tableNames[order.tableId],
            orders: [order]
        });
    }
    return { ...state };
}

function setOrders(state, data) {
    const orders = {}
    const tableNames = state.tableNames
    data.forEach(order => {
        let tableOrders = orders[order.tableId];
        if (!tableOrders) {
            tableOrders = {
                tableId: order.tableId,
                tableName: tableNames[order.tableId],
                orders: []
            }
            orders[order.tableId] = tableOrders;
        }
        tableOrders.orders.push(order);
    });

    return {
        ...state,
        tableOrders: sort(Object.values(orders))
    }
}

function setTableNames(state, data) {
    let tableNames = data.reduce((acc, item) => {
        acc[item.id] = item.name;
        return acc;
    }, {});
    return {
        ...state,
        tableNames
    }
}

export const reducer = (state, action) => {
    const payload = action.payload;
    switch (action.type) {
        case 'set-orders':
            return setOrders(state, payload);
        case 'set-table-names':
            return setTableNames(state, payload);
        case 'update-order-status':
            return updateOrderStatus(state, payload.orderId, payload.status);
        case 'remove-order':
            return removeOrder(state, payload);
        case 'update-item-status':
            return updateItemStatus(state, payload.itemId, payload.status);
        case 'add-order':
            return addOrder(state, payload);
        default:
            throw new Error();
    }
}