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

export const getErrorMessage = (errorData) => {
    return typeof errorData === 'string' ? errorData :
        (errorData && errorData.error) || 'Error';
}

export const deleteIfEmpty = (obj, prop) => {
    if (!obj[prop]) {
        delete obj[prop];
    }
}