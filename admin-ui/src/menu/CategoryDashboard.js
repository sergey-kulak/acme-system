import { useCallback, useEffect, useState } from 'react'
import * as Icon from 'react-feather'
import { connect } from "react-redux"
import { useIntl } from 'react-intl'
import { Link, useHistory, useLocation } from "react-router-dom"
import { combineAsUrlParams } from '../common/paginationUtils'
import { hasRole, ROLE } from "../common/security"
import ShowFilterButton from '../common/ShowFilterButton'
import { onError, onSuccess } from '../common/toastNotification'
import CategoryFilter from './CategoryFilter'
import categoryService from './categoryService'

function CategoryDashboard({ auth, onError, onSuccess }) {
    const history = useHistory()
    const intl = useIntl()
    const query = new URLSearchParams(useLocation().search)

    const [categories, setCategories] = useState([])
    const [editCategories, setEditCategories] = useState([])
    const [filter, setFilter] = useState(Filter.fromUrlParams(query, auth))
    const [showFilter, setShowFilter] = useState(true)
    const [isEdit, setIsEdit] = useState(false)

    const loadData = useCallback(() => {
        return categoryService.find(filter)
            .then(response => response.data)
            .then(setCategories)
    }, [filter])

    useEffect(() => {
        if (filter.companyId && filter.publicPointId) {
            loadData()
                .then(() => {
                    let filterUrlParams = filter.toUrlParams(auth)
                    history.replace(combineAsUrlParams(filterUrlParams))
                })
        } else {
            setCategories([])
        }
    }, [filter, history, auth, loadData])

    function onFilterChange(filter) {
        setFilter(filter)
    }

    function onEdit() {
        setIsEdit(true)
        setEditCategories(categories.map(ctg => ({ ...ctg })))
    }

    function onCancel() {
        setIsEdit(false)
    }

    function onSave() {
        let request = {
            companyId: filter.companyId,
            publicPointId: filter.publicPointId,
            categoryIds: editCategories.map(item => item.id)
        }
        categoryService.updateOrder(request)
            .then(() => {
                onSuccess(`Menu category order was changed successfuly`)
                loadData()
                    .then(() => setIsEdit(false))
            }, error => {
                let errorMessage = error.response.data.error
                onError(errorMessage || 'Error')
            })

    }

    function deleteCategory(e, category) {
        e.preventDefault()
        setEditCategories(prev => prev.filter(prevItem => prevItem !== category))
    }

    function onUp(e, ctg, index) {
        e.preventDefault()
        ctg.position = index - 1
        editCategories[index - 1].position = index
        setEditCategories(sort([...editCategories]))
    }

    function onDown(e, ctg, index) {
        e.preventDefault()
        ctg.position = index + 1
        editCategories[index + 1].position = index
        setEditCategories(sort([...editCategories]))
    }

    function sort(items) {
        items.sort((a, b) => {
            let fa = a.position,
                fb = b.position

            if (fa < fb) {
                return -1
            }
            if (fa > fb) {
                return 1
            }
            return 0
        })

        return items
    }

    function days(category) {
        return category.days &&
            category.days.map(day => intl.formatMessage({ id: `day.${day.toLowerCase()}` })).join(', ')
    }

    let displayCategories = isEdit ? editCategories : categories
    const canEdit = hasRole(auth, ROLE.PP_MANAGER)

    return (
        <div className="main-content">
            <div className="d-flex align-items-center mb-3">
                <div className="main-content-title pb-0">
                    Menu
                </div>
                {!auth.user.ppid && <ShowFilterButton filter={filter} showFilter={showFilter}
                    className="btn btn-light cmt-btn ml-3"
                    onClick={e => setShowFilter(!showFilter)} />}
                {filter.companyId && filter.publicPointId && canEdit &&
                    <Link className="btn btn-light cmt-btn ml-2"
                        to={{
                            pathname: "/menu/categories/new",
                            state: { companyId: filter.companyId, publicPointId: filter.publicPointId }
                        }}>
                        <Icon.PlusSquare className="filter-icon" />
                    </Link>}
            </div>

            {showFilter && <CategoryFilter auth={auth} filter={filter} onChange={onFilterChange} />}
            {filter.companyId && filter.publicPointId && <div className="main-content-body">
                <table className="table table-striped table-hover table-responsive-md">
                    <thead>
                        <tr>
                            <th style={{ width: '5%' }}>#</th>
                            <th>Name</th>
                            <th>Days</th>
                            <th style={{ width: '10%' }}>Start time</th>
                            <th style={{ width: '10%' }}>End time</th>
                            <th style={{ width: '12%' }}>Dish count</th>
                            {isEdit && <th style={{ width: '8%' }} />}
                        </tr>
                    </thead>
                    <tbody>
                        {
                            displayCategories.map((ctg, index) => <tr key={ctg.id}>
                                <td>{ctg.position + 1}</td>
                                <td>
                                    <Link to={`/menu/categories/${ctg.id}`}>
                                        {ctg.name}
                                    </Link>
                                </td>
                                <td>{days(ctg)}</td>
                                <td>{ctg.startTime}</td>
                                <td>{ctg.endTime}</td>
                                <td>{ctg.dishIds.length}</td>
                                {isEdit && <td className="del-btn-td text-right">
                                    {index > 0 && <a href="#up" onClick={e => onUp(e, ctg, index)}>
                                        <Icon.ChevronUp className="filter-icon" />
                                    </a>}
                                    {index !== displayCategories.length - 1 &&
                                        <a href="#down" onClick={e => onDown(e, ctg, index)}>
                                            <Icon.ChevronDown className="filter-icon" />
                                        </a>}
                                    <a href="#delete"
                                        onClick={e => deleteCategory(e, ctg)}>
                                        <Icon.XCircle className="filter-icon" />
                                    </a>
                                </td>}
                            </tr>)}
                    </tbody>
                </table>
                {canEdit && <div>
                    {isEdit ? <div>
                        <button type="button" className="btn btn-primary mr-2" onClick={onSave}>Save</button>
                        <button type="button" className="btn btn-secondary" onClick={onCancel}>Cancel</button>
                    </div> :
                        <button type="button" className="btn btn-primary mr-2"
                            onClick={onEdit}>Edit</button>
                    }
                </div>}

            </div>}
        </div>
    )
}

