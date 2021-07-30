export const isEmptyObject = (obj) => {
    for (var propName in obj) {
        if (obj[propName] !== null || obj[propName] !== undefined || obj[propName] !== '') {
            return true;
        }
    }
    return false;
}