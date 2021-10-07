import * as Icon from 'react-feather'
import './QuantityModifier.css'

function QuantityModifier({ value, onChange }) {

    function onIncreaseClick(e) {
        e.preventDefault()
        onChange(value + 1)
    }

    function onDecreaseClick(e) {
        e.preventDefault()
        if (value > 1) {
            onChange(value - 1)
        }
    }

    return (
        <div className="quantity-modifier">
            <a href="#minus" onClick={onDecreaseClick}>
                <Icon.Minus className="feather-icon" />
            </a>
            <input type="text" value={value} readOnly/>
            <a href="#plus" onClick={onIncreaseClick}>
                <Icon.Plus className="feather-icon" />
            </a>
        </div>
    )
}

export default QuantityModifier