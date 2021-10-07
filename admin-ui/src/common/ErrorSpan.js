function ErrorSpan({ children }) {

    return (
        <small className="form-text text-danger">
            <span >
                {children}
            </span >
        </small >
    )
}

export default ErrorSpan