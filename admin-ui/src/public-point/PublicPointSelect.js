import { useEffect, useState } from 'react';
import Select from '../common/Select';
import publicPointService from './publicPointService';

function PublicPointSelect({ companyId, ...props }) {
    const [options, setOptions] = useState([]);

    useEffect(() => {
        if (companyId) {
            publicPointService.findNames(companyId)
                .then(response => response.data)
                .then(data => setOptions(data.map(mapToOption)));
        }
    }, [companyId])

    function mapToOption(pp) {
        return {
            value: pp.id,
            label: pp.name,
            data: pp
        };
    }

    return (
        <Select options={options} {...props} />
    );
}

export default PublicPointSelect;