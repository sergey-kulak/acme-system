import { useEffect, useState } from 'react'
import Select from '../Select'
import rfDataService from './rfDataService'

function LangSelect(props) {
    const [options, setOptions] = useState([])

    useEffect(() => {
        function mapToOption(lang) {
            return {
                value: lang.code,
                label: lang.name,
                data: lang
            }
        }

        rfDataService.findLangs()
            .then(response => response.data)
            .then(data => setOptions(data.map(mapToOption)))
    }, [])

    return (
        <Select options={options} {...props} />
    )
}

export default LangSelect