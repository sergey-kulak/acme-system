import { useIntl } from 'react-intl';
import Select from '../common/Select';
import { toOptions } from '../common/utils';

const STATUSES = ['CREATED', 'CONFIRMED', 'IN_PROGRESS',
    'READY', 'DELIVERED', 'PAID', 'DECLINED'];

function OrderStatusSelect(props) {
    const intl = useIntl();
    const options = toOptions(intl, STATUSES, 'order.status');

    return (
        <Select options={options} {...props} />
    );
}

export default OrderStatusSelect;