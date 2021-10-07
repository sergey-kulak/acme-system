import './Dish.css'
import * as Icon from 'react-feather'

function Dish({ dish, image, currency, onAddClick }) {

    function addToCart(e) {
        e.preventDefault()
        onAddClick(dish)
    }

    return (
        <div className="dish">
            <div className="position-relative">
                <img src={image} alt="" className="dish-img" />
                <div className="d-flex align-items-center p-1 price-footer">
                    <div className="flex-grow-1 price">{dish.price} {currency}</div>
                    <div>
                        <a href="#addToCart" onClick={addToCart} className="pr-1">
                            <Icon.ShoppingCart className="filter-icon" />
                        </a>
                    </div>
                </div>
            </div>
            <div className="dish-title">
                {dish.name}
            </div>
            <div className="dish-composition">
                {dish.composition}
            </div>
        </div>
    )
}

export default Dish