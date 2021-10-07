import { useEffect, useState } from 'react';
import Select from '../common/Select';
import dishService from './dishService';

function DishSelect({ companyId, publicPointId, ...props }) {
    const [options, setOptions] = useState([]);

    useEffect(() => {
        if (companyId && publicPointId) {
            dishService.findNames(companyId, publicPointId)
                .then(response => response.data)
                .then(data => setOptions(data.map(mapToOption)));
        } else {
            setOptions([]);
        }
    }, [companyId, publicPointId])

    function mapToOption(dish) {
        return {
            value: dish.id,
            label: dish.name,
            data: dish
        };
    }

    return (
        <Select options={options} {...props} />
    );
}

export default DishSelect