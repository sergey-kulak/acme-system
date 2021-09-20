import { useEffect, useState } from 'react';
import ReactCreatableSelect from 'react-select/creatable';
import dishService from './dishService';

function TagSelect({ companyId, publicPointId, value, onChange, field, form, ...props }) {
    const [tags, setTags] = useState([]);

    useEffect(() => {
        if (companyId && publicPointId) {
            dishService.findTags(companyId, publicPointId)
                .then(response => response.data)
                .then(setTags);
        }
    }, [companyId, publicPointId]);

    function handleChange(selectedOptions) {
        let selectedValues = selectedOptions.map(option => option.value)
        if (field && field.onChange) {
            let event = {
                target: {
                    name: field.name,
                    value: selectedValues
                }
            };
            field.onChange(event);
        }
        if (onChange) {
            onChange(selectedValues);
        }
    }

    function toOptions(items) {
        return items && items.length ? items.map(item => ({
            value: item,
            label: item
        })) : [];
    }

    value = field && field.value ? field.value : value;
    let options = toOptions(tags);
    let values = toOptions(value || []);

    return (
        <ReactCreatableSelect options={options}
            onChange={handleChange} isMulti placeholder=""
            {...props}
            value={values}>
        </ReactCreatableSelect>
    );
}

export default TagSelect;