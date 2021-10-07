import './CartItem.css';
import * as Icon from 'react-feather';
import QuantityModifier from './QuantityModifier';

function CartItem({ cartItem, dish, image, currency, onChange, onRemove, onChangeComment }) {

    function changeCommentClick(e) {
        e.preventDefault();
        if (onChangeComment) {
            onChangeComment(cartItem);
        }
    }

    function onRemoveClick(e) {
        e.preventDefault();
        onRemove(cartItem.dishId)
    }

    function onQuantityChange(newQuantity) {
        onChange({ ...cartItem, quantity: newQuantity })
    }

    return (<>
        {cartItem && dish && <div className="cart-item">
            <div className="cart-item-img-container">
                <img src={image} alt="" className="cart-item-img" />
            </div>
            <div className="cart-item-info">
                <div className="flex-grow-1">
                    {dish.name}
                </div>
                <div className="d-flex">
                    <div>
                        <QuantityModifier value={cartItem.quantity}
                            onChange={onQuantityChange} />
                    </div>
                    <div className="flex-grow-1 text-center">
                        <span className="font-weight-bold">
                            {dish.price * cartItem.quantity} {currency}
                        </span>
                    </div>
                    <div className="position-relative">
                        <a href="#message" onClick={changeCommentClick} className="">
                            <Icon.MessageSquare className="feather-icon" />
                        </a>
                        {!!cartItem.comment &&
                            <span className="badge badge-pill badge-danger red-dot">.</span>}
                    </div>
                </div>
                <a href="#remove" onClick={onRemoveClick} className="remove-icon">
                    <Icon.X className="feather-icon" />
                </a>
            </div>
        </div>}
    </>);
}

export default CartItem;