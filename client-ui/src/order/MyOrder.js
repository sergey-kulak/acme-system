
import React, { useCallback, useEffect, useState } from "react"
import { connect } from "react-redux"
import { onRemove, onUpdate, onSetOrderId } from "../order/cartReducer"
import { onSuccess } from "../common/toastNotification"
import CartItem from "./CartItem"
import menuService from "../menu/menuService"
import fileService from "../common/fileService"
import { isEmptyObject } from "../common/utils"
import CommentDialog from "./CommentDialog"
import OrderItem from "./OrderItem"
import { FormattedMessage } from "react-intl"

function MyOrder({ auth, cart, onRemove, onUpdate, onSetOrderId, onSuccess }) {
    const [dishes, setDishes] = useState({})
    const [imageUrls, setImageUrls] = useState({})
    const [modifiedCartItem, setModifiedCartItem] = useState()
    const [order, setOrder] = useState()

    const loadImages = useCallback(data => {
        if (!data.length) {
            return Promise.resolve(data)
        }
        let request = {
            companyId: auth.user.cmpid,
            publicPointId: auth.user.ppid,
            action: 'DOWNLOAD',
            imageKeys: data.map(dish => dish.primaryImage)
        }

        return fileService.getDishImageUrls(request)
            .then(response => setImageUrls(prev => ({
                ...prev,
                ...response.data
            })))
            .then(() => data)
    }, [auth])

    useEffect(() => {
        if (!cart.orderId || !!order) {
            let orderItems = (order && order.items) || []
            let dishIds = [
                ...cart.items.map(item => item.dishId),
                ...orderItems.map(item => item.dishId)
            ]

            let loadedDishIds = Object.keys(dishes)
            let newDishIds = dishIds.filter(id => !loadedDishIds.includes(id))
            if (newDishIds.length) {
                let promises = newDishIds.map(dishId =>
                    menuService.findDish(dishId)
                        .then(response => response.data)
                )

                Promise.all(promises)
                    .then(loadImages)
                    .then(data => {
                        let dishes = {}
                        data.forEach(dish => dishes[dish.id] = dish)
                        setDishes(prev => ({
                            ...prev,
                            ...dishes
                        }))
                    })
            }
        }
    }, [cart, order, dishes, loadImages])

    useEffect(() => {
        if (cart.orderId) {
            menuService.findOrderById(cart.orderId)
                .then(response => response.data)
                .then(setOrder)
        } else {
            setOrder()
        }
    }, [cart.orderId, cart.orderUpdateTime])

    function buildCartItem(item) {
        let dish = dishes[item.dishId]
        if (!dish) {
            return <React.Fragment key={item.dishId}></React.Fragment>
        }

        return <CartItem key={item.dishId}
            cartItem={item} dish={dish} onChange={onUpdate}
            onRemove={onRemove} onChangeComment={onChangeComment}
            currency={auth.data.currency}
            image={imageUrls[dish.primaryImage]} />
    }

    function buildOrderItem(item) {
        let dish = dishes[item.dishId]
        if (!dish) {
            return <React.Fragment key={item.id}></React.Fragment>
        }

        return <OrderItem key={item.id}
            orderItem={item} dish={dish}
            currency={auth.data.currency}
            image={imageUrls[dish.primaryImage]} />
    }

    function onChangeComment(cartItem) {
        setModifiedCartItem(cartItem)
    }

    function onCommentDialogClose(comment) {
        onUpdate({ ...modifiedCartItem, comment })
        setModifiedCartItem(null)
    }

    function onPlaceOrderClick() {
        if (!order) {
            let items = cart.items.map(item => {
                let dish = dishes[item.dishId]

                return {
                    ...item,
                    price: dish.price,
                    dishName: dish.name
                }
            })
            menuService.placeOrder({ items })
                .then(response => {
                    let orderId = response.data.id
                    onSuccess(`Your order was placed`)
                    onSetOrderId(orderId)
                })
        } else {
            alert('Adding to an order is not implemented yet')
        }
    }

    function pageTitle() {
        return <span>
            Order {!!order && <span>
                #{order.number} (<FormattedMessage id={`order.status.${order.status.toLowerCase()}`} />)
            </span>}
        </span>
    }

    function onNewOrderClick() {
        onSetOrderId()
    }

    return (
        <div className="main-content">
            <div>
                <div className="main-content-title">{pageTitle()}</div>
                {(cart.items.length > 0 || !!order) && !isEmptyObject(dishes) ?
                    <div className="main-content-body">
                        {!!order && <>
                            <div className="mb-2">
                                <span className="">Total price: </span>
                                <span className="font-weight-bold">{order.totalPrice} {auth.data.currency}</span>
                            </div>
                            {
                                order.items.map(buildOrderItem)
                            }
                            {(order.status === 'PAID' || order.status === 'DECLINED') && <div className="mb-3">
                                <button className="btn btn-primary" onClick={onNewOrderClick}>
                                    New order
                                </button>
                            </div>}
                        </>}
                        {cart.items.length > 0 && !!order && <div className="mb-2">
                            <span className="font-weight-bold">In the cart:</span>
                        </div>}
                        {
                            cart.items.map(buildCartItem)
                        }
                        {cart.items.length > 0 && <div>
                            <button className="btn btn-primary" onClick={onPlaceOrderClick}>
                                {!order ? 'Place order' : 'Add to the order'}
                            </button>
                        </div>}
                        {<div>
                            {!!modifiedCartItem
                                && <CommentDialog show comment={modifiedCartItem.comment}
                                    onClose={onCommentDialogClose}
                                />
                            }
                        </div>}
                    </div> : <div className="main-content-body">
                        You have no dishes selected
                    </div>}
            </div>
        </div>
    )
}

const mapStateToProps = ({ auth, cart }) => {
    return { auth, cart }
}
export default connect(mapStateToProps, {
    onRemove, onUpdate, onSetOrderId, onSuccess
})(MyOrder)