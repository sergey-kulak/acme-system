import { getIn } from 'formik';

function HighlightInput({
    field,
    form: { touched, errors },
    ...props
}) {

    function getStyles(errors, fieldName) {
        if (touched[field.name] && getIn(errors, fieldName)) {
            return {
                'borderColor': 'red'
            }
        }
    }

    function createElement() {
        switch (props.tag || 'input') {
            case 'textarea':
                return <textarea {...field} {...props} style={getStyles(errors, field.name)} />;
            default:
                return <input {...field} {...props} style={getStyles(errors, field.name)} />

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