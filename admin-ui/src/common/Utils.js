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