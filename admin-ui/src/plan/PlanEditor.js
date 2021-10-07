import { Field, Form, Formik } from 'formik'
import { useEffect, useState } from "react"
import { connect } from 'react-redux'
import { useParams } from "react-router-dom"
import * as Yup from 'yup'
import BackButton from "../common/BackButton"
import planService from './planService'
import HighlightInput from '../common/HighlightInput'
import useHistoryBack from '../common/useHistoryBack'
import { onError, onSuccess } from '../common/toastNotification'
import CountrySelect from '../common/rf-data/CountrySelect'
import CurrencySelect from '../common/rf-data/CurrencySelect'
import PlanStatusSelect from './PlanStatusSelect'

function PlanEditor({ auth, onSuccess, onError }) {
    const { id } = useParams()
    const isCreate = id === 'new'
    const [plan, setPlan] = useState()
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        maxTableCount: '',
        monthPrice: '',
        currency: '',
        upfrontDiscount6m: '',
        upfrontDiscount1y: '',
        countries: []
    })
    const historyBack = useHistoryBack("/plans")

    const validationSchema = Yup.object({
        name: Yup.string().required('Required'),
        maxTableCount: Yup.number().required('Required'),
        monthPrice: Yup.number().typeError('Must be a number').required('Required'),
        currency: Yup.string().required('Required'),
        countries: Yup.array().of(Yup.string()),
        upfrontDiscount6m: percentSchema(),
        upfrontDiscount1y: percentSchema(),
    })

    function percentSchema() {
        return Yup.number()
            .typeError('Must be a number')
            .max(100, 'Not greater than 100')
    }

    useEffect(() => {
        if (!isCreate) {
            planService.findById(id)
                .then(response => {
                    setPlan(response.data)
                    toFormData(response.data)
                })
        }
    }, [id, isCreate])

    function toFormData(plan) {
        setFormData({
            name: plan.name,
            description: plan.description || '',
            maxTableCount: plan.maxTableCount,
            monthPrice: plan.monthPrice,
            currency: plan.currency,
            upfrontDiscount6m: plan.upfrontDiscount6m || '',
            upfrontDiscount1y: plan.upfrontDiscount1y || '',
            countries: plan.countries || []
        })
    }

    function onSubmit(formData) {
        let request = { ...formData }
        if (isCreate) {
            planService.create(request)
                .then(() => {
                    onSuccess(`${request.name} plan was created successfuly`)
                    historyBack()
                }, error => {
                    let errorMessage = error.response.data.error
                    onError(errorMessage || 'Error')
                })
        } else {
            planService.update(plan.id, request)
                .then(() => {
                    onSuccess(`${request.name} plan was updated successfuly`)
                    historyBack()
                }, error => {
                    let errorMessage = error.response.data.error
                    onError(errorMessage || 'Error')
                })
        }
    }

    const isEditable = !plan || plan.status === 'INACTIVE'

    return (
        <div className="main-content">
            <div>
                <div className="main-content-title mb-2">
                    {plan ? plan.name : 'Plan creation'}
                </div>
                <div className="main-content-body">
                    <Formik enableReinitialize
                        initialValues={formData}
                        validationSchema={validationSchema}
                        onSubmit={onSubmit}>
                        <Form noValidate={true}>
                            <div className="form-row">
                                <div className="form-group col-md-6">
                                    <label htmlFor="name">Name</label>
                                    <Field component={HighlightInput} name="name"
                                        type="text" className="form-control" readOnly={!isEditable} />
                                </div>
                                <div className="form-group col-md-3">
                                    <label htmlFor="maxTableCount">Max table count</label>
                                    <Field component={HighlightInput} name="maxTableCount"
                                        type="text" className="form-control"
                                        readOnly={!isEditable} />
                                </div>
                            </div>
                            <div className="form-group">
                                <label htmlFor="description">Description</label>
                                <Field component={HighlightInput} name="description"
                                    tag="textarea" rows={2} className="form-control"
                                    readOnly={!isEditable} />
                            </div>
                            <div className="form-row">
                                <div className="form-group col-md-3">
                                    <label htmlFor="monthPrice">Month price</label>
                                    <Field component={HighlightInput} name="monthPrice"
                                        type="text" className="form-control"
                                        readOnly={!isEditable} />
                                </div>
                                <div className="form-group col-md-3">
                                    <label htmlFor="currency">Currency</label>
                                    <Field component={CurrencySelect} name="currency"
                                        isDisabled={!isEditable} />
                                </div>
                                <div className="form-group col-md-3">
                                    <label htmlFor="upfrontDiscount6m">6 monts upfront discount</label>
                                    <Field component={HighlightInput} name="upfrontDiscount6m"
                                        type="text" className="form-control" readOnly={!isEditable} />
                                </div>
                                <div className="form-group col-md-3">
                                    <label htmlFor="upfrontDiscount1y">1 year upfront discount</label>
                                    <Field component={HighlightInput} name="upfrontDiscount1y"
                                        type="text" className="form-control" readOnly={!isEditable} />
                                </div>
                            </div>
                            <div className="form-row">
                                <div className="form-group col-md-6">
                                    <label htmlFor="countries">Country</label>
                                    <Field component={CountrySelect} name="countries"
                                        isMulti isDisabled={!isEditable} />
                                </div>
                                {!isEditable && <div className="form-group col-md-3">
                                    <label htmlFor="status">Status</label>
                                    <PlanStatusSelect name="status" 
                                        value={plan.status} isDisabled> 
                                    </PlanStatusSelect >
                                </div>}
                            </div>
                            {isEditable && <button type="submit" className="btn btn-primary mr-2">Save</button>}
                            <BackButton defaultPath="/users">Back</BackButton>
                        </Form>
                    </Formik>
                </div>
            </div>
        </div>
    )
}

const mapStateToProps = ({ auth }) => {
    return { auth }
}

export default connect(mapStateToProps, {
    onSuccess, onError
})(PlanEditor)