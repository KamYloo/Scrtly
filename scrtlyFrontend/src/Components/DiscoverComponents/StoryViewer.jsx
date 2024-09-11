import React, {useEffect, useRef, useState} from 'react'
import {FaChevronLeft, FaChevronRight} from "react-icons/fa";
import {useDispatch} from "react-redux";


// eslint-disable-next-line react/prop-types
function StoryViewer({ stories, currentUserIndex, onClose }) {
    const [currentStoryIndex, setCurrentStoryIndex] = useState(currentUserIndex)
    const [currentImageIndex, setCurrentImageIndex] = useState(0)
    const [progress, setProgress] = useState(0)
    const intervalRef = useRef()
    const currentUser = stories[currentStoryIndex]

    const handleNextImage  = () => {
        if (currentImageIndex < currentUser.storyImages.length - 1) {
            setCurrentImageIndex((prevIndex) => prevIndex + 1)
        } else {
            handleNextUser()
        }
        setProgress(0)
    };

    const handlePreviousImage  = () => {
        if (currentImageIndex > 0) {
            setCurrentImageIndex((prevIndex) => prevIndex - 1)
        } else {
            handlePreviousUser()
        }
        setProgress(0)
    };

    const handleNextUser = () => {
        if (currentStoryIndex < stories.length - 1) {
            setCurrentStoryIndex((prevIndex) => prevIndex + 1)
            setCurrentImageIndex(0)
        } else {
            setCurrentStoryIndex(0)
            setCurrentImageIndex(0)
        }
        setProgress(0)
    };

    const handlePreviousUser = () => {
        if (currentStoryIndex > 0) {
            setCurrentStoryIndex((prevIndex) => prevIndex - 1)
            setCurrentImageIndex(stories[currentStoryIndex - 1].storyImages.length - 1)
        } else {
            setCurrentStoryIndex(stories.length - 1)
            setCurrentImageIndex(stories[stories.length - 1].storyImages.length - 1)
        }
        setProgress(0)
    };

    useEffect(() => {
        intervalRef.current = setInterval(() => {
            setProgress((prevProgress) => {
                if (prevProgress >= 100) {
                    handleNextImage()
                    return 0
                }
                return prevProgress + 2
            });
        }, 100)

        return () => clearInterval(intervalRef.current)
    }, [currentImageIndex, currentStoryIndex])


    return (
        <div className="story-viewer-modal">
            <button className="close-button" onClick={onClose}>X</button>
            <button className="nav-button left" onClick={handlePreviousImage}>
                <FaChevronLeft/>
            </button>
            <div className="story-content">
                <div className="progress-bar">
                    <div className="progress" style={{width: `${progress}%`}}></div>
                </div>
                <img src={currentUser.storyImages[currentImageIndex]} alt={`Story ${currentImageIndex}`}/>
                <p>{currentUser.username}</p>
            </div>
            <button className="nav-button right" onClick={handleNextImage}>
                <FaChevronRight/>
            </button>
        </div>
    );
}

export {StoryViewer}