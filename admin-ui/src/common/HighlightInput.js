import { getIn } from 'formik';

function HighlightInput({
    field,
    form: { touched, errors },
    ...props
}) {

    function getValidClass() {
        return getIn(touched, field.name) && getIn(errors, field.name) ? 'invalid' : ''
    }

    function createElement() {
        field.value = field.value === undefined ? '' : field.value;
        let className = `${props.className} ${getValidClass()}`
        switch (props.tag || 'input') {
            case 'textarea':
                return <textarea {...field} {...props} className={className} />;
            default:
                return <input {...field} {...props} className={className} />

        }
    }
    
    const element = createElement();
    const error = getIn(errors, field.name);    

    return (
        <>
            {element}
            {
                getIn(touched, field.name) &&
                error &&
                <small className="form-text text-danger">
                    <span >
                        {error}
                    </span >
                </small >
            }

        </>
    );
}

export default HighlightInput;