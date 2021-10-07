import { useEffect, useState } from "react";
import { FormattedMessage } from "react-intl";
import { connect } from "react-redux";
import { useParams } from "react-router";
import BackButton from "../common/BackButton";
import orderService from "./orderService";
import companyService from "../company/companyService";
import publicPointService from "../public-point/publicPointService";
import dishService from "../dish/dishService";
import moment from "moment";
import publicPointTableService from "../public-point/publicPointTableService";

function OrderViewer({ auth }) {
    const params = useParams();
    const id = params.id;
    const [order, setOrder] = useState();
    const [company, setCompany] = useState();
    const [publicPoint, setPublicPoint] = useState();
    const [table, setTable] = useState();
    const [dishes, setDishes] = useState({});

    const labelClass = "col-sm-4 col-md-2 col-form-label text-sm-right font-italic";
    const controlClass = "col-sm-8 col-md-4";

    useEffect(() => {
        orderService.findById(id)
            .then(response => response.data)
            .then(setOrder);
    }, [id]);

    useEffect(() => {
        if (order) {
            companyService.findById(order.companyId)
                .then(response => response.data)
                .then(setCompany);
            publicPointService.findByIdFullDetails(order.publicPointId)
                .then(response => response.data)
                .then(setPublicPoint);
            publicPointTableService.findById(order.tableId)
                .then(response => response.data)
                .then(setTable);

            let dishPromises = order.items.map(item =>
                dishService.findById(item.dishId)
                    .then(response => response.data)
            )

            Promise.all(dishPromises)
                .then(data => {
                    let dishes = {};
                    data.forEach(item => dishes[item.id] = item);
                    setDishes(dishes);
                })
        }
    }, [order]);

    function priceLabel() {
        return order.totalPrice +
            (publicPoint ? ` ${publicPoint.currency}` : '');
    }

    function formatDate(date) {
        return moment(new Date(date)).format('DD.MM.yyyy HH:mm:ss')
    }

    function dishName(dishId) {
        let dish = dishes[dishId]
        return dish && dish.name;
    }

    return (
        <div className="main-content">
            {order && <div>
                <div className="main-content-title mb-2">
                    "{order.number}" order
                </div>
                <div className="main-content-body">
                    <div className="form-group row mb-0">
                        <label htmlFor="number" className={labelClass}>Number:</label>
                        <div className={controlClass}>
                            <input readOnly value={order.number} name="number"
                                type="text" className="form-control-plaintext" />
                        </div>
                        <label htmlFor="status" className={labelClass}>Status:</label>
                        <div className="col-form-label col-sm-6 col-md-4">
                            <FormattedMessage id={`order.status.${order.status.toLowerCase()}`} />
                        </div>
                    </div>
                    <div className="form-group row mb-0">
                        <label htmlFor="company" className={labelClass}>Company:</label>
                        <div className={controlClass}>
                            <input readOnly value={(company && company.fullName) || ''} name="company"
                                type="text" className="form-control-plaintext" />
                        </div>
                        <label htmlFor="publicPoint" className={labelClass}>Public point:</label>
                        <div className={controlClass}>
                            <input readOnly value={(publicPoint && publicPoint.name) || ''} name="publicPoint"
                                type="text" className="form-control-plaintext" />
                        </div>
                    </div>
                    <div className="form-group row mb-0">
                        <label htmlFor="table" className={labelClass}>Table:</label>
                        <div className={controlClass}>
                            <input readOnly value={(table && table.name) || ''} name="table"
                                type="text" className="form-control-plaintext" />
                        </div>
                        <label htmlFor="totalPrice" className={labelClass}>Total price:</label>
                        <div className={controlClass}>
                            <input readOnly value={priceLabel()} name="totalPrice"
                                type="text" className="form-control-plaintext" />
                        </div>
                    </div>
                    <div className="form-group row mb-0">
                        <label htmlFor="createdDate" className={labelClass}>Created date:</label>
                        <div className={controlClass}>
                            <input readOnly value={formatDate(order.createdDate)}
                                name="createdDate" type="text" className="form-control-plaintext" />
                        </div>
                        <label htmlFor="doneDate" className={labelClass}>Done date:</label>
                        <div className={controlClass}>
                            <input readOnly value={order.paidDate && formatDate(order.paidDate)}
                                name="doneDate" type="text" className="form-control-plaintext" />
                        </div>
                    </div>
                    <div className="mt-3">
                        <table className="table table-striped table-hover table-responsive-md">
                            <thead>
                                <tr>
                                    <th>Dish</th>
                                    <th style={{ width: '12%' }}>Quantity</th>
                                    <th style={{ width: '12%' }}>
                                        Price
                                        {publicPoint ? `, ${publicPoint.currency}` : ''}
                                    </th>
                                    <th style={{ width: '16%' }}>
                                        Total price
                                        {publicPoint ? `, ${publicPoint.currency}` : ''}
                                    </th>
                                    <th>Comment</th>
                                </tr>
                            </thead>
                            <tbody>
                                {
                                    order.items.map(item => <tr key={item.id}>
                                        <td>{dishName(item.dishId)}</td>
                                        <td>{item.quantity}</td>
                                        <td>{item.price}</td>
                                        <td>{item.price * item.quantity}</td>
                                        <td>{item.comment}</td>
                                    </tr>)}
                            </tbody>
                        </table>
                    </div>
                    <BackButton defaultPath="/orders">Back</BackButton>
                </div>
            </div>}
        </div >
    )
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps, {

})(OrderViewer);