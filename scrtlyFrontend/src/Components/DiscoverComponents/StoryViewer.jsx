import React, {useEffect, useRef, useState} from 'react'
import {FaChevronLeft, FaChevronRight} from "react-icons/fa";
import {BASE_API_URL} from "../../config/api.js";

// eslint-disable-next-line react/prop-types
function StoryViewer({ stories, currentUserIndex, currentStoryIndex, onClose }) {
    const [currentStory, setCurrentStory] = useState(currentStoryIndex)
    const [progress, setProgress] = useState(0)
    const [userIndex, setUserIndex] = useState(currentUserIndex)
    const intervalRef = useRef()

    const userEntries = Object.entries(stories);
    const currentUserStories = userEntries[userIndex]?.[1] || []

    const handleNextStory = () => {
        if (currentStory < currentUserStories.length - 1) {
            setCurrentStory((prevStory) => prevStory + 1)
        } else {
            handleNextUser()
        }
        setProgress(0)
    }

    const handlePreviousStory = () => {
        if (currentStory > 0) {
            setCurrentStory((prevStory) => prevStory - 1)
        } else {
            handlePreviousUser()
        }
        setProgress(0)
    }

    const handleNextUser = () => {
        if (userIndex < userEntries.length - 1) {
            setUserIndex((prevIndex) => prevIndex + 1)
            setCurrentStory(0)
        } else {
            onClose()
        }
        setProgress(0)
    }

    const handlePreviousUser = () => {
        if (userIndex > 0) {
            setUserIndex((prevIndex) => prevIndex - 1)
            setCurrentStory(userEntries[userIndex - 1]?.[1]?.length - 1 || 0)
        } else {
            onClose()
        }
        setProgress(0)
    }

    useEffect(() => {
        if (intervalRef.current) {
            clearInterval(intervalRef.current)
        }

        intervalRef.current = setInterval(() => {
            setProgress((prevProgress) => {
                if (prevProgress >= 100) {
                    handleNextStory()
                    return 0;
                }
                return prevProgress + 2
            });
        }, 100)

        return () => clearInterval(intervalRef.current)
    }, [currentStory, userIndex])


    if (!currentUserStories.length) {
        return (
            <div className="story-viewer-modal">
                <button className="close-button" onClick={onClose}>X</button>
                <p>No stories available</p>
            </div>
        )
    }

    return (
        <div className="story-viewer-modal">
            <button className="close-button" onClick={onClose}>X</button>
            <button className="nav-button left" onClick={handlePreviousStory}>
                <FaChevronLeft />
            </button>
            <div className="story-content">
                <div className="progress-bar">
                    <div className="progress" style={{width: `${progress}%`}}></div>
                </div>
                <img src={`${BASE_API_URL}${currentUserStories[currentStory]?.image}`} alt={`Story ${currentStory}`} />
                <p>{currentUserStories[0]?.user?.fullName}</p>
            </div>
            <button className="nav-button right" onClick={handleNextStory}>
                <FaChevronRight />
            </button>
        </div>
    )
}

export { StoryViewer }
