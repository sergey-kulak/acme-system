import { getIn } from 'formik';

export const isEmptyObject = (obj) => {
    for (let propName in obj) {
        if (!isEmpty(obj[propName])) {
            return true;
        }
    }
    return false;
}

const isEmpty = (value) =>
    value === null || value === undefined
    || value === '' || (Array.isArray(value) && !value.length);

export const clearEmptyProps = (obj) => {
    for (let propName in obj) {
        if (isEmpty(obj[propName])) {
            delete obj[propName];
        }
    }
}

export const hasValidationError = (form, field) => {
    return form && form.touched && form.errors && field &&
        form.touched[field.name] && form.errors[field.name];
}

export const getValidationClass = (form, field) => {
    return form && field &&
        form.touched[field.name] && getIn(form.errors, field.name) ? 'invalid' : ''
}