import React, {useState} from 'react'
import {MdCancel} from "react-icons/md";
import {useDispatch} from "react-redux";
import {createStory} from "../../Redux/Story/Action.js";

// eslint-disable-next-line react/prop-types
function AddStory({onClose}) {
    const [image, setImage] = useState(null)
    const dispatch = useDispatch()

    const handleFileChange = (e) => {
        setImage(e.target.files[0])
    };

    const handleStoryCreation = () => {
        const formData = new FormData()
        formData.append('file', image)
        dispatch(createStory(formData))
        setImage(null)
    }

    return (
        <div className='addStory'>
            <i className='cancel' onClick={onClose}><MdCancel/></i>
            <div className="title">
                <h2>AddStory</h2>
            </div>
            <form onSubmit={handleStoryCreation}>
                <div className="pickImage">
                    <input type="file" onChange={handleFileChange}/>
                    <button type="button" onClick={() => document.querySelector('input[type="file"]').click()}>
                        Pick Image
                    </button>
                </div>
                <button type="submit" className='submit'>Send</button>
            </form>
        </div>
    )
}

export {AddStory}