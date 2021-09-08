import { useIntl } from 'react-intl';
import Select from '../common/Select';
import { toOptions } from '../common/utils';

const STATUSES = ['INACTIVE', 'ACTIVE', 'SUSPENDED', 'STOPPED'];

function PublicPointStatusSelect(props) {
    const intl = useIntl();
    const options = toOptions(intl, STATUSES, 'pp.status');

    return (
        <Select options={options} {...props} />
    );
}

export default PublicPointStatusSelect;