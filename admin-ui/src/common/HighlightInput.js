import { getIn } from 'formik';

function HighlightInput({
    field,
    form: { touched, errors },
    ...props
}) {

    function getValidClass() {
        return touched[field.name] && getIn(errors, field.name) ? 'invalid' : ''
    }

    function createElement() {
        let className = `${props.className} ${getValidClass()}`
        switch (props.tag || 'input') {
            case 'textarea':
                return <textarea {...field} {...props} className={className} />;
            default:
                return <input {...field} {...props} className={className} />

        }
    }
    const element = createElement();
    return (
        <>
            {element}
            {
                touched[field.name] &&
                errors[field.name] &&
                <small className="form-text text-danger">
                    <span >
                        {errors[field.name]}
                    </span >
                </small >
            }

        </>
    );
}

export default HighlightInput;