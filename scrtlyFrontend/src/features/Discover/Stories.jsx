import React, {useEffect, useRef, useState} from 'react';
import { FaChevronRight, FaChevronLeft } from "react-icons/fa";
import {useDispatch, useSelector} from "react-redux";
import {AddStory} from "./AddStory.jsx";
import {StoryViewer} from "./StoryViewer.jsx";
import {getFollowedUsersStory} from "../../Redux/Story/Action.js";
import defaultAvatar from "../../assets/user.jpg";


function Stories() {
  const storyBoxRef = useRef(null)
  const [addStory, setAddStory] = useState(false)
  const [showViewer, setShowViewer] = useState(false);
  const [currentUserIndex, setCurrentUserIndex] = useState(0);
  const [currentStoryIndex, setCurrentStoryIndex] = useState(0);
  const dispatch = useDispatch()
  const { reqUser } = useSelector(state => state.auth);
  const {story} = useSelector(store => store);

    const handleScrollLeft = () => {
    storyBoxRef.current.scrollBy({
      left: -400,
      behavior: 'smooth'
    })
  }

  const handleScrollRight = () => {
    storyBoxRef.current.scrollBy({
      left: 400,
      behavior: 'smooth'
    })
  }

    const handleStoryClick = (userIndex, storyIndex) => {
        setCurrentUserIndex(userIndex)
        setCurrentStoryIndex(storyIndex)
        setShowViewer(true)
    };

    useEffect(() => {
        dispatch(getFollowedUsersStory())
    }, [dispatch])

  return (
      <div className='stories'>
        <button className="scroll-button left" onClick={handleScrollLeft}>
          <FaChevronLeft />
        </button>
        <div className="box" ref={storyBoxRef}>
          <div className="story add-story" onClick={() => setAddStory(((prev) => !prev))}>
            <img src={reqUser?.profilePicture || defaultAvatar} alt="Add story" />
            <span>+ Story</span>
          </div>
            {Object.entries(story.stories).map(([user, stories], userIndex) => (
                <div className="story" key={userIndex} onClick={() => handleStoryClick(userIndex, 0)}>
                    <img src={stories[0]?.user?.profilePicture || defaultAvatar} alt={stories[0]?.user?.fullName || ''} />
                    <span>{stories[0]?.user?.fullName || ''}</span>
                </div>
            ))}
        </div>
        <button className="scroll-button right" onClick={handleScrollRight}>
          <FaChevronRight />
        </button>
          {addStory && <AddStory onClose={() => setAddStory(((prev) => !prev))}/>}
          {showViewer && (
              <StoryViewer
                  stories={story.stories}
                  currentUserIndex={currentUserIndex}
                  currentStoryIndex={currentStoryIndex}
                  onClose={() => setShowViewer(false)}
              />
          )}
      </div>
  );
}

export { Stories };
