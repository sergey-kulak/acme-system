import { DebounceInput } from 'react-debounce-input';
import moment from "moment";
import DatePicker from "react-datepicker";
import { hasRole, ROLE } from '../common/security';
import CompanySelect from '../company/CompanySelect';
import PublicPointSelect from '../public-point/PublicPointSelect';
import OrderStatusSelect from './OrderStatusSelect';
import DishSelect from '../dish/DishSelect';

const FORMAT = 'yyyy-MM-DD';

function OrderFilter({ auth, filter, onChange }) {

    function onCompanyChange(companyId) {
        onChange(filter.withNewValue('companyId', companyId));
    }

    function onPublicPointChange(publicPointId) {
        onChange(filter.withNewValue('publicPointId', publicPointId));
    }

    function onStatusChange(status) {
        onChange(filter.withNewValue('status', status));
    }

    function onDishChange(dishId) {
        onChange(filter.withNewValue('dishId', dishId));
    }

    function handleChange(e) {
        onChange(filter.withNewValue(e.target.name, e.target.value));
    }

    function onCreatedDateChange(field, date) {
        onChange(filter.withNewValue(field, 
            date && moment(date).format(FORMAT)));
    }

    function toDate(value) {
        return value && new Date(value);
    }

    return (
        <div className="table-filter">
            <div className="form-row">
                {hasRole(auth, ROLE.ADMIN) && <div className="form-group col-lg-3 col-md-6">
                    <label htmlFor="company">Company</label>
                    <CompanySelect name="company" isClearable showTypeCheckBox
                        value={filter.companyId} onChange={onCompanyChange} />
                </div>}
                {hasRole(auth, ROLE.COMPANY_OWNER) && <div className="form-group col-lg-3 col-md-6">
                    <label htmlFor="company">Public point</label>
                    <PublicPointSelect name="pp" isClearable auth={auth}
                        isDisabled={!filter.companyId} companyId={filter.companyId}
                        value={filter.publicPointId} onChange={onPublicPointChange} />
                </div>}
                <div className="form-group col-lg-3 col-md-6">
                    <label htmlFor="number">Order number</label>
                    <DebounceInput name="number" onChange={handleChange}
                        value={filter.number} debounceTimeout="300"
                        readOnly={!filter.publicPointId}
                        type="text" className="form-control" />
                </div>
                <div className="form-group col-lg-3 col-md-6">
                    <label htmlFor="status">Status</label>
                    <OrderStatusSelect name="status" onChange={onStatusChange}
                        isDisabled={!filter.publicPointId}
                        value={filter.status} isClearable />
                </div>
                <div className="form-group col-lg-2 col-md-6">
                    <label htmlFor="fromTotalPrice">From price</label>
                    <DebounceInput name="fromTotalPrice" onChange={handleChange}
                        value={filter.fromTotalPrice} debounceTimeout="300"
                        readOnly={!filter.publicPointId}
                        type="text" className="form-control" />
                </div>
                <div className="form-group col-lg-2 col-md-6">
                    <label htmlFor="toTotalPrice">To price</label>
                    <DebounceInput name="toTotalPrice" onChange={handleChange}
                        value={filter.toTotalPrice} debounceTimeout="300"
                        readOnly={!filter.publicPointId}
                        type="text" className="form-control" />
                </div>
                <div className="form-group col-lg-2 col-md-6">
                    <label htmlFor="fromCreatedDate">Created from</label>
                    <DatePicker name="fromCreatedDate" selected={toDate(filter.fromCreatedDate)}
                        onChange={date => onCreatedDateChange('fromCreatedDate', date)}
                        dateFormat="yyyy-MM-dd" readOnly={!filter.publicPointId}
                        className="form-control" />
                </div>
                <div className="form-group col-lg-2 col-md-6">
                    <label htmlFor="toCreatedDate">Created till</label>
                    <DatePicker name="toCreatedDate" selected={toDate(filter.toCreatedDate)}
                        onChange={date => onCreatedDateChange('toCreatedDate', date)}
                        dateFormat="yyyy-MM-dd" readOnly={!filter.publicPointId}
                        className="form-control" />
                </div>
                <div className="form-group col-lg-4 col-md-6">
                    <label htmlFor="dishId">Dish</label>
                    <DishSelect companyId={filter.companyId} isClearable
                        publicPointId={filter.publicPointId}
                        value={filter.dishId} isDisabled={!filter.publicPointId}
                        onChange={onDishChange} />
                </div>
            </div>
        </div>
    )
}


export default OrderFilter;