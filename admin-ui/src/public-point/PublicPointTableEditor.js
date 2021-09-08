import { Field, Form, Formik } from 'formik';
import { useCallback, useEffect, useRef, useState } from "react";
import * as Icon from 'react-feather';
import { connect } from 'react-redux';
import { useHistory, useLocation } from 'react-router';
import * as Yup from 'yup';
import HighlightInput from '../common/HighlightInput';
import { combineAsUrlParams } from '../common/paginationUtils';
import { hasRole, ROLE } from '../common/security';
import { onError, onSuccess } from '../common/toastNotification';
import { getErrorMessage } from '../common/utils';
import publicPointPlanService from '../plan/publicPointPlanService';
import './PublicPointTableEditor.css';
import PublicPointTableFilter from './PublicPointTableFilter';
import publicPointTableService from './publicPointTableService';

const INIT_TABLE_DATA = { items: [], seq: 0, formItems: [], deleted: [] };

function PublicPointTableEditor({ auth, onSuccess, onError }) {
    const history = useHistory();
    const isAdmin = hasRole(auth, ROLE.ADMIN);
    const query = new URLSearchParams(useLocation().search);
    const [filter, setFilter] = useState(Filter.fromUrlParams(query, auth))
    const [tableData, setTableData] = useState(INIT_TABLE_DATA);
    const [isEdit, setIsEdit] = useState(false);
    const [plan, setPlan] = useState();
    const formikRef = useRef(null);

    const validationSchema = Yup.array().of(
        Yup.object().shape({
            name: Yup.string().required('Required'),
            seatCount: Yup.number()
                .typeError('Must be a number')
                .required('Required')
                .min(1, 'Min 1')
        })
    );

    function onFilterChange(filter) {
        setFilter(filter);
    }

    const loadTables = useCallback(() => {
        return publicPointTableService.find(filter.publicPointId)
            .then(response => response.data)
            .then(tables => {
                setTableData({
                    items: tables,
                    seq: tables.length,
                    formItems: [...tables],
                    deleted: []
                });
            })

    }, [filter]);

    useEffect(() => {
        if (filter.companyId && filter.publicPointId) {
            loadTables()
                .then(() => {
                    let filterUrlParams = filter.toUrlParams(auth);
                    history.replace(combineAsUrlParams(filterUrlParams));
                });
        } else {
            setTableData(INIT_TABLE_DATA);
        }
    }, [filter, history, auth, loadTables]);

    useEffect(() => {
        if (filter.companyId && filter.publicPointId) {
            publicPointPlanService.findActivePlan(filter.publicPointId)
                .then(response => response.data)
                .then(setPlan);
        } else {
            setPlan();
        }
    }, [filter, history, auth, loadTables]);

    function onEdit() {
        setIsEdit(true);
    }

    function onSave() {
        let formItems = getFormikValues();
        let changed = formItems.filter(item => !item.id || isChanged(item))
        if (!changed.length && !tableData.deleted.length) {
            setIsEdit(false);
            return;
        }
        let request = {
            publicPointId: filter.publicPointId,
            changed: changed,
            deleted: tableData.deleted
        };
        publicPointTableService.save(request)
            .then(() => {
                onSuccess(`Tables were updated successfuly`);
                setIsEdit(false);
                loadTables();
            }, error => onError(getErrorMessage(error.response.data)))
    }

    function isChanged(chItem) {
        return tableData.items.some(item => item.id === chItem.id
            && (item.name !== chItem.name || item.seatCount !== chItem.seatCount
                || item.description !== chItem.description));
    }

    function getFormikValues() {
        return formikRef.current && formikRef.current.values;
    }

    function onCancel() {
        setIsEdit(false);
        setTableData(prev => ({
            ...prev,
            formItems: [...prev.items],
            seq: prev.items.length
        }));
    }

    function addTable(e) {
        e.preventDefault();
        let formItems = getFormikValues();
        setTableData(prev => ({
            ...prev,
            formItems: formItems.concat([{
                name: `Table ${prev.seq + 1}`,
                seatCount: 5,
                description: ''
            }]),
            seq: prev.seq + 1
        }));
    }

    function deleteTable(e, table, index) {
        e.preventDefault();
        let formItems = getFormikValues();
        setTableData(prev => ({
            ...prev,
            formItems: formItems.filter((item, itemIndex) => itemIndex !== index),
            deleted: table.id ? prev.deleted.concat([table.id]) : prev.deleted,
        }));
    }

    return (
        <div className="main-content">
            <div>
                <div className="main-content-title mb-2">
                    Tables
                </div>
                <div className="main-content-body">
                    <PublicPointTableFilter isAdmin={isAdmin} filter={filter}
                        onChange={onFilterChange} plan={plan} />
                    {filter.companyId && filter.publicPointId && <div>
                        {isEdit ?
                            <Formik innerRef={formikRef}
                                enableReinitialize
                                initialValues={tableData.formItems}
                                validationSchema={validationSchema}
                                onSubmit={onSave}>
                                <Form noValidate>
                                    <div className="mb-3">
                                        <table className="table table-hover table-responsive-md mb-0">
                                            <TableHeader isEdit />
                                            <tbody>
                                                {
                                                    tableData.formItems.map((table, index) =>
                                                        <tr key={index}>
                                                            <td>{index + 1}</td>
                                                            <EditCell index={index} fieldName="name" />
                                                            <EditCell index={index} fieldName="seatCount" />
                                                            <EditCell index={index} fieldName="description" />
                                                            <td className="del-btn-td">
                                                                <a href="#delete"
                                                                    onClick={e => deleteTable(e, table, index)}>
                                                                    <Icon.XCircle className="filter-icon" />
                                                                </a>
                                                            </td>
                                                        </tr>
                                                    )}
                                            </tbody>
                                        </table>
                                        {tableData.formItems.length < plan.maxTableCount && <div className="add-btn-wrapper w-100 text-right">
                                            <a href="#add" className="add-btn"
                                                onClick={addTable}>
                                                <Icon.PlusCircle className="filter-icon" />
                                            </a>
                                        </div>}
                                    </div>
                                    <div>
                                        <button type="submit" className="btn btn-primary mr-2">Save</button>
                                        <button type="button" className="btn btn-secondary" onClick={onCancel}>Cancel</button>
                                    </div>
                                </Form>
                            </Formik> :
                            <div>
                                <div className="mb-3">
                                    <table className="table table-hover table-responsive-md mb-0">
                                        <TableHeader />
                                        <tbody>
                                            {
                                                tableData.formItems.map((table, index) =>
                                                    <tr key={index}>
                                                        <td>{index + 1}</td>
                                                        <td>{table.name}</td>
                                                        <td>{table.seatCount}</td>
                                                        <td>{table.description}</td>
                                                    </tr>
                                                )}
                                        </tbody>
                                    </table>
                                </div>
                                {!!plan && <div>
                                    <button type="button" className="btn btn-primary mr-2"
                                        onClick={onEdit}>Edit</button>
                                </div>}
                            </div>}
                    </div>}
                </div>
            </div >
        </div >
    )
}