class Filter {
    static URL_PARAM_COMPANY_ID = 'cmp'
    static URL_PARAM_PP_ID = 'pp'

    constructor(companyId, publicPointId) {
        this.publicPointId = publicPointId
        this.companyId = companyId
    }

    withNewValue(field, value) {
        let newFilter = new Filter(this.companyId, this.publicPointId)
        newFilter[field] = value
        if (field === 'companyId') {
            newFilter.publicPointId = undefined
        }
        return newFilter
    }

    toUrlParams(auth) {
        let urlData = {
            [Filter.URL_PARAM_COMPANY_ID]: this.companyId,
            [Filter.URL_PARAM_PP_ID]: this.publicPointId
        }

        if (!hasRole(auth, ROLE.ADMIN)) {
            delete urlData[Filter.URL_PARAM_COMPANY_ID]
        }
        if (!hasRole(auth, ROLE.COMPANY_OWNER)) {
            delete urlData[Filter.URL_PARAM_PP_ID]
        }
        return { toUrlParams: () => urlData }
    }

    static fromUrlParams(urlSearchParams, auth) {
        let companyId = hasRole(auth, ROLE.ADMIN) ?
            urlSearchParams.get(Filter.URL_PARAM_COMPANY_ID) || '' :
            auth.user.cmpid
        let ppId = hasRole(auth, ROLE.COMPANY_OWNER) ?
            urlSearchParams.get(Filter.URL_PARAM_PP_ID) || '' :
            auth.user.ppid
        return new Filter(companyId, ppId
        )
    }
}

const mapStateToProps = ({ auth }) => {
    return { auth }
}

export default connect(mapStateToProps, {
    onSuccess, onError
})(CategoryDashboard)