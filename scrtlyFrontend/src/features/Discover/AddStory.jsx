import React, {useState} from 'react'
import {MdCancel} from "react-icons/md";
import {useDispatch, useSelector} from "react-redux";
import {createStory} from "../../Redux/Story/Action.js";
import toast from "react-hot-toast";

// eslint-disable-next-line react/prop-types
function AddStory({onClose}) {
    const [image, setImage] = useState(null)
    const dispatch = useDispatch()
    const { loading, error } = useSelector(state => state.story);


    const handleFileChange = (e) => {
        setImage(e.target.files[0])
    };

    const handleStoryCreation = (e) => {
        e.preventDefault()
        const formData = new FormData()
        formData.append('file', image)

        dispatch(createStory(formData))
            .then(() => {
                toast.success('Story created successfully.');
            })
            .catch(() => {
                toast.error(error);
            })
            .finally(() => {
                setImage(null);
                onClose();
            });
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
                <button type="submit" className='submit' disabled={loading}>
                    {loading ? "Sending..." : "Send"}
                </button>
            </form>
        </div>
    )
}

export {AddStory}