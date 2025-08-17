import React, {useEffect, useRef, useState} from 'react'
import {FaChevronLeft, FaChevronRight, FaTrash} from "react-icons/fa";
import {useDeleteStoryMutation} from "../../Redux/services/storyApi.js";
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";
import toast from "react-hot-toast";

// eslint-disable-next-line react/prop-types
function StoryViewer({ groups, currentUserIndex, currentStoryIndex, onClose }) {
    const [userIndex, setUserIndex] = useState(currentUserIndex);
    const [storyIndex, setStoryIndex] = useState(currentStoryIndex);
    const [progress, setProgress] = useState(0);
    const intervalRef = useRef();
    const { data: currentUser } = useGetCurrentUserQuery(null, {
        skip: !localStorage.getItem('isLoggedIn'),
    });
    const [deleteStory, { isLoading: isDeleting }] = useDeleteStoryMutation();

    const group = groups[userIndex] || { user: '', stories: [] };
    const stories = group.stories;
    const story = stories[storyIndex];

    const nextStory = () => {
        setProgress(0);
        if (storyIndex < stories.length - 1) {
            setStoryIndex(i => i + 1);
        } else nextUser();
    };
    const prevStory = () => {
        setProgress(0);
        if (storyIndex > 0) {
            setStoryIndex(i => i - 1);
        } else prevUser();
    };
    const nextUser = () => {
        setProgress(0);
        if (userIndex < groups.length - 1) {
            setUserIndex(i => i + 1);
            setStoryIndex(0);
        } else onClose();
    };
    const prevUser = () => {
        setProgress(0);
        if (userIndex > 0) {
            const prevGroup = groups[userIndex - 1];
            setUserIndex(i => i - 1);
            setStoryIndex((prevGroup.stories.length || 1) - 1);
        } else onClose();
    };

    useEffect(() => {
        clearInterval(intervalRef.current);
        intervalRef.current = setInterval(() => {
            setProgress(p => {
                if (p >= 100) {
                    nextStory();
                    return 0;
                }
                return p + 2;
            });
        }, 100);
        return () => clearInterval(intervalRef.current);
    }, [userIndex, storyIndex]);


    const handleDelete = async () => {
        if (!window.confirm('Delete this story?')) return;
        try {
            await deleteStory(story.id).unwrap();
            toast.success('Story deleted successfully.');
            onClose();
        } catch (err) {
            toast.error(err.data.businessErrornDescription);
        }
    };

    if (!stories.length) {
        return (
            <div className="story-viewer-modal">
                <button className="close-button" onClick={onClose}>×</button>
                <p>No stories</p>
            </div>
        );
    }


    return (
        <div className="story-viewer-modal">
            <button className="close-button" onClick={onClose}>×</button>


            <button className="nav-button left" onClick={prevStory}>
                <FaChevronLeft />
            </button>

            <div className="story-content">
                {group.user === currentUser?.nickName && (
                    <button
                        className="delete-button"
                        onClick={handleDelete}
                        disabled={isDeleting}
                        title="Delete your story"
                    >
                        <FaTrash />
                    </button>
                )}
                <div className="progress-bar">
                    <div className="progress" style={{ width: `${progress}%` }} />
                </div>
                <img src={story.image} alt="" />
                <p className="user-name">{group.user}</p>
            </div>

            <button className="nav-button right" onClick={nextStory}>
                <FaChevronRight />
            </button>
        </div>
    );
}

export { StoryViewer }
