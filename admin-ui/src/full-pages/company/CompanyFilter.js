import {DebounceInput} from 'react-debounce-input';

function CompanyFilter({ filter, onChange }) {

    function handleChange(e) {
        onChange({
            ...filter,
            [e.target.name]: e.target.value
        });
    }
    
    return (
        <div className="form-row">
            <div className="form-group col-lg-3 col-md-6">
                <label htmlFor="namePattern">Name</label>
                <DebounceInput name="namePattern" onChange={handleChange}
                    value={filter.namePattern} debounceTimeout="300"
                    type="text" className="form-control" />
            </div>
            <div className="form-group col-lg-3 col-md-6">
                <label htmlFor="vatin">VATIN</label>
                <input name="vatin" onChange={handleChange}
                    value={filter.vatin}
                    type="text" className="form-control" />
            </div>
            <div className="form-group col-lg-3 col-md-6">
                <label htmlFor="country">Country</label>
                <input name="country" onChange={handleChange}
                    value={filter.country}
                    type="text" className="form-control" />
            </div>
            <div className="form-group col-lg-3 col-md-6">
                <label htmlFor="status">Status</label>
                <select name="status" className="form-control"
                    value={filter.status} onChange={handleChange}>
                    <option value="">All</option>
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                </select>
            </div>
        </div>
    );
}

export default CompanyFilter;