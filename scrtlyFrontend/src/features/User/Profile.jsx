import React, {useEffect, useState} from 'react'
import "../../Styles/Profile.css"
import { ProfileInfo } from './ProfileInfo.jsx'
import {Post} from "../Discover/Post.jsx";
import {useParams} from "react-router-dom";
import Spinner from "../../Components/Spinner.jsx";
import ErrorOverlay from "../../Components/ErrorOverlay.jsx";
import {useGetPostsByUserQuery} from "../../Redux/services/postApi.js";

function Profile() {
    const [selectedPost, setSelectedPost] = useState(false)
    const [postDetail, setPostDetail] = useState(null)
    const {nickName} = useParams();
    const {
        data: postsPage,
        isLoading,
        isError,
        error,
    } = useGetPostsByUserQuery(nickName, {
    });

    const togglePost = (post = null) => {
        setPostDetail(post)
        setSelectedPost((prev) => !prev);
    };

    if (isLoading) {
        return <Spinner />;
    }
    if (isError) {
        return <ErrorOverlay error={error} />;
    }

    const posts = postsPage?.content || [];

    return (
        <div className='profileSite'>
            <ProfileInfo  />
            <hr className="lineP"/>
            <div className="posts">
                {posts.map((item) => (
                    <div className="post" key={item.id} onClick={() => togglePost(item)}>
                        <img src={item.image} alt=""/>
                    </div>
                ))}
            </div>
            {selectedPost && <Post post={postDetail} onClose={togglePost}/>}
        </div>
    )
}

export {Profile}