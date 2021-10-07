import { useIntl } from 'react-intl'
import Select from '../common/Select'
import { toOptions } from '../common/utils'

const STATUSES = ['INACTIVE', 'ACTIVE', 'STOPPED']

function PlanStatusSelect(props) {
    const intl = useIntl()
    const options = toOptions(intl, STATUSES, 'plan.status')

    return (
        <Select options={options} {...props} />
    )
}

export default PlanStatusSelect