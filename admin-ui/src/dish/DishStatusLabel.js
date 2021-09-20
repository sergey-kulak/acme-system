import { memo } from 'react';
import './DishStatusLabel.css';

function DishStatusLabel({ status }) {
    
    return (
        <div className={`dish-status del-${status}`}/>
    );
}

export default memo(DishStatusLabel);