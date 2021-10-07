import { useEffect, useState } from 'react'
import Select from '../Select'
import rfDataService from './rfDataService'

function CurrencySelect(props) {
    const [options, setOptions] = useState([])

    useEffect(() => {
        function mapToOption(currency) {
            return {
                value: currency.code,
                label: `${currency.name}, ${currency.symbol}`,
                data: currency
            }
        }

        rfDataService.findCurrencies()
            .then(response => response.data)
            .then(data => setOptions(data.map(mapToOption)))
    }, [])

    return (
        <Select options={options} {...props} />
    )
}

export default CurrencySelect