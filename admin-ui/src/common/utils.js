import { getIn } from 'formik';

export const isEmptyObject = (obj) => {
    for (let propName in obj) {
        if (!isEmpty(obj[propName])) {
            return false;
        }
    }
    return true;
}

export const isEmpty = (value) =>
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

export const toOptions = (intl, values, msgPrefix) => {
    return values.map(value => ({
        value: value,
        label: intl.formatMessage({ id: `${msgPrefix}.${value.toLowerCase()}` })
    })
    );
}

export const getErrorMessage = (errorData) => {
    return typeof errorData === 'string' ? errorData :
        (errorData && errorData.error) || 'Error';
}

export const deleteIfEmpty = (obj, prop) => {
    if (!obj[prop]) {
        delete obj[prop];
    }
}

export const byProperty = function (property) {
    var sortOrder = 1;
    if (property[0] === "-") {
        sortOrder = -1;
        property = property.substr(1);
    }
    return function (a, b) {
        var result = (a[property] < b[property]) ? -1 : (a[property] > b[property]) ? 1 : 0;
        return result * sortOrder;
    }
}