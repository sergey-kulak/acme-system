import { useIntl } from 'react-intl';
import { ROLE } from '../common/security';
import Select from '../common/Select';
import { toOptions } from '../common/utils';

function UserRoleSelect(props) {
    const intl = useIntl();
    const options = toOptions(intl, Object.values(ROLE), 'user.role');

    return (
        <Select options={options} {...props} />
    );
}

export default UserRoleSelect;