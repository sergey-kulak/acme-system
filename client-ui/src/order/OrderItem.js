import { FormattedMessage } from 'react-intl';
import './OrderItem.css';

function OrderItem({ orderItem, dish, image, currency }) {

    return (<>
        {orderItem && dish && <div className="order-item">
            <div className="order-item-img-container">
                <img src={image} alt="" className="order-item-img" />
            </div>
            <div className="order-item-info">
                <div className="flex-grow-1 d-flex">
                    <div className="flex-grow-1">
                        {dish.name}
                    </div>
                    <div className="text-success">
                        <FormattedMessage id={`orderitem.status.${orderItem.status.toLowerCase()}`}/>
                    </div>
                </div>
                <div className="text-right">
                    {orderItem.quantity} x {dish.price} {currency}  =  <span
                        className="font-weight-bold">{dish.price * orderItem.quantity} {currency}</span>
                </div>
            </div>
        </div>}
    </>);
}

export default OrderItem;