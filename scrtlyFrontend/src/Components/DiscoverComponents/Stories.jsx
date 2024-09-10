import React, {useRef, useState} from 'react';
import { FaChevronRight, FaChevronLeft } from "react-icons/fa";
import {useSelector} from "react-redux";
import {BASE_API_URL} from "../../config/api.js";
import {AddStory} from "./AddStory.jsx";

function Stories() {

  const storyBoxRef = useRef(null)
  const [addStory, setAddStory] = useState(false)

  const {auth} = useSelector(store => store);

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
          {Array.from({ length: 13 }, (_, index) => (
              <div className="story" key={index}>
                <img src="" alt="" />
                <span>Oscar</span>
              </div>
          ))}
        </div>
        <button className="scroll-button right" onClick={handleScrollRight}>
          <FaChevronRight />
        </button>
          {addStory && <AddStory onClose={() => setAddStory(((prev) => !prev))}/>}
      </div>
  );
}

export { Stories };
