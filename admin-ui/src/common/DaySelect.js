import { useIntl } from 'react-intl'
import Select from '../common/Select'
import { toOptions } from '../common/utils'

const STATUSES = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY',
    'FRIDAY', 'SATURDAY', 'SUNDAY']

function DaySelect(props) {
    const intl = useIntl()
    const options = toOptions(intl, STATUSES, 'day')

    return (
        <Select options={options} {...props} />
    )
}

export default DaySelect