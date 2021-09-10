import { useEffect, useState } from 'react';
import { hasRole, ROLE } from '../common/security';
import Select from '../common/Select';
import publicPointService from './publicPointService';

function PublicPointSelect({ companyId, auth, ...props }) {
    const [options, setOptions] = useState([]);

    useEffect(() => {
        if (companyId && hasRole(auth, ROLE.COMPANY_OWNER)) {
            publicPointService.findNames(companyId)
                .then(response => response.data)
                .then(data => setOptions(data.map(mapToOption)));
        } else {
            publicPointService.findById(auth.user.ppid)
                .then(response => response.data)
                .then(data => setOptions([mapToOption(data)]));
        }
    }, [companyId, auth])

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