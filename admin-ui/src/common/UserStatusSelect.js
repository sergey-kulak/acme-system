import Select from 'react-select';

const options = [
    { value: 'INACTIVE', label: 'Inactive' },
    { value: 'ACTIVE', label: 'Active' }
];
function UserStatusSelect({ selectedStatuses, optionFilter, onChange, ...props }) {

    function handleStatusChange(selectedOptions) {
        let selectedValues = props.isMulti ?
            selectedOptions.map(option => option.value) : selectedOptions.value;
        onChange(selectedValues);
    }

    const selectedOptions = props.isMulti ?
        options.filter(option => selectedStatuses.includes(option.value)) :
        options.filter(option => selectedStatuses === option.value);

    const allowedOptions = optionFilter ? optionFilter(options) : options;

    return (
        <Select options={allowedOptions} placeholder=""
            onChange={handleStatusChange}
            {...props}
            value={selectedOptions}>
        </Select>
    );
}

export default UserStatusSelect;