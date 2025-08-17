import React, {useRef, useState} from 'react';
import { FaChevronRight, FaChevronLeft } from "react-icons/fa";
import {AddStory} from "./AddStory.jsx";
import {StoryViewer} from "./StoryViewer.jsx";
import defaultAvatar from "../../assets/user.jpg";
import {useGetCurrentUserQuery} from "../../Redux/services/authApi.js";
import {useGetFollowedStoriesQuery, useGetUserStoriesQuery} from "../../Redux/services/storyApi.js";


function Stories() {
  const storyBoxRef = useRef(null)
  const [addStory, setAddStory] = useState(false)
  const [showViewer, setShowViewer] = useState(false);
  const [currentUserIndex, setCurrentUserIndex] = useState(0);
  const [currentStoryIndex, setCurrentStoryIndex] = useState(0);
    const { data: reqUser } = useGetCurrentUserQuery(null, {
        skip: !localStorage.getItem('isLoggedIn'),
    });

    const {
        data: myStories = [],
    } = useGetUserStoriesQuery(undefined, {
        skip: !reqUser,
    });

    const {
        data: followedGroups = [],
    } = useGetFollowedStoriesQuery(undefined, {
        skip: !reqUser,
    });

    const groups = [];
    if (reqUser) {
        groups.push({
            user: reqUser.nickName,
            stories: myStories,
        });
    }
    followedGroups.forEach((g) => groups.push(g));

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
            {groups.map(({ user, stories }, uIdx) => {
                const first = stories[0];
                return (
                    <div
                        key={user}
                        className='story'
                        onClick={() => handleStoryClick(uIdx, 0)}
                    >
                        <img
                            src={first?.image || defaultAvatar}
                            alt={user}
                        />
                        <span>{user}</span>
                    </div>
                );
            })}
        </div>
          <button className='scroll-button right' onClick={handleScrollRight}>
              <FaChevronRight />
          </button>
          {addStory && <AddStory onClose={() => setAddStory(((prev) => !prev))}/>}
          {showViewer && (
              <StoryViewer
                  groups={groups}
                  currentUserIndex={currentUserIndex}
                  currentStoryIndex={currentStoryIndex}
                  onClose={() => setShowViewer(false)}
              />
          )}
      </div>
  );
}

export { Stories };