class Filter {
    static URL_PARAM_COMPANY_ID = 'cmp';
    static URL_PARAM_PP_ID = 'pp';;

    constructor(companyId, publicPointId) {
        this.companyId = companyId;
        this.publicPointId = publicPointId;
    }

    withNewValue(field, value) {
        let newFilter = new Filter(this.companyId, this.publicPointId);
        newFilter[field] = value;
        if (field === 'companyId') {
            newFilter.publicPointId = undefined;
        }
        return newFilter;
    }

    toUrlParams(auth) {
        let urlData = {
            [Filter.URL_PARAM_COMPANY_ID]: this.companyId,
            [Filter.URL_PARAM_PP_ID]: this.publicPointId
        };

        if (!hasRole(auth, ROLE.ADMIN)) {
            delete urlData[Filter.URL_PARAM_COMPANY_ID];
        }
        return { toUrlParams: () => urlData };
    }

    static fromUrlParams(urlSearchParams, auth) {
        let companyId = hasRole(auth, ROLE.ADMIN) ?
            urlSearchParams.get(Filter.URL_PARAM_COMPANY_ID) || '' :
            auth.user.cmpid;

        return new Filter(companyId,
            urlSearchParams.get(Filter.URL_PARAM_PP_ID) || ''
        );
    }
}

function TableHeader({ isEdit }) {
    return (
        <thead>
            <tr>
                <th style={{ width: '1%' }}>#</th>
                <th style={{ width: '30%' }}>Name</th>
                <th style={{ width: '6%' }}>Seats</th>
                <th>Description</th>
                {isEdit && <th style={{ width: '1%' }} />}
            </tr>
        </thead>
    );
}

function EditCell({ index, fieldName }) {
    return <td>
        <Field name={`${index}.${fieldName}`}
            component={HighlightInput} className="edit-cell" />
    </td>
}

const mapStateToProps = ({ auth }) => {
    return { auth };
};

export default connect(mapStateToProps, {
    onSuccess, onError
})(PublicPointTableEditor);