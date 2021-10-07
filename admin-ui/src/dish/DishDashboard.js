import { useCallback, useEffect, useState } from 'react'
import * as Icon from 'react-feather'
import { connect } from "react-redux"
import { Link, useHistory, useLocation } from "react-router-dom"
import Pagination from '../common/Pagination'
import { combineAsUrlParams, Pageable, Sort } from '../common/paginationUtils'
import { hasRole, ROLE } from "../common/security"
import ShowFilterButton from '../common/ShowFilterButton'
import SortColumn from '../common/SortColumn'
import { onError, onSuccess } from '../common/toastNotification'
import { getErrorMessage } from '../common/utils'
import dishService from './dishService'
import DishFilter from './DishFilter'
import DishStatusLabel from './DishStatusLabel'
import fileService from '../common/fileService'
import publicPointService from '../public-point/publicPointService'

function DishDashboard({ auth, onError, onSuccess }) {
    const history = useHistory()
    const query = new URLSearchParams(useLocation().search)

    const [page, setPage] = useState({ content: [] })
    const [pageable, setPageable] = useState(Pageable.fromUrlParams(query))
    const [sort, setSort] = useState(Sort.fromUrlParams(query, 'name'))
    const [filter, setFilter] = useState(Filter.fromUrlParams(query, auth))
    const [showFilter, setShowFilter] = useState(true)
    const [imageUrls, setImageUrls] = useState({})
    const [publicPoint, setPublicPoint] = useState()

    const loadData = useCallback(() => {
        return dishService.find(filter, pageable, sort)
            .then(response => response.data)
    }, [filter, pageable, sort])

    const loadImages = useCallback((filter, data) => {
        if (!data.content.length) {
            return Promise.resolve(data)
        }
        let request = {
            companyId: filter.companyId,
            publicPointId: filter.publicPointId,
            action: 'DOWNLOAD',
            imageKeys: data.content.map(dish => dish.primaryImage)
        }

        return fileService.getDishImageUrls(request)
            .then(response => setImageUrls(response.data))
            .then(() => data)
    }, [])

    useEffect(() => {
        if (filter.companyId && filter.publicPointId) {
            loadData()
                .then(data => loadImages(filter, data))
                .then(setPage)
                .then(() => {
                    let filterUrlParams = filter.toUrlParams(auth)
                    history.replace(combineAsUrlParams(filterUrlParams, pageable, sort))
                })
        } else {
            setPage({ content: [] })
        }
    }, [pageable, sort, filter, history, auth, loadData, loadImages])

    function onPageableChange(page) {
        setPageable(page)
    }

    function onSortChange(sort) {
        setSort(sort)
    }

    function onFilterChange(filter) {
        setFilter(filter)
    }

    function onDishStatusClick(e, dish) {
        e.preventDefault()
        if (window.confirm(`Are you sure to delete ${dish.name}`)) {
            dishService.delete(dish.id)
                .then(() => {
                    onSuccess(`${dish.name} dish was deleted successfuly`)
                    loadData()
                }, error => onError(getErrorMessage(error.response.data)))
        }
    }

    useEffect(() => {
        if (filter.publicPointId) {
            publicPointService.findByIdFullDetails(filter.publicPointId)
                .then(response => response.data)
                .then(setPublicPoint)
        }
    }, [filter.publicPointId])

    const canEdit = hasRole(auth, ROLE.PP_MANAGER)
    const priceColumnName = 'Price' + (publicPoint ? ', ' + publicPoint.currency : '')

    return (
        <div className="main-content">
            <div className="d-flex align-items-center mb-3">
                <div className="main-content-title pb-0">
                    Dishes
                </div>
                <ShowFilterButton filter={filter} showFilter={showFilter}
                    className="btn btn-light cmt-btn ml-3"
                    onClick={e => setShowFilter(!showFilter)} />
                {filter.companyId && filter.publicPointId && canEdit &&
                    <Link className="btn btn-light cmt-btn ml-2"
                        to={{
                            pathname: "/dishes/new",
                            state: { companyId: filter.companyId, publicPointId: filter.publicPointId }
                        }}>
                        <Icon.PlusSquare className="filter-icon" />
                    </Link>}
            </div>

            {showFilter && <DishFilter auth={auth} filter={filter} onChange={onFilterChange} />}
            {filter.companyId && filter.publicPointId && <div className="main-content-body">
                <table className="table table-striped table-hover table-responsive-md">
                    <thead>
                        <tr>
                            <th className="status-th" />
                            <th className="thumbnail-th" />
                            <SortColumn field="name" name="Name" sort={sort} onClick={onSortChange} />
                            <th>Description</th>
                            <th>Composition</th>
                            <SortColumn field="price" name={priceColumnName} sort={sort} onClick={onSortChange} />
                            <th>Tags</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            page.content.map(dish => <tr key={dish.id}>
                                <td className="align-middle">
                                    {(dish.deleted || !canEdit) ?
                                        <DishStatusLabel status={dish.deleted} /> :
                                        <a href="#status" onClick={e => onDishStatusClick(e, dish)}>
                                            <DishStatusLabel status={dish.deleted} />
                                        </a>
                                    }
                                </td>
                                <td>
                                    <img src={imageUrls[dish.primaryImage]} alt="" className="thumbnail" />
                                </td>
                                <td>
                                    <Link to={`/dishes/${dish.id}`}>
                                        {dish.name}
                                    </Link>
                                </td>
                                <td>{dish.description}</td>
                                <td>{dish.composition}</td>
                                <td>{dish.price}</td>
                                <td>{(dish.tags || []).join(', ')}</td>
                            </tr>)}
                    </tbody>
                </table>
                <Pagination className="mt-4" page={page} onPageableChange={onPageableChange} />
            </div>}
        </div>
    )
}

class Filter {
    static URL_PARAM_COMPANY_ID = 'cmp'
    static URL_PARAM_PP_ID = 'pp'
    static URL_PARAM_NAME_PATTERN = 'np'
    static URL_PARAM_WITH_DELETED = 'wd'

    constructor(companyId, publicPointId, namePattern, withDeleted) {
        this.publicPointId = publicPointId
        this.companyId = companyId
        this.namePattern = namePattern
        this.withDeleted = withDeleted
    }

    withNewValue(field, value) {
        let newFilter = new Filter(this.companyId, this.publicPointId, this.namePattern, this.withDeleted)
        newFilter[field] = value
        if (field === 'companyId') {
            newFilter.publicPointId = undefined
        }
        return newFilter
    }

    toUrlParams(auth) {
        let urlData = {
            [Filter.URL_PARAM_COMPANY_ID]: this.companyId,
            [Filter.URL_PARAM_PP_ID]: this.publicPointId,
            [Filter.URL_PARAM_NAME_PATTERN]: this.namePattern,
            [Filter.URL_PARAM_WITH_DELETED]: this.withDeleted
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
        return new Filter(companyId, ppId,
            urlSearchParams.get(Filter.URL_PARAM_NAME_PATTERN) || '',
            urlSearchParams.get(Filter.URL_PARAM_WITH_DELETED) === 'true' || false,
        )
    }
}

const mapStateToProps = ({ auth }) => {
    return { auth }
}

export default connect(mapStateToProps, {
    onSuccess, onError
})(DishDashboard)