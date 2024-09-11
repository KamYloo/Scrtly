import React, {useRef, useState} from 'react';
import { FaChevronRight, FaChevronLeft } from "react-icons/fa";
import {useSelector} from "react-redux";
import {BASE_API_URL} from "../../config/api.js";
import {AddStory} from "./AddStory.jsx";
import {StoryViewer} from "./StoryViewer.jsx";

function Stories() {

  const storyBoxRef = useRef(null)
  const [addStory, setAddStory] = useState(false)
  const [showViewer, setShowViewer] = useState(false);
    const [currentUserIndex, setCurrentUserIndex] = useState(0);

  const {auth} = useSelector(store => store);

    const stories = [
        {
            id: 1,
            username: "Oscar",
            storyImages: [
                "https://i.insider.com/5ddc0ddcfd9db217f85f4c1a?width=1000&format=jpeg&auto=webp",
                "https://www.gannett-cdn.com/presto/2021/03/22/NRCD/9d9dd9e4-e84a-402e-ba8f-daa659e6e6c5-PhotoWord_003.JPG?crop=1999,1125,x0,y78&width=2560",
                "https://example.com/image3.jpg"
            ]
        },
        {
            id: 2,
            username: "Alice",
            storyImages: [
                "https://www.gannett-cdn.com/presto/2021/03/22/NRCD/9d9dd9e4-e84a-402e-ba8f-daa659e6e6c5-PhotoWord_003.JPG?crop=1999,1125,x0,y78&width=2560",
                "https://i.insider.com/5ddc0ddcfd9db217f85f4c1a?width=1000&format=jpeg&auto=webp"
            ]
        },
    ];


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

    const handleStoryClick = (index) => {
        setCurrentUserIndex(index)
        setShowViewer(true)
    };

  return (
      <div className='stories'>
        <button className="scroll-button left" onClick={handleScrollLeft}>
          <FaChevronLeft />
        </button>
        <div className="box" ref={storyBoxRef}>
          <div className="story add-story" onClick={() => setAddStory(((prev) => !prev))}>
            <img src={`${BASE_API_URL}/${auth.reqUser?.profilePicture || ''}`} alt="Add story" />
            <span>+ Story</span>
          </div>
            {stories.map((story, index) => (
                <div className="story" key={story.id} onClick={() => handleStoryClick(index)}>
                    <img src={story.image} alt={story.username} />
                    <span>{story.username}</span>
                </div>
            ))}
        </div>
        <button className="scroll-button right" onClick={handleScrollRight}>
          <FaChevronRight />
        </button>
          {addStory && <AddStory onClose={() => setAddStory(((prev) => !prev))}/>}
          {showViewer && (
              <StoryViewer
                  stories={stories}
                  currentUserIndex={currentUserIndex}
                  onClose={() => setShowViewer(false)}
              />
          )}
      </div>
  );
}

export { Stories };
