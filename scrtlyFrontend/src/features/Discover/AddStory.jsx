import React, {useState} from 'react'
import {MdCancel} from "react-icons/md";
import toast from "react-hot-toast";
import {useCreateStoryMutation} from "../../Redux/services/storyApi.js";

// eslint-disable-next-line react/prop-types
function AddStory({onClose}) {
    const [image, setImage] = useState(null)
    const [createStory, { isLoading }] = useCreateStoryMutation();

    const handleFileChange = (e) => {
        setImage(e.target.files[0])
    };

    const handleStoryCreation = async (e) => {
        e.preventDefault()

        if (!image) {
            toast.error('Please pick an image first');
            return;
        }

        const formData = new FormData();
        formData.append('file', image);

        try {
            await createStory(formData).unwrap();
            toast.success('Story created successfully.');
            onClose();
        } catch (err) {
            toast.error(err.data.businessErrornDescription);
        } finally {
            setImage(null);
        }
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
                <button type="submit" className='submit' disabled={isLoading}>
                    {isLoading ? "Sending..." : "Send"}
                </button>
            </form>
        </div>
    )
}

export {AddStory}