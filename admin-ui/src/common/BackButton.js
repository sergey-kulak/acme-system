import useHistoryBack from './useHistoryBack';

function BackButton({ defaultPath,
    className = "btn btn-secondary",
    children, ...props }) {
    const historyBack = useHistoryBack(defaultPath);

    return (
        <button type="button" className={className}
            {...props}
            onClick={historyBack}>
            {children ? children : 'Cancel'}
        </button>
    );
}

export default BackButton;