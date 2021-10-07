import './ImageUpload.css'

function ImageUpload({ src, onChange, isDisabled }) {

    function onFileChange(event) {
        let file = event.target.files[0]
        if (onChange) {
            onChange(file)
        }
    }

    return (
        <div className="image-upload">
            <label htmlFor="file-input">
                <img src={src ? src : '/no-image-icon.png'}
                    alt="preview" />
            </label>

            {!isDisabled && <input id="file-input" type="file" onChange={onFileChange} />}
        </div>
    )
}

export default ImageUpload