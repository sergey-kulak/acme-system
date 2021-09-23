
import { useCallback, useEffect, useState } from "react";
import { connect } from "react-redux";
import { onRemove, onUpdate } from "../order/cartReducer";
import CartItem from "./CartItem";
import menuService from "../menu/menuService";
import fileService from "../common/fileService";
import { isEmptyObject } from "../common/utils";
import CommentDialog from "./CommentDialog";

function MyOrder({ auth, cart, onRemove, onUpdate }) {
    const [dishes, setDishes] = useState({});
    const [imageUrls, setImageUrls] = useState({});
    const [modifiedCartItem, setModifiedCartItem] = useState();

    const loadImages = useCallback(data => {
        if (!data.length) {
            return Promise.resolve(data);
        }
        let request = {
            companyId: auth.user.cmpid,
            publicPointId: auth.user.ppid,
            action: 'DOWNLOAD',
            imageKeys: data.map(dish => dish.primaryImage)
        }

        return fileService.getDishImageUrls(request)
            .then(response => setImageUrls(response.data))
            .then(() => data)
    }, [auth]);

    useEffect(() => {
        if (cart.length) {
            let loadedDishIds = Object.keys(dishes);
            if (cart.some(item => !loadedDishIds.includes(item.dishId))) {
                let promises = cart.map(item =>
                    menuService.findDish(item.dishId)
                        .then(response => response.data)
                );

                Promise.all(promises)
                    .then(loadImages)
                    .then(data => {
                        let dishes = {};
                        data.forEach(dish => dishes[dish.id] = dish);
                        setDishes(dishes);
                    })
            }
        }
    }, [cart, dishes, loadImages]);

    function buildCartItem(item) {
        let dish = dishes[item.dishId]

        return <CartItem key={item.dishId}
            cartItem={item} dish={dish} onChange={onUpdate}
            onRemove={onRemove} onChangeComment={onChangeComment}
            image={imageUrls[dish.primaryImage]} />
    }

    function onChangeComment(cartItem) {
        setModifiedCartItem(cartItem);
    }

    function onCommentDialogClose(comment) {
        onUpdate({ ...modifiedCartItem, comment });
        setModifiedCartItem(null);
    }

    return (
        <div className="main-content">
            <div>
                <div className="main-content-title mb-2">
                    Order
                </div>
                {cart.length > 0 && !isEmptyObject(dishes) ?
                    <div className="main-content-body">
                        {
                            cart.map(buildCartItem)
                        }
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
    );
}

const mapStateToProps = ({ auth, cart }) => {
    return { auth, cart };
};
export default connect(mapStateToProps, {
    onRemove, onUpdate
})(MyOrder);